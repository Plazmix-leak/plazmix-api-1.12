package net.plazmix.inventory.button.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.inventory.button.BaseInventoryButton;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public class SimpleInventoryButton implements BaseInventoryButton {

    private final ItemStack itemStack;

    @Override
    public IInventoryButtonAction getButtonAction() {
        return null;
    }
}
