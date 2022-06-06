package net.plazmix.listener;

import net.plazmix.event.PlazmixLevelChangeEvent;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelListener implements Listener {

    @EventHandler
    public void onLevelChange(PlazmixLevelChangeEvent event) {
        Player player = event.getPlayer();

        int newLevel = event.getNewLevel();
        int oldLevel = event.getOldLevel();

        if (newLevel <= oldLevel) {
            return;
        }

        PlazmixUser plazmixUser = PlazmixUser.of(player);

        // Chat message
        plazmixUser.localization().sendMessage(localizationResource -> localizationResource
                .getMessage("LVL_UP_MESSAGE")

                .replace("%new_level%", newLevel)
                .replace("%old_level%", oldLevel)
                .replace("%total_exp%", NumberUtil.spaced(plazmixUser.getMaxExperience() - plazmixUser.getExperience()))

                .toList());

        // Title message
        plazmixUser.localization().sendTitle(localizationResource -> localizationResource
                        .getMessage("LVL_UP_TITLE")

                        .replace("%new_level%", newLevel)
                        .replace("%old_level%", oldLevel)
                        .replace("%total_exp%", NumberUtil.spaced(plazmixUser.getMaxExperience() - plazmixUser.getExperience()))

                        .toText(),

                localizationResource -> localizationResource
                        .getMessage("LVL_UP_SUBTITLE")

                        .replace("%new_level%", newLevel)
                        .replace("%old_level%", oldLevel)
                        .replace("%total_exp%", NumberUtil.spaced(plazmixUser.getMaxExperience() - plazmixUser.getExperience()))

                        .toText());

        // Sounds.
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
    }

}
