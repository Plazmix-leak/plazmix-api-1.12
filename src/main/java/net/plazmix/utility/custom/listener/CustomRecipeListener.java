package net.plazmix.utility.custom.listener;

import net.plazmix.utility.custom.CustomRecipe;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CustomRecipeListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemCraft(CraftItemEvent event) {

        HumanEntity humanEntity = event.getWhoClicked();
        ItemStack itemStack = event.getRecipe().getResult();

        for (CustomRecipe customRecipe : CustomRecipe.CUSTOM_RECIPE_COLLECTION) {
            if (customRecipe.getRecipeResult().equals(itemStack)) {

                customRecipe.onItemCraft((Player) humanEntity, event);
            }
        }
    }
}
