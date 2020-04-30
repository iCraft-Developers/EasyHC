package icraft.easyhc.Essentials;

import icraft.easyhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;

import static icraft.easyhc.Main.formatInfoAsMessage;

public class Giveaway implements Listener {
    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    public static Plugin plugin = Main.getPlugin(Main.class);
    public static HashMap<UUID, Integer> tasks;
    public static HashMap<UUID, Integer> playTime;

/*
    public Giveaway() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        try {
            Player p = e.getPlayer();
            playTime.put(p.getUniqueId(), 0);
            tasks.put(e.getPlayer().getUniqueId(), scheduler.scheduleSyncRepeatingTask(plugin, (Runnable) () -> {
                playTime.computeIfPresent(p.getUniqueId(), (k, v) -> v += 1);
                if (playTime.get(p.getUniqueId()) % 6 == 0) {
                    p.setExp(p.getExp() + 500);
                    p.sendMessage(formatInfoAsMessage("Otrzymales 500XP za godzine gry na serwerze."));
                } else {
                    p.setExp(p.getExp() + 50);
                    p.sendMessage(formatInfoAsMessage("Otrzymales 50XP za 10 minut gry na serwerze."));
                }
            }, 12000, 12000));
        } catch(NullPointerException exception){
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        scheduler.cancelTask(tasks.get(e.getPlayer().getUniqueId()));
        tasks.remove(e.getPlayer().getUniqueId());
    }
 */

}
