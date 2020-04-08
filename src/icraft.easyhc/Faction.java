package icraft.easyhc;

import icraft.easyhc.GUI.GUIPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

import static icraft.easyhc.Main.formatInfoAsMessage;

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

    public Faction(Main plugin){
        Main.pm.registerEvents(this, plugin);
    }

    public Faction(String tag, String name, UUID owner, Location2D heart, int level, boolean isNewlyCreated, UUID...members) throws Exception {
        this(tag, name, owner, heart, level, isNewlyCreated);
        addMembers(members);
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
        if(isNewlyCreated) DatabaseBuffer.buffer.add(new BufferAction(BufferAction.ActionType.ADD_FACTION, tag, name, owner.toString(), heart.getX(), heart.getZ(), 0));
        factions.put(tag, this);
    }

    void addMembers(UUID...uuid){
        this.members.addAll(members);
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

    public boolean isAtLocation(Location loc){
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        if(x >= pos1.getX() && z >= pos1.getZ() && x <= pos2.getX() && z <= pos2.getZ()) return true;
        return false;
    }

    public void remove() {

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

    public ArrayList<UUID> getMembers() {
        return members;
    }

















    @EventHandler
    public void onFactionEnter(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if(from == to) return;
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
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

            if(fromTag != null & toTag != null) break;
        }

        if(!Objects.equals(fromTag, toTag)) {
            if(toTag == null) new GUIPlayer(p).showTitle("§8Pustkowie", null, 5, 30, 5);
            if(toTag != null) new GUIPlayer(p).showTitle("§8" + toTag, "§8" + Faction.get(toTag).getName(), 5, 30, 5);
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
            Location loc = b.getLocation();
            for (Faction faction : Faction.getAll()) {
                if (faction.isAtLocation(loc)) {
                    new GUIPlayer(p).showTitle("§8[§6Gildie§8] §cTen teren nalezy do gildii: " + faction.getTag() + " - " + faction.getName(),5, 30, 5);
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }







}
