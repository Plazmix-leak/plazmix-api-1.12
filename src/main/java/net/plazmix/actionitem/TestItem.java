package net.plazmix.actionitem;

import lombok.NonNull;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.MinecraftRecipes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestItem extends AbstractActionItem {

    public TestItem() {
        super(ItemUtil.newBuilder(Material.STICK)
                .setName(ChatColor.YELLOW + "Палочка из говна")
                .build());
    }

    @Override
    public void handle(@NonNull ActionItem actionItem) {
        MinecraftRecipes.createRecipe("poop_stick", actionItem.getItemStack(), minecraftRecipeData -> {

            minecraftRecipeData.setCraftSlot(2, Material.BROWN_SHULKER_BOX);
            minecraftRecipeData.setCraftSlot(5, Material.STICK);
        });

        actionItem.setAttackHandler(event -> {

            if (event.getEntity() instanceof Player) {
                Player target = ((Player) event.getEntity());

                event.getDamager().sendMessage("Ты уебал " + target.getName() + " палкой из говна");
            }
        });
    }

}
