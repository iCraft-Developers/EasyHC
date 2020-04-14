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

public class Giveaway extends Main implements Listener {
    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    public static HashMap<UUID, Integer> schedulers;
    public static HashMap<UUID, Integer> playTime;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        playTime.put(p.getUniqueId(), 0);
        schedulers.put(e.getPlayer().getUniqueId(), scheduler.scheduleSyncRepeatingTask(this, (Runnable) () -> {
            playTime.computeIfPresent(p.getUniqueId(), (k, v) -> v + 1);
            if(playTime.get(p.getUniqueId()) % 6 == 0){
                p.setExp(p.getExp() + 500);
                p.sendMessage(formatInfoAsMessage("Otrzymales 500XP za godzine gry na serwerze."));
            } else {
                p.setExp(p.getExp() + 50);
                p.sendMessage(formatInfoAsMessage("Otrzymales 50XP za 10 minut gry na serwerze."));
            }
        }, 12000, 12000));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        scheduler.cancelTask(schedulers.get(e.getPlayer().getUniqueId()));
        schedulers.remove(e.getPlayer().getUniqueId());
    }

}
