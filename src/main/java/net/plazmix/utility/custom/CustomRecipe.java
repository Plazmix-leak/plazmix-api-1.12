package net.plazmix.utility.custom;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Getter
@Deprecated
public abstract class CustomRecipe {

    public static final Collection<CustomRecipe> CUSTOM_RECIPE_COLLECTION = new ArrayList<>();

    private final String recipeKey;

    private final ItemStack recipeResult;
    private final ItemStack[] recipeCraft = new ItemStack[9];


// ================================================== // Передающиеся методы // ================================================ //

    public abstract void onRecipeCreate();

// ============================================== // Переопределяющиеся методы // ============================================== //

    public void onItemCraft(@NonNull Player player, @NonNull CraftItemEvent craftItemEvent) { }

// ============================================================================================================================= //


    public void setCraftSlot(int craftSlot, @NonNull ItemStack itemStack) {
        if (craftSlot > 9 || craftSlot < 1) {
            return;
        }

        recipeCraft[craftSlot - 1] = itemStack;
    }

    public void setCraftSlot(int craftSlot, @NonNull Material material) {
        if (craftSlot > 9 || craftSlot < 1) {
            return;
        }

        recipeCraft[craftSlot - 1] = new ItemStack(material);
    }

    public void setCraftSlot(int craftSlot, @NonNull MaterialData materialData) {
        if (craftSlot > 9 || craftSlot < 1) {
            return;
        }

        recipeCraft[craftSlot - 1] = materialData.toItemStack(1);
    }


    public void register(@NonNull Plugin plugin) {
        String[] recipeShape = new String[3];
        NamespacedKey namespacedKey = new NamespacedKey(plugin, recipeKey);

        if (Bukkit.getServer().getRecipesFor(recipeResult).stream().anyMatch(recipe -> (recipe instanceof ShapedRecipe) && ((ShapedRecipe) recipe).getKey().equals(namespacedKey))) {
            return;
        }

        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, recipeResult);
        onRecipeCreate();

        int shapeLineCounter = 0;
        for (int i = 0 ; i < 9 ; i++) {
            ItemStack itemStack = recipeCraft[i];
            char ingredientChar = Character.toChars('a' + i)[0];

            if (itemStack == null) {
                ingredientChar = ' ';
            }

            String shapeLine = recipeShape[shapeLineCounter];
            recipeShape[shapeLineCounter] = (shapeLine == null ? "" : shapeLine) + ingredientChar;

            if ((i + 1) % 3 == 0) {
                shapeLineCounter++;
            }
        }

        shapedRecipe.shape(recipeShape);

        shapeLineCounter = 0;
        for (int i = 0; i < 9 ; i++) {

            if (i > 0 && i % 3 == 0) {
                shapeLineCounter++;
            }

            String shapeLine = recipeShape[shapeLineCounter];
            char ingredientChar = shapeLine.charAt(i - (shapeLineCounter * 3));

            if (ingredientChar == ' ') {
                continue;
            }

            shapedRecipe.setIngredient(ingredientChar, recipeCraft[i].getData());
        }

        Bukkit.getServer().addRecipe(shapedRecipe);
        CUSTOM_RECIPE_COLLECTION.add(this);
    }
}
