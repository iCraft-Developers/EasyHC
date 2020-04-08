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

import java.util.HashMap;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Menu implements InventoryHolder, Listener {
    // Create a new inventory, with "this" owner for comparison with other inventories, a size of nine, called example
    private Inventory inv;
    private static HashMap<String, Menu> menus = new HashMap<>();

    public Menu(String name, String title, int rows, HashMap<Integer, ItemStack> options) {
        this(name, title, rows);
        for(Integer slot : options.keySet()){
            addOption(slot, options.get(slot));
        }
        Main.pm.registerEvents(this, getPlugin(Main.class));
    }



    public Menu(String name, String title, int rows, ItemStack...options) {
        this(name, title, rows);
        addOption(options);
        Main.pm.registerEvents(this, getPlugin(Main.class));
    }




    public Menu(String name, String title, int rows) {
        inv = Bukkit.createInventory(this, rows * 9, title);
        menus.put(name, this);
    }




    @Override
    public Inventory getInventory() {
        return inv;
    }




    // You can call this whenever you want to put the items in
    public void addOption(int slot, ItemStack itemStack) {
        //"Example Sword", "works", "§aFirst line of the lore", "§bSecond line of the lore"
        inv.setItem(slot, itemStack);
        //inv.addItem(new ItemStack(Material.IRON_HELMET,1, "§bExample Helmet", "§aFirst line of the lore", "§bSecond line of the lore"));
    }



    public void addOption(ItemStack...itemStack){
        int slot = 0;
        for(ItemStack is : itemStack){
            inv.setItem(slot, is);
            slot++;
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
    public void GUIOptionClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) {
            return;
        }
        if (e.getClick().isLeftClick()) {
            Player p = (Player) e.getWhoClicked();
            ItemStack clickedItem = e.getCurrentItem();
            int clickedSlot = e.getRawSlot();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            p.sendMessage("You clicked at slot " + clickedSlot);
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
