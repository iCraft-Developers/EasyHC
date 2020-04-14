package icraft.easyhc;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import icraft.gui.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static icraft.easyhc.Main.formatInfoAsMessage;
import static icraft.easyhc.Main.setServerError;

public class Faction implements Listener {
    private static HashMap<String, Faction> factions = new HashMap<>();

    private String tag;
    private String name;
    private Location2D heart;
    private int level;
    private UUID owner;
    private ArrayList<UUID> members;
    private Location2D pos1;
    private Location2D pos2;
    private double hp;
    private BossBar bossbar;

    public Faction(Main plugin){
        Main.pm.registerEvents(this, plugin);
    }

    public Faction(String tag, String name, UUID owner, Location2D heart, int level, boolean isNewlyCreated, UUID...members) throws Exception {
        this(tag, name, owner, heart, level, isNewlyCreated);
        addMembers(members);
    }


    public BossBar getBossbar() {
        return bossbar;
    }

    public Faction(String tag, String name, UUID owner, Location2D heart, int level, boolean isNewlyCreated) throws Exception {
        members = new ArrayList<>();
        pos1 = new Location2D(heart.getX() - 15 - (level * 5), heart.getZ() - 15 - (level * 5));
        pos2 = new Location2D(heart.getX() + 15 + (level * 5), heart.getZ() + 15 + (level * 5));
        this.tag = tag;
        this.name = name;
        this.owner = owner;
        this.level = level;
        this.heart = heart;
        factions.put(tag, this);
        this.hp = (this.level + 1) * 100.0;
        this.bossbar = Bukkit.createBossBar("Gildia " + this.tag + " - " + this.name + "    " + ((int)this.hp) + "/" + ((this.level + 1) * 100) + "HP" + " (Poziom " + this.level + ")", BarColor.RED, BarStyle.SEGMENTED_10, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
        if(isNewlyCreated) {
            DatabaseBuffer.buffer.add(new BufferAction(BufferAction.ActionType.ADD_FACTION, tag, name, owner.toString(), heart.getX(), heart.getZ(), 0));
            this.createHeart();
        }
        this.bossbar.setProgress(this.hp / ((this.level + 1) * 100));
    }

    void addMembers(UUID...uuids){
        this.members.addAll(Arrays.asList(uuids));
    }

    String getTag(){
        return this.tag;
    }

    public Location2D getPos1(){
        return pos1;
    }


    public Location2D getPos2(){
        return pos2;
    }


    public String getName() {
        return name;
    }



    private void createHeart() throws Exception {
        File schematic = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("EasyHC")).getDataFolder().getAbsolutePath() + "/heart.schem");
        if(!schematic.exists()){
            setServerError();
            throw new Exception("Could not find file heart.schem in plugin's data folder!");
        }



        WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        assert we != null;
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        assert format != null;
        ClipboardReader reader = format.getReader(new FileInputStream(schematic));

        Clipboard clipboard = reader.read();

        try { //Pasting Operation
// We need to adapt our world into a format that worldedit accepts. This looks like this:
// Ensure it is using com.sk89q... otherwise we'll just be adapting a world into the same world.
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("EasyHC")));

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,
                    -1);

// Saves our operation and builds the paste - ready to be completed.
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(this.heart.getX(), 45, this.heart.getZ())).ignoreAirBlocks(false).build();

                Operations.complete(operation);
                editSession.flushSession();

            } catch (WorldEditException e) { // If worldedit generated an exception it will go here
                e.printStackTrace();
                setServerError();
            }
    }


    public Location2D getHeart() {
        return heart;
    }

    public static boolean exists(String tag){
        if(factions.containsKey(tag)) return true; else return false;
    }

    public boolean isAtLocation(Location loc){
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if(x >= pos1.getX() && z >= pos1.getZ() && x <= pos2.getX() && z <= pos2.getZ()) return true;
        return false;
    }

    public void remove() throws Throwable {
        Collection<Entity> entities = Bukkit.getWorld("EasyHC").getNearbyEntities(new Location(Bukkit.getWorld("EasyHC"), this.heart.getX(), 45, this.heart.getZ()), 3, 3, 3);
        for (Entity entity : entities) {
            if (entity.getType() == EntityType.ENDER_CRYSTAL) {
                entity.remove();
            }
        }

        this.bossbar.removeAll();

        factions.remove(this.tag);
        DatabaseBuffer.buffer.add(new BufferAction(BufferAction.ActionType.REMOVE_FACTION, "tag=\"" + this.tag + "\""));
        DatabaseBuffer.buffer.add(new BufferAction(BufferAction.ActionType.REMOVE_MEMBER, "tag=\"" + this.tag + "\""));
    }



    public void destroy() throws Throwable {
        Bukkit.getWorld("EasyHC").createExplosion(new Location(Bukkit.getWorld("EasyHC"), this.getHeart().getX(), 45, this.getHeart().getZ()),30F + (10 * (this.level + 1)));
        Bukkit.getServer().broadcastMessage(String.join("\n", formatInfoAsMessage("§cGildia " + this.tag + " - " + this.name + " zostala zniszczona!")));
        for(UUID uuid : this.members) {
            if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                new Title("§c" + "GILDIA ZNISZCZONA!", Title.Type.TITLE, 5, 30, 5).show(Bukkit.getPlayer(uuid));
                new Title("§c" + "Gildia do ktorej przynalezales zostala zniszczona!", Title.Type.SUBTITLE, 5, 30, 5).show(Bukkit.getPlayer(uuid));
            }
        }
        if(Bukkit.getPlayer(this.owner) != null && Bukkit.getPlayer(this.owner).isOnline()){
            new Title("§c" + "GILDIA ZNISZCZONA!", Title.Type.TITLE, 5, 30, 5).show(Bukkit.getPlayer(this.owner));
            new Title("§c" + "Twoja gildia zostala zniszczona!", Title.Type.SUBTITLE, 5, 30, 5).show(Bukkit.getPlayer(this.owner));
        }
        this.remove();
    }


    public void extend() throws Throwable {
        this.level++;
        factions.put(this.tag, new Faction(this.tag, this.name, this.owner, this.heart, this.level, false));
        DatabaseBuffer.buffer.add(new BufferAction(BufferAction.ActionType.EXTEND_FACTION, "level=" + this.level, "tag=\"" + this.tag + "\""));
    }

    public static Faction get(String tag){
        return factions.get(tag);
    }

    public static Collection<Faction> getAll(){
        return factions.values();
    }

    public UUID getOwner() {
        return owner;
    }

    ArrayList<UUID> getMembers() {
        return members;
    }

    void removeMember(UUID uuid) throws Exception {
        this.members.remove(uuid);
        DatabaseBuffer.buffer.add(new BufferAction(BufferAction.ActionType.REMOVE_MEMBER, "uuid=\"" + uuid.toString() + "\""));
    }


    public int getLevel() {
        return level;
    }











    @EventHandler
    public void onTeleportToFaction(PlayerTeleportEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        if(from == to) return;
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        checkIfEnteredOrLeavedFaction(p, from, to);
    }







    @EventHandler
    public void onFactionEnter(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if(from == to) return;
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        checkIfEnteredOrLeavedFaction(p, from, to);
    }





public void checkIfEnteredOrLeavedFaction(Player p, Location from, Location to){
    String fromTag = null;
    String toTag = null;

    for (Faction faction : Faction.getAll()) {
        if(fromTag == null && faction.isAtLocation(from)) {
            fromTag = faction.getTag();
            //p.sendMessage(formatInfoAsMessage("Opuszczasz teren gildii: " + key + " - " + factions.get(key).getName()));
        }

        if(toTag == null && faction.isAtLocation(to)) {
            toTag = faction.getTag();
            //p.sendMessage(formatInfoAsMessage("Wchodzisz na teren gildii: " + tag + " - " + faction.getName()));

        }

        if(fromTag != null && toTag != null) break;
    }

    if(!Objects.equals(fromTag, toTag)) {
        if(toTag == null) {
            new Title("§8Pustkowie", Title.Type.TITLE, 5, 30, 5).show(p);
            new Title("", Title.Type.SUBTITLE, 5, 30, 5).show(p);
            Faction.get(fromTag).bossbar.removePlayer(p);
        } else {
            new Title("§8" + toTag, Title.Type.TITLE, 5, 30, 5).show(p);
            new Title("§8" + Faction.get(toTag).getName(), Title.Type.SUBTITLE, 5, 30, 5).show(p);
            if(!Faction.get(toTag).isAllowed(p)) {
                Faction.get(toTag).bossbar.addPlayer(p);
            }
        }
    }
}








/*
    @EventHandler
    void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location loc = b.getLocation();
        for (Faction faction : Faction.getAll()) {
            if (faction.isAtLocation(loc)) {
                p.sendMessage(formatInfoAsMessage("Ten teren nalezy do gildii: " + faction.getTag() + " - " + faction.getName()));
                e.setCancelled(true);
                return;
            }
        }
    }



    @EventHandler
    void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location loc = b.getLocation();
        for (Faction faction : Faction.getAll()) {
            if (faction.isAtLocation(loc)) {
                p.sendMessage(formatInfoAsMessage("Ten teren nalezy do gildii: " + faction.getTag() + " - " + faction.getName()));
                e.setCancelled(true);
                return;
            }
        }
    }
*/

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
            Block b = e.getClickedBlock();
            assert b != null;
            Location loc = b.getLocation();
            for (Faction faction : Faction.getAll()) {
                if (faction.isAtLocation(loc)) {
                    if(e.isBlockInHand()){
                        b = b.getRelative(e.getBlockFace());
                        loc = b.getLocation();
                        if(!faction.isAtLocation(loc)){
                            for (Faction f : Faction.getAll()) {
                                if (f.isAtLocation(loc)) {
                                    faction = f;
                                    break;
                                }
                            }
                        }
                    }
                    if(!p.getUniqueId().equals(faction.owner) && !faction.members.contains(p.getUniqueId())) {
                        new Title("§8[§6Gildie§8] §cTen teren nalezy do gildii: " + faction.tag + " - " + faction.getName(), Title.Type.ACTIONBAR, 5, 30, 5).show(p);
                        e.setCancelled(true);
                    } else if(loc.getBlockX() > faction.getHeart().getX() - 4 && loc.getBlockX() < faction.getHeart().getX() + 4 && loc.getBlockY() > 41 && loc.getY() < 49 && loc.getBlockZ() > faction.getHeart().getZ() - 4 && loc.getBlockZ() < faction.getHeart().getZ() + 4) {
                        new Title("§8[§6Gildie§8] §cNie mozesz modyfikowac serca gildii", Title.Type.ACTIONBAR, 5, 30, 5).show(p);
                        e.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }



    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
        Player p = e.getPlayer();
        for(Faction f : Faction.getAll()){
            if(f.isAtLocation(e.getRightClicked().getLocation())){
                if(!f.isAllowed(p)){
                    new Title("§8[§6Gildie§8] §cNie mozesz wchodzic w interacje", Title.Type.ACTIONBAR, 5, 30, 5).show(p);
                    e.setCancelled(true);
                }
                break;
            }
        }
    }



    //TODO: Dodac blokade dodawania endercystalow
    //TODO: Zrobic zabezpieczenie zeby pluginy nie niszczyly serca jak np komenda /mobkill

    /*
    if(e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        e.setCancelled(true);
                    }
     */

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) throws Throwable {
        if(!(e.getEntity() instanceof Monster)) {
            for (Faction f : factions.values()) {
                if(e.getEntity().getType() == EntityType.ENDER_CRYSTAL) {
                    EnderCrystal ec = (EnderCrystal) e.getEntity();
                    if(f.isAtLocation(e.getEntity().getLocation())) {
                        if (ec.getLocation().getBlockX() > f.getHeart().getX() - 4 && ec.getLocation().getBlockX() < f.getHeart().getX() + 4 && ec.getLocation().getBlockY() > 41 && ec.getLocation().getY() < 49 && ec.getLocation().getBlockZ() > f.getHeart().getZ() - 4 && ec.getLocation().getBlockZ() < f.getHeart().getZ() + 4) {
                            if (e.getDamager().getType() == EntityType.PLAYER) {
                                Player p = (Player) e.getDamager();
                                if (f.isAllowed(p)) {
                                    new Title("§8[§6Gildie§8] §cNie mozesz zaatakowac serca gildii", Title.Type.ACTIONBAR, 5, 30, 5).show(p);
                                } else {
                                    f.hp -= e.getFinalDamage();
                                    if (!(f.hp > 0)) {
                                        f.destroy();
                                    } else {
                                        f.bossbar.setProgress(f.hp / ((f.level + 1) * 100));
                                        f.bossbar.setTitle("Gildia " + f.tag + " - " + f.name + "    " + ((int) f.hp) + "/" + ((f.level + 1) * 100) + "HP" + " (Poziom " + f.level + ")");
                                    }
                                }
                            } else {
                                f.hp -= e.getFinalDamage();
                                if (!(f.hp > 0)) {
                                    f.destroy();
                                } else {
                                    f.bossbar.setProgress(f.hp / ((f.level + 1) * 100));
                                    f.bossbar.setTitle("Gildia " + f.tag + " - " + f.name + "    " + ((int) f.hp) + "/" + ((f.level + 1) * 100) + "HP" + " (Poziom " + f.level + ")");
                                }
                            }
                            e.setDamage(0);
                            e.setCancelled(true);
                        }
                    }
                    break;
                } else {
                    if (e.getDamager().getType() == EntityType.PLAYER) {
                        if(f.isAtLocation(e.getEntity().getLocation())) {
                            Player p = (Player) e.getDamager();
                            if (!f.isAllowed(p)) {
                                new Title("§8[§6Gildie§8] §cMozesz atakowac tylko agresywne moby i serce gildii", Title.Type.ACTIONBAR, 5, 30, 5).show(p);
                                e.setCancelled(true);
                            }
                            break;
                        }
                    } else break;
                }
            }
        }
    }




    public boolean isMember(Player p) {
        if(this.members.contains(p.getUniqueId())) return true;
        return false;
    }

    public boolean isOwner(Player p){
        if(this.owner.equals(p.getUniqueId())) return true;
        return false;
    }

    public boolean isAllowed(Player p){
        if(this.isOwner(p) || this.isMember(p)) return true;
        return false;
    }


/*
    @EventHandler
    public void onHeartOtherAttacks(EntityDamageByBlockEvent){

    }

 */





    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        for(Faction f : getAll()){
            if(f.isAtLocation(p.getLocation())){
                if(!f.owner.equals(p.getUniqueId()) && !f.members.contains(p.getUniqueId())) {
                    f.bossbar.addPlayer(p);
                }
            }
        }

    }



}
