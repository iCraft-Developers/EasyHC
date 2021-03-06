package icraft.easyhc;

import icraft.easyhc.sql.SQLConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashSet;

import static icraft.easyhc.Main.serverError;
import static icraft.easyhc.Main.setServerError;

public class DatabaseBuffer {
    public static LinkedHashSet<BufferAction> buffer;


    public DatabaseBuffer(Main plugin) {
        buffer = new LinkedHashSet<>();
        new BukkitRunnable() {
            public void run() {
                try {
                    //ResultSet results = new SQLQuery(new Connection("", "", ""), "select * from users").results();
                    if(!buffer.isEmpty()) {
                        SQLConnection conn = new SQLConnection();
                        for (BufferAction action : buffer) {
                            conn.execute(action.getQuery());
                        }
                        conn.close();
                        buffer = new LinkedHashSet<>();
                        serverError = false;
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    setServerError();
                }
            }
        }.runTaskTimer(plugin, 600L,100L);
    }

}
