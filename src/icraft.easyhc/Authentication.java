package icraft.easyhc;

import icraft.easyhc.sql.SQLConnection;
import icraft.gui.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

import static icraft.easyhc.Main.pm;
import static org.bukkit.craftbukkit.libs.org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

public class Authentication implements Listener {
    public static ArrayList<UUID> unLoggedPlayers = new ArrayList<>();
    public static HashMap<UUID, String> waitingToConfirmPassword = new HashMap<>();

    public Authentication(){
        pm.registerEvents(this, Bukkit.getPluginManager().getPlugin("EasyHC"));
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        SQLConnection conn = new SQLConnection();
        ResultSet results = conn.getResults("select * from users where uuid='" + p.getUniqueId().toString() + "'");
        if(results.first()){
            new Title("§aZaloguj sie", Title.Type.TITLE, 5, 99999, 5).show(p);
        } else {
            new Title("§aZarejestruj sie", Title.Type.TITLE, 5, 99999, 5).show(p);
        }
        new Title("§aWprowadz haslo na czacie", Title.Type.SUBTITLE, 5, 99999, 5).show(p);
        conn.close();
        unLoggedPlayers.add(p.getUniqueId());
    }


    @EventHandler
    public void onPasswordEnter(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        try {
            if (unLoggedPlayers.contains(p.getUniqueId())) {
                String pass = e.getMessage();
                final MessageDigest digest = MessageDigest.getInstance(SHA_256);
                final byte[] hashbytes = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
                String sha_256hex = bytesToHex(hashbytes);

                SQLConnection conn = new SQLConnection();
                ResultSet results = conn.getResults("select password from users where uuid='" + p.getUniqueId().toString() + "'");
                if(results.first()) {
                    if (results.getString("password").equals(sha_256hex)) {
                        new Title("", Title.Type.TITLE, 0, 1, 0).show(p);
                        new Title("", Title.Type.SUBTITLE, 0, 1, 0).show(p);
                        unLoggedPlayers.remove(p.getUniqueId());
                        p.sendMessage("Zalogowano.");
                    } else {
                        p.sendMessage("Haslo nieprawidlowe.");
                    }
                } else {
                    if(!waitingToConfirmPassword.containsKey(p.getUniqueId())) {
                        if (pass.length() < 5 || !Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$").matcher(pass).matches()) {
                            p.sendMessage("Haslo musi zawierac co najmniej 5 znakow, 1 wielka litere, 1 cyfre i 1 znak specjalny!");
                        } else {
                            p.sendMessage("Wprowadz ponownie haslo.");
                            waitingToConfirmPassword.put(p.getUniqueId(), pass);
                        }
                    } else {
                        if(pass.equals(waitingToConfirmPassword.get(p.getUniqueId()))) {
                            conn.execute("insert into users values('" + p.getUniqueId().toString() + "','" + sha_256hex + "');");
                            new Title("", Title.Type.TITLE, 0, 1, 0).show(p);
                            new Title("", Title.Type.SUBTITLE, 0, 1, 0).show(p);
                            unLoggedPlayers.remove(p.getUniqueId());
                            p.sendMessage("Zarejestrowano!");
                        }
                    }
                }
                conn.close();
                e.setCancelled(true);
            }
        } catch (Exception exception){
            exception.printStackTrace();
            p.sendMessage("Wystapil nieoczekiwany blad. Prosimy o kontakt z administracja serwera.");
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws Exception {
        Player p = e.getPlayer();
        unLoggedPlayers.remove(p.getUniqueId());
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnloggedPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(unLoggedPlayers.contains(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobTriggerUnloggedPlayer(EntityTargetLivingEntityEvent e){
        LivingEntity livingEntity = e.getTarget();
        if(livingEntity instanceof Player){
            Player p = (Player) livingEntity;
            if(unLoggedPlayers.contains(p.getUniqueId())){
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onAttackUnloggedPlayer(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(entity instanceof Player){
            Player p = (Player) entity;
            if(unLoggedPlayers.contains(p.getUniqueId())){
                e.setCancelled(true);
            }
        }
    }


    //TODO: Zablokowac granaty.


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            Player p = (Player) e.getDamager();
            if(unLoggedPlayers.contains(p.getUniqueId())){
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerBurn(EntityCombustEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(unLoggedPlayers.contains(p.getUniqueId())){
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (unLoggedPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (unLoggedPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }


    //TODO: Dokonczyc!!!
    /*
    @EventHandler
    public void onInventory(InventoryMoveItemEvent e){
        if(unLoggedPlayers.contains(((Player)(e.get)).getUniqueId())){
            e.setCancelled(true);
        }
    }
     */






    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
