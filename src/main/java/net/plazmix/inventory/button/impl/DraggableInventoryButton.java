package net.plazmix.inventory.button.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.impl.DraggableButtonAction;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class DraggableInventoryButton implements BaseInventoryButton {

    private final ItemStack itemStack;

    private final DraggableButtonAction buttonAction;

}
