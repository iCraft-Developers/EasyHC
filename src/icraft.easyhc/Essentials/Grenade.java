package icraft.easyhc.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Grenade {

    public static void registerRecipes() {
        // Our custom variable which we will be changing around.
        ItemStack item = new ItemStack(Material.TNT);

    // The meta of the diamond sword where we can change the name, and properties of the item.
        ItemMeta meta = item.getItemMeta();

    // We will initialise the next variable after changing the properties of the sword


        // This sets the name of the item.
        // Instead of the § symbol, you can use ChatColor.<color>
        meta.setDisplayName("§aGranat zaczepny");

        // Set the meta of the sword to the edited meta.
        item.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("EasyHC"), "tnt");

        ShapedRecipe recipe = new ShapedRecipe(key, item);

        recipe.shape(" F ", "ITI", "PPP");

        recipe.setIngredient('F', Material.FLINT_AND_STEEL);
        recipe.setIngredient('T', Material.TNT);
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('P', Material.PRISMARINE_SHARD);
    }

}
