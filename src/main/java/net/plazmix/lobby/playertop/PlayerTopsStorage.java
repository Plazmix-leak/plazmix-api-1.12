package net.plazmix.lobby.playertop;

import gnu.trove.map.TIntIntMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.lobby.playertop.database.PlayerTopsDatabase;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class PlayerTopsStorage {

    public static PlayerTopsBuilder newBuilder() {
        return new PlayerTopsBuilder();
    }


    private PlayerTopsSkull skull;
    private PlayerTopsHolographic holographic;




    private PlayerTopsSkull createSkull(Location location,
                                        String texture,
                                        Particle skullParticle) {

        PlayerTopsSkull playerTopsSkull = new PlayerTopsSkull(location);

        playerTopsSkull.setParticle(skullParticle);
        playerTopsSkull.create(texture);

        return this.skull = playerTopsSkull;
    }

    private PlayerTopsHolographic createHolographic(Location location,
                                                    String statsName,
                                                    String valueSuffix,
                                                    TIntIntMap topDataMap,
                                                    String... description) {

        PlayerTopsHolographic playerTopsHolographic = new PlayerTopsHolographic(statsName, location);

        playerTopsHolographic.setTopDataMap(topDataMap);
        playerTopsHolographic.setDescription(description);

        playerTopsHolographic.create(valueSuffix);

        return this.holographic = playerTopsHolographic;
    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlayerTopsBuilder {

        private int limit;
        private int updaterPeriod;

        private boolean hasUpdater;

        private String statsName    = "Unknown stats";
        private String valueSuffix  = "";

        private String skullTexture;

        private String[] description = new String[0];

        private Location location;

        private Particle skullParticle;
        private PlayerTopsDatabase playerTopsDatabase;


        public PlayerTopsBuilder setDatabaseManager(PlayerTopsDatabase playerTopsDatabase) {
            this.playerTopsDatabase = playerTopsDatabase;

            return this;
        }

        public PlayerTopsBuilder setLimit(int limit) {
            this.limit = limit;

            return this;
        }

        public PlayerTopsBuilder setStatsName(String statsName) {
            this.statsName = statsName;

            return this;
        }

        public PlayerTopsBuilder setDescription(String... description) {
            this.description = description;

            return this;
        }

        public PlayerTopsBuilder setValueSuffix(String valueSuffix) {
            this.valueSuffix = valueSuffix;

            return this;
        }

        public PlayerTopsBuilder setSkullTexture(String skullTexture) {
            this.skullTexture = skullTexture;

            return this;
        }

        public PlayerTopsBuilder setSkullParticle(Particle skullParticle) {
            this.skullParticle = skullParticle;

            return this;
        }

        public PlayerTopsBuilder setLocation(Location location) {
            this.location = location;

            return this;
        }

        public PlayerTopsBuilder setUpdater(int secondsPeriod) {
            this.updaterPeriod = secondsPeriod;
            this.hasUpdater = true;

            return this;
        }

        public PlayerTopsStorage build(boolean publicSpawn) {
            if (playerTopsDatabase == null) {
                return null;
            }

            PlayerTopsStorage playerTopsStorage = new PlayerTopsStorage();
            TIntIntMap topDataMap = playerTopsDatabase.find(limit);

            if (!topDataMap.isEmpty()) {
                playerTopsStorage.createHolographic(location.clone().subtract(0, (limit * 0.3) - 0.45, 0), statsName, valueSuffix, topDataMap, description);

                if (hasUpdater) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            TIntIntMap topDataMap = playerTopsDatabase.find(limit);

                            playerTopsStorage.holographic.getTopDataMap().clear();
                            playerTopsStorage.holographic.getTopDataMap().putAll(topDataMap);

                        }

                    }.runTaskTimer(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), 0, 20L * updaterPeriod);
                }
            }

            if (skullTexture != null) {
                playerTopsStorage.createSkull(location.clone().add(0, 0.5, 0), skullTexture, skullParticle);
            }

            if (publicSpawn) {
                if (playerTopsStorage.skull != null)
                    playerTopsStorage.skull.getFakeGiantZombie().spawn();

                if (playerTopsStorage.holographic != null)
                    playerTopsStorage.holographic.getProtocolHolographic().spawn();
            }

            return playerTopsStorage;
        }

    }

}
