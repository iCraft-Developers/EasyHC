package icraft.easyhc;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

import static icraft.easyhc.Main.formatInfoAsMessage;

public class FactionsCommand {

    public static LinkedHashMap<String,String> factionsArgs = new LinkedHashMap<String,String>();


    public static void stworz(Player p, String...args){
        try {
            for(Faction faction : Faction.getAll()){
                if(faction.getOwner().equals(p.getUniqueId())){
                    p.sendMessage(formatInfoAsMessage("Posiadasz juz wlasna gildie!"));
                    return;
                } else {
                    if(faction.getMembers().contains(p.getUniqueId())){
                        p.sendMessage(formatInfoAsMessage("Jestes czlonkiem innej gildii!"));
                        return;
                    }
                }
            }
            for (ItemStack itemStack : Main.itemsForFaction) {
                if (!p.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    p.sendMessage(formatInfoAsMessage("Nie posiadasz wymaganych przedmiotow do stworzenia gildii!"));
                    return;
                }
            }
            String tag = "";
            String name = "";
            if(args.length < 3){
                p.sendMessage(formatInfoAsMessage("Nie podales wszystkich argumentow!", "Poprawne uzycie: /g stworz <tag> <nazwa>"));
                return;
            } else if(args.length > 3) {
                p.sendMessage(formatInfoAsMessage("Podales zbyt duzo argumentow!", "Poprawne uzycie: /g stworz <tag> <nazwa>"));
                return;
            }

            if(args[1].length() < 3 || args[1].length() > 5){
                p.sendMessage(formatInfoAsMessage("Tag gildii musi miec od 3 do 5 znakow."));
                return;
            }
            if(args[2].length() < 5 || args[2].length() > 20) {
                p.sendMessage(formatInfoAsMessage("Nazwa gildii musi miec od 5 do 20 znakow."));
                return;
            }
            tag = args[1].toUpperCase();
            name = args[2];




            for (ItemStack itemStack : Main.itemsForFaction) {
                p.getInventory().removeItem(itemStack);
            }

            new Faction(tag, name, p.getUniqueId(), new Location2D(p.getLocation().getBlockX(), p.getLocation().getBlockZ()), 0, true);
            p.sendMessage(formatInfoAsMessage("Gildia zostala utworzona!"));


        } catch(Exception e) {
            e.printStackTrace();
            p.kickPlayer("Wystapil blad! Skontaktuj sie z administracja.");
            for (Player ps : Bukkit.getServer().getOnlinePlayers()) {
                ps.kickPlayer("Na serwerze wystapil krytyczny blad. Bardzo prosimy o powiadomienie o tym administracji.");
            }
            Bukkit.reload();
        }

        return;
    }


    public static void itemy(Player p, String...args){
        p.performCommand("itemy");
    }


    public static void menu(Player p, String...args) {

    }

    public static void zapros(Player p, String...args){

    }



    public static void akceptuj(Player p, String...args){

    }


    public static void odrzuc(Player p, String...args){

    }


    public static void rozszerz(Player p, String...args) throws Throwable{
        try {
            if (args.length > 1) {
                p.sendMessage(formatInfoAsMessage("Podales zbyt duzo argumentow!", "Poprawne uzycie: /g rozszerz"));
                return;
            }

            for (Faction faction : Faction.getAll()) {
                if (faction.getOwner().equals(p.getUniqueId())) {
                    if (!p.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 32 * (faction.getLevel() + 1))) {
                        p.sendMessage(formatInfoAsMessage("Nie posiadasz wymaganych przedmiotow aby roszerzyc gildie!"));
                        return;
                    }
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 32 * (faction.getLevel() + 1)));
                    faction.extend();
                    p.sendMessage(formatInfoAsMessage("Pomyslnie rozszerzyles swoja gildie!"));
                    return;
                }
            }

            p.sendMessage(formatInfoAsMessage("Nie posiadasz wlasnej gildii!"));

        } catch(Exception e) {
            e.printStackTrace();
            p.kickPlayer("Wystapil blad! Skontaktuj sie z administracja.");
            for (Player ps : Bukkit.getServer().getOnlinePlayers()) {
                ps.kickPlayer("Na serwerze wystapil krytyczny blad. Bardzo prosimy o powiadomienie o tym administracji.");
            }
            Bukkit.reload();
        }
    }


    public static void usun(Player p, String...args) throws Throwable {
        try {
            if (args.length > 1) {
                p.sendMessage(formatInfoAsMessage("Podales zbyt duzo argumentow!", "Poprawne uzycie: /g usun"));
                return;
            }

            for (Faction faction : Faction.getAll()) {
                if (faction.getOwner().equals(p.getUniqueId())) {
                    faction.remove();
                    p.sendMessage(formatInfoAsMessage("Pomyslnie usunales swoja gildie!"));
                    return;
                }
            }

            p.sendMessage(formatInfoAsMessage("Nie posiadasz wlasnej gildii!"));

        } catch(Exception e) {
            e.printStackTrace();
            p.kickPlayer("Wystapil blad! Skontaktuj sie z administracja.");
            for (Player ps : Bukkit.getServer().getOnlinePlayers()) {
                ps.kickPlayer("Na serwerze wystapil krytyczny blad. Bardzo prosimy o powiadomienie o tym administracji.");
            }
            Bukkit.reload();
        }

    }


    public static void info(Player p, String...args){

    }



    public static void chat(Player p, String...args){

    }


    public static void zabezpiecz(Player p, String...args){

    }


}
