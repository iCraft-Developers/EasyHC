package icraft.easyhc.GUI.Chest;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class Option {
    private ItemStack itemStack;
    private String function;


    public Option(ItemStack itemStack, String function) {
        this.function = function;
        this.itemStack = itemStack;
    }


    public static void runFunction(String function, Player p) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Functions.class.getDeclaredMethod(function, Player.class);
        method.invoke(null, p);
        //p.sendMessage(function);
    }


    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getFunction(){
        return function;
    }
}
