package net.plazmix.advancement;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.advancement.api.AdvancementFrame;
import net.plazmix.advancement.api.CustomAdvancement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

@Getter
public class PlazmixAdvancement {

    protected final String namespace, namespaceKey, advancementKeyName;

    protected final CustomAdvancement customAdvancement;
    protected final PlazmixAdvancementMenu plazmixAdvancementMenu;


    public PlazmixAdvancement(@NonNull PlazmixAdvancementMenu plazmixAdvancementMenu,
                              @NonNull String parentKeyId,
                              @NonNull String minecraftIconMaterial,

                              @NonNull String advancementKeyName,
                              @NonNull String advancementTitle, @NonNull String advancementDescription) {

        this.advancementKeyName = advancementKeyName;

        this.namespace = plazmixAdvancementMenu.namespace;
        this.namespaceKey = advancementKeyName;

        this.plazmixAdvancementMenu = plazmixAdvancementMenu;

        this.customAdvancement = CustomAdvancement.builder(new NamespacedKey(namespace, namespaceKey))
                //.counter(counter)

                .title(ChatColor.GREEN + advancementTitle)
                .description(ChatColor.WHITE + advancementDescription)

                .icon("minecraft:" + minecraftIconMaterial.toLowerCase())

                .hidden(false)
                .toast(true)

                .background("minecraft:" + plazmixAdvancementMenu.getMenuBackgroundPath())

                .frame(AdvancementFrame.GOAL)
                .parent(namespace + ":" + parentKeyId)

                .build();
    }


    public void advance(@NonNull String playerName) {
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            return;
        }

        customAdvancement.grant(player);
    }


    public void progressUp(@NonNull Player player) {
        customAdvancement.counterUp(player);
    }

    public void progressDown(@NonNull Player player) {
        customAdvancement.counterDown(player);
    }

    public void progressReset(@NonNull Player player) {
        customAdvancement.counterReset(player);
    }


    public AdvancementProgress getPlayerProgress(@NonNull Player player) {
        return player.getAdvancementProgress(customAdvancement.getAdvancement());
    }

}
