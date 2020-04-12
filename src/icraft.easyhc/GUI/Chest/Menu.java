package icraft.easyhc.GUI.Chest;

import icraft.easyhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Objects;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Menu implements InventoryHolder, Listener {
    private Inventory inv;
    private static HashMap<String, Menu> menus = new HashMap<>();
    private HashMap<Integer, String> functions = new HashMap<>();


    public Menu(String name, String title, int rows, HashMap<Integer, Option> options) {
        this(name, title, rows);
        for(Integer slot : options.keySet()){
            addOption(slot, options.get(slot));
        }
        Main.pm.registerEvents(this, getPlugin(Main.class));
    }



    public Menu(String name, String title, int rows, Option...options) throws Exception {
        this(name, title, rows);
        addOption(options);
        Main.pm.registerEvents(this, getPlugin(Main.class));
    }




    public Menu(String name, String title, int rows) {
        inv = Bukkit.createInventory(this, rows * 9, title);
        menus.put(name, this);
        Main.pm.registerEvents(this, getPlugin(Main.class));
    }




    public Inventory getInventory() {
        return inv;
    }




    // You can call this whenever you want to put the items in
    public void addOption(int slot, Option option) {
        //"Example Sword", "works", "§aFirst line of the lore", "§bSecond line of the lore"
        inv.setItem(slot, option.getItemStack());
        functions.put(slot, option.getFunction());
        //inv.addItem(new ItemStack(Material.IRON_HELMET,1, "§bExample Helmet", "§aFirst line of the lore", "§bSecond line of the lore"));
    }



    public void addOption(Option...options) throws Exception {
        int slot = 0;
        for(Option option : options) {
            if(slot < inv.getSize()) {
                while (slot < inv.getSize() && inv.getItem(slot) != null) {
                    slot++;
                }
                if (inv.getItem(slot) == null) {
                    inv.setItem(slot, option.getItemStack());
                    if(option.getFunction() != null) functions.put(slot, option.getFunction());
                }
                slot++;
            } else {
                throw new Exception("Not enough space in inventory to add new option!");
            }
        }
    }



    // You can open the inventory with this
    public void show(Player p) {
        p.openInventory(inv);
    }


    public static Menu get(String name) {
        return menus.get(name);
    }

    // Check for clicks on items
    @EventHandler
    public void GUIOptionClick(InventoryClickEvent e) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (e.getInventory().getHolder() != this) {
            return;
        }
        if (e.getClick().isLeftClick()) {
            Player p = (Player) e.getWhoClicked();
            ItemStack clickedItem = e.getCurrentItem();
            int clickedSlot = e.getRawSlot();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if(functions.containsKey(clickedSlot)) {
                Option.runFunction(functions.get(clickedSlot), p);
            }

        }

        e.setCancelled(true);
        /*
        try {
            clickedItem.runFunction(p);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
         */
    }



}
