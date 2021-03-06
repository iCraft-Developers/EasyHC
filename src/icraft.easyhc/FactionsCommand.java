package icraft.easyhc;

import com.sk89q.worldedit.antlr4.runtime.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.UUID;

import static icraft.easyhc.Faction.factionChat;
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

            tag = args[1].toUpperCase();

            if(Faction.exists(tag)){
                p.sendMessage(formatInfoAsMessage("Ten tag gildii jest juz zajety przez inna gildie."));
                return;
            }

            if(args[2].length() < 5 || args[2].length() > 20) {
                p.sendMessage(formatInfoAsMessage("Nazwa gildii musi miec od 5 do 20 znakow."));
                return;
            }

            name = args[2];

            if(p.getLocation().getBlockY() < 50) {
                p.sendMessage(formatInfoAsMessage("Nie mozesz sie znajdowac ponizej 50 kratki."));
                return;
            }



            //TODO: Opracowac instrukcje warunkowa - min. 50 kratek od granicy innej gildii
            /*
            if(args[2].length() < 5 || args[2].length() > 20) {
                p.sendMessage(formatInfoAsMessage("Nie mozesz sie znajdowac na terenie innej gildii."));
                return;
            }

            for(Faction f : Faction.getAll()) {
                if(Math.abs(f.getPos1().getX() - p.getLocation().getBlockX()) > 49 && Math.abs(f.getPos1().getZ() - p.getLocation().getBlockZ()) > 49 && Math.abs(f.getPos2().getX() - p.getLocation().getBlockX()) > 49 && Math.abs(f.getPos2().getZ() - p.getLocation().getBlockZ()) > 49)
                p.sendMessage(formatInfoAsMessage("Musisz sie znajdowac co najmniej 50 kratek od granicy innej gildii."));
                return;
            }

             */




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
        //TODO: Blokada rozszerzania gdy wejdzie na inna gildie
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


    public static void info(Player p, String...args) {
        if (args.length > 2) {
            p.sendMessage(formatInfoAsMessage("Podales zbyt duzo argumentow!", "Poprawne uzycie: /g info [tag]"));
            return;
        }

        Faction f = null;
        if (args.length < 2) {
            for (Faction faction : Faction.getAll()) {
                if (faction.isAllowed(p)) {
                    f = faction;
                    break;
                }
            }
            if(f == null) {
                p.sendMessage(formatInfoAsMessage("Nie posiadasz ani nie jestes sojusznikiem zadnej gildii!"));
                return;
            }
        } else {
            if (Faction.factions.containsKey(args[1].toUpperCase())) {
                f = Faction.get(args[1].toUpperCase());
            } else {
                p.sendMessage(formatInfoAsMessage("Gildia o podanym tagu nie istnieje!"));
            }
        }

        if(f != null){
            ArrayList<String> members = new ArrayList<>();
            if(f.getMembers().size() > 0) {
                for (UUID uuid : f.getMembers()) {
                    members.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
            } else {
                members.add("Brak sojusznikow");
            }

            p.sendMessage(formatInfoAsMessage(1, "Informacje o gildii: " + f.getTag(), "Tag: " + f.getTag(), "Nazwa: " + f.getName(), "Poziom: " + f.getLevel(), "Koordynaty: " + f.getHeart().getX() + ", " + f.getHeart().getZ(), "Wlasciciel: " + Bukkit.getOfflinePlayer(f.getOwner()).getName(), "Sojusznicy: " + String.join(", ", members), "HP: " + (((int) (f.getHp() * 10)) / 10) + "/" + ((f.getLevel() + 1) * 100) + "HP"));
        }
    }



    public static void chat(Player p, String...args) {
        if (args.length > 1) {
            p.sendMessage(formatInfoAsMessage("Podales zbyt duzo argumentow!", "Poprawne uzycie: /g chat"));
            return;
        } else {
            if (factionChat.containsKey(p.getUniqueId())) {
                factionChat.remove(p.getUniqueId());
                p.sendMessage(formatInfoAsMessage("Od teraz piszesz na czacie globalnym."));
            } else {
                for (Faction f : Faction.getAll()) {
                    if (f.isAllowed(p)) {
                        factionChat.put(p.getUniqueId(), f.getTag());
                        p.sendMessage(formatInfoAsMessage("Od teraz piszesz na czacie gildijnym."));
                    }
                    return;
                }
                p.sendMessage(formatInfoAsMessage("Nie posiadasz ani nie jestes sojusznikiem zadnej gildii!"));
            }
        }
    }


    public static void zabezpiecz(Player p, String...args){

    }


}
