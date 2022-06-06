package net.plazmix.inventory.button.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.impl.ClickableButtonAction;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public class ActionInventoryButton implements BaseInventoryButton {

    private final ItemStack itemStack;

    private final ClickableButtonAction buttonAction;

}
