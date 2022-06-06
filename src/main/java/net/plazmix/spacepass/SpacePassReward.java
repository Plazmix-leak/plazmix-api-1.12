package net.plazmix.spacepass;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.function.Consumer;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public class SpacePassReward {

    @NonNull int maxExperience;

    @NonNull String name;
    @NonNull MaterialData icon;

    @NonNull Consumer<Player> playerConsumer;
}
