package icraft.easyhc.GUI.Chest;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Option extends ItemStack {
    private String function;


    public Option(ItemStack itemStack, String function) {
        this.function = function;
        ItemStack item = new ItemStack(itemStack.getType(), itemStack.getAmount());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemStack.getItemMeta().getDisplayName());

        List<String> metaLore = itemStack.getItemMeta().getLore();

        meta.setLore(metaLore);
        item.setItemMeta(meta);

    }


    public void runFunction(Player p) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Functions.class.getDeclaredMethod(this.function, Player.class);
        method.invoke(null, p);
    }



    public String getFunction(){
        return this.function;
    }
}
