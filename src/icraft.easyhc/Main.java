package icraft.easyhc;

import icraft.easyhc.Essentials.Grenade;
import icraft.gui.Chest.Menu;
import icraft.gui.Chest.Option;
import icraft.easyhc.sql.SQLConnection;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

//import static icraft.easyhc.Faction.factions;

public class Main extends JavaPlugin implements Listener {


    public static ItemStack[] itemsForFaction;
    public static PluginManager pm;
    ArrayList<String> argsCompletions;
    public static boolean serverError = false;


    public void onEnable() {
        pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        Grenade.registerRecipes();
        new RandomTP(this);
        new DatabaseBuffer(this);
        new Faction(this);


        FactionsCommand.factionsArgs.put("stworz", "§6Tworzy nowa gildie.");
        FactionsCommand.factionsArgs.put("itemy", "§6Wyswietla itemy potrzebne na gildie.");
        FactionsCommand.factionsArgs.put("menu", "§6Otwiera graficzne menu gildii.");
        FactionsCommand.factionsArgs.put("zapros", "§6Zaprasza gracza do gildii.");
        FactionsCommand.factionsArgs.put("akceptuj", "§6Akceptuje zaproszenie do gildii.");
        FactionsCommand.factionsArgs.put("odrzuc", "§6Odrzuca zaproszenie do gildii.");
        FactionsCommand.factionsArgs.put("rozszerz", "§6Rozszszerza obszar gildii.");
        FactionsCommand.factionsArgs.put("usun", "§6Usuwa gildie.");
        FactionsCommand.factionsArgs.put("info", "§6Wyswietla informacje o gildii.");
        FactionsCommand.factionsArgs.put("chat", "§6Zarzadza chatem gildii.");
        FactionsCommand.factionsArgs.put("zabezpiecz", "§6Umozliwia kupno dodatkowego zabezpieczenia gildii");

        argsCompletions = new ArrayList<>(FactionsCommand.factionsArgs.keySet());

        itemsForFaction = new ItemStack[]{new ItemStack(Material.DIAMOND, 64), new ItemStack(Material.EMERALD, 64), new ItemStack(Material.BOOK, 32)};


        Menu menu = new Menu("itemsForFaction", "Itemy potrzebne na gildie:", 1, this);
        for(ItemStack is : itemsForFaction){
            try {
                /*
                HashMap<Option.ClickType, String> fs = new HashMap<>();
                fs.put(Option.ClickType.LEFT, "works");
                menu.addOption(new Option(is, fs));
                 */
                menu.addOption(new Option(is, null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        //SQLConnection conn = new SQLConnection(null, null, null);
        try {
            //ResultSet results = new SQLQuery(new Connection("", "", ""), "select * from users").results();
            SQLConnection conn = new SQLConnection();
            ResultSet results = conn.getResults("select * from factions");
            while (results.next()) {
                new Faction(results.getString("tag"), results.getString("name"), UUID.fromString(results.getString("owner")), new Location2D(results.getInt("x"), results.getInt("z")), results.getInt("level"), false);
            }
            results = conn.getResults("select * from members");
            while (results.next()) {
                Faction faction = icraft.easyhc.Faction.get(results.getString("tag"));
                faction.addMembers(UUID.fromString(results.getString("uuid")));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            setServerError();
            new BukkitRunnable() {
                public void run() {
                    Bukkit.reload();
                }
            }.runTaskLater(this, 300L);
        }


    }

    public void onDisable() {

    }


    public static void setServerError() {
        setServerError();
    }


    public static String horizontalLine(int width, char character) {
        StringBuilder line = new StringBuilder();
        line.append("§c");
        for (int i = 0; i < width; i++) line.append(character);
        return line.toString();
    }

    public static String[] formatInfoAsMessage(String... info) {
        String[] formattedInfo = new String[info.length + 6];
        formattedInfo[0] = "";
        formattedInfo[1] = horizontalLine(50, '=');
        formattedInfo[2] = "";
        System.arraycopy(info, 0, formattedInfo, 3, info.length);
        formattedInfo[info.length + 3] = "";
        formattedInfo[info.length + 4] = horizontalLine(50, '=');
        formattedInfo[info.length + 5] = "";

        return formattedInfo;
    }

    public String[] formatInfoAsMessage(int t, String... info) {
        String[] infoWithoutTitle = new String[info.length - t];
        String[] title = new String[t];
        System.arraycopy(info, 0, title, 0, t);
        System.arraycopy(info, t, infoWithoutTitle, 0, info.length - t);
        String[] formattedInfoWithoutTitle = formatInfoAsMessage(infoWithoutTitle);
        String[] formattedTitle = new String[t+2];
        formattedTitle[0] = "";
        formattedTitle[1] = horizontalLine(50, '=');
        formattedTitle = (String[]) ArrayUtils.addAll(formattedTitle, title);
        return (String[]) ArrayUtils.addAll(formattedTitle, formattedInfoWithoutTitle);
    }


    public String[] formatFactionsArgumentsAsMessage() {
        String[] message = new String[icraft.easyhc.FactionsCommand.factionsArgs.size()];
        int index = 0;
        for (String arg : icraft.easyhc.FactionsCommand.factionsArgs.keySet()) {
            message[index] = "§2/gildia " + arg + " - " + icraft.easyhc.FactionsCommand.factionsArgs.get(arg);
            index++;
        }

        return message;
    }


    /*
    public String[] formatFactionItemsAsMessage() {
        String[] message = new String[itemsForFaction.length];
        for (int i = 0; i < itemsForFaction.length; i++) {
            message[i] = itemsForFaction[0].toString();
        }
        return message;
    }
     */



    @EventHandler
    public void onTabComplete(TabCompleteEvent e){
        if(e.getSender() instanceof Player) {
            Player p = (Player) e.getSender();
            String buffer = e.getBuffer();
            String[] args = buffer.split(" ");
            if (args[0].equalsIgnoreCase("/gildia") || args[0].equalsIgnoreCase("/g")) {
                if (args.length > 2) {
                    e.setCompletions(new ArrayList<>());
                    return;
                }
                if (args.length > 1) {
                    if(buffer.endsWith(" ")) {
                        e.setCompletions(new ArrayList<>());
                        return;
                    }
                    ArrayList<String> completions = new ArrayList<>();
                    for (String completion : argsCompletions) {
                        if (completion.startsWith(args[1].toLowerCase())) {
                            completions.add(completion);
                        }
                    }
                    e.setCompletions(completions);
                } else {
                    e.setCompletions(argsCompletions);
                }
            }
        }
    }




    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            switch (cmd.getName().toLowerCase()) {
                case "openinv":
                    if (args.length > 1) {
                        return false;
                    }
                    Player p2 = null;
                    if (args.length == 0) {
                        p2 = p;
                    } else {
                        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                            if (offlinePlayer.getPlayer().getName().equals(args[0])) {
                                p2 = offlinePlayer.getPlayer();
                                break;
                            }
                        }
                        if (p2 == null) {
                            p.sendMessage("Gracz " + args[0] + " nigdy nie gral na tym serwerze!");
                            return true;
                        }
                    }
                    Inventory inv = p2.getInventory();

                    p.openInventory(inv);


                    break;
                case "itemy":
                    //p.sendMessage(formatInfoAsMessage(new String[]{"Itemy potrzebne na gildie:"}, formatFactionItemsAsMessage()));
                    Menu.get("itemsForFaction").show(p);
                    //new Menu("xd", "Itemy potrzebne na gildie:", 1, this).show(p);
                    break;
                case "gildia":
                    if (args.length > 0 && icraft.easyhc.FactionsCommand.factionsArgs.containsKey(args[0].toLowerCase())) {
                        Method method;
                        try {
                            method = FactionsCommand.class.getDeclaredMethod(args[0].toLowerCase(), Player.class, String[].class);
                            method.invoke(null, p, args);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.sendMessage(formatInfoAsMessage(1, (String[]) ArrayUtils.add(formatFactionsArgumentsAsMessage(), 0, "Komendy dotyczace gildii:")));
                        //p.sendMessage(formatInfoAsMessage(formatFactionsArgumentsAsMessage()));
                        break;
                    }
                    break;
                case "gc":

                    break;
                case "randomtp":
                    RandomTP.teleportRandomly(p);
                    break;
            }
        }

        return true;
    }

    /*
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        int x = p.getLocation().getX();
        int z = p.getLocation().getZ();
        for (String tag : factions.keySet()) {
            Faction faction = factions.get(tag);
            int[] pos1 = faction.getPos1();
            int[] pos2 = faction.getPos2();
            if (faction.isAtLocation(to)) {
                location.put(p.getUniqueId(), tag);
            }
        }
    }
     */


    public void onJoin(PlayerLoginEvent e){
        if(serverError){
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "");
        }
    }



}