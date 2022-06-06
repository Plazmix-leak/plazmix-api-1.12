package net.plazmix.advancement;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.advancement.api.AdvancementFrame;
import net.plazmix.advancement.api.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PlazmixAdvancementMenu {

    protected final String namespace, namespaceKey,
            menuBackgroundPath;

    protected final CustomAdvancement customAdvancement;
    protected final Map<String, PlazmixAdvancement> tynixAdvancementMap = new HashMap<>();

    public static final List<PlazmixAdvancementMenu> TYNIX_ADVANCEMENT_MENU_LIST = new ArrayList<>();


    public PlazmixAdvancementMenu(@NonNull Material baseIconType,
                                  @NonNull String menuKeyName,
                                  @NonNull String menuTitle, @NonNull String menuDescription,
                                  @NonNull String menuBackgroundPath) {

        this.namespace = menuKeyName;
        this.namespaceKey = menuKeyName;

        this.menuBackgroundPath = menuBackgroundPath;

        this.customAdvancement = CustomAdvancement.builder(new NamespacedKey(namespace, namespaceKey))
                .title(menuTitle)
                .description(menuDescription)

                .icon("minecraft:" + baseIconType.name().toLowerCase())

                .hidden(false)
                .toast(false)

                .background("minecraft:" + menuBackgroundPath)
                .frame(AdvancementFrame.TASK)

                .build();

        TYNIX_ADVANCEMENT_MENU_LIST.add(this);
    }

    public PlazmixAdvancement addAdvancement(@NonNull PlazmixAdvancement plazmixAdvancement) {
        tynixAdvancementMap.put(plazmixAdvancement.advancementKeyName.toLowerCase(), plazmixAdvancement);

        return plazmixAdvancement;
    }

    public PlazmixAdvancement addAdvancement(@NonNull String minecraftIconMaterial,
                                             @NonNull String parentKeyId,

                                             @NonNull String advancementKeyName,
                                             @NonNull String advancementTitle, @NonNull String advancementDescription) {

        if (hasAdvancement(advancementKeyName)) {
            return getAdvancement(advancementKeyName);
        }

        PlazmixAdvancement plazmixAdvancement = new PlazmixAdvancement(this, parentKeyId, minecraftIconMaterial,
                advancementKeyName, advancementTitle, advancementDescription);

        return addAdvancement(plazmixAdvancement);
    }

    public PlazmixAdvancement addAdvancement(@NonNull Material baseIconType,
                                             @NonNull String parentKeyId,

                                             @NonNull String advancementKeyName,
                                             @NonNull String advancementTitle, @NonNull String advancementDescription) {

        return addAdvancement(baseIconType.name().toLowerCase(), parentKeyId, advancementKeyName,
                advancementTitle, advancementDescription);
    }

    public PlazmixAdvancement getAdvancement(@NonNull String advancementKeyName) {
        return tynixAdvancementMap.get(advancementKeyName.toLowerCase());
    }

    public boolean hasAdvancement(@NonNull String advancementKeyName) {
        return tynixAdvancementMap.containsKey(advancementKeyName.toLowerCase());
    }

}
