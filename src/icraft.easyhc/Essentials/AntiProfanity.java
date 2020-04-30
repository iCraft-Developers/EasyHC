package icraft.easyhc.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static icraft.easyhc.Main.pm;

public class AntiProfanity implements Listener {

    public static ArrayList<String> bannedWords = new ArrayList<>();


    public AntiProfanity() throws Exception {
        pm.registerEvents(this, Bukkit.getPluginManager().getPlugin("EasyHC"));
        File data = Bukkit.getServer().getPluginManager().getPlugin("EasyHC").getDataFolder();
        if(!data.exists()){
            if(data.mkdirs()) throw new Exception("Could not create data directory.");
        }

        File antiProfanityDir = new File(Bukkit.getServer().getPluginManager().getPlugin("EasyHC").getDataFolder() + File.separator + "AntiProfanity");
        if(!antiProfanityDir.exists()){
            if(antiProfanityDir.mkdir()) throw new Exception("Could not create directory.");
        }

        File[] listOfFiles = antiProfanityDir.listFiles();
        for(File file : listOfFiles){
            if(file.isFile()){
                file.read
            }
        }

    }



    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.getMessage().repla
    }
}
