package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StatsCommand extends BaseCommand<Player> {

    public StatsCommand() {
        super("stats", "статистика", "стата");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        new StatsMenu().openInventory(player);
    }

    public static class StatsMenu
            extends BaseSimpleInventory {

        public StatsMenu() {
            super("Игровая статистика", 5);
        }

        @Override
        public void drawInventory(Player player) {
            PlazmixUser plazmixUser = PlazmixUser.of(player);

            setOriginalItem(5, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(plazmixUser.getName())

                    .setName("§bОбщая статистика §8§k§l|")

                    .addLore("")
                    .addLore("§8▪ §fСтатус: " + plazmixUser.getGroup().getColouredName())
                    .addLore("§8▪ §fSpacePass: " + (plazmixUser.getPass().isActivated() ? "§d§lПриобретен" : "§c§lНе приобретён"))
                    .addLore("")
                    .addLore("§8▪ §fМонет: §a" + NumberUtil.spaced(plazmixUser.getCoins()))
                    .addLore("§8▪ §fПлазмы: §6" + NumberUtil.spaced(plazmixUser.getGolds()))
                    .addLore("§8▪ §fУровень: §d" + NumberUtil.spaced(plazmixUser.getLevel()))
                    .addLore("")
                    .addLore("§8▪ §fЯзык: §7" + plazmixUser.getLanguage().getDisplayName())
                    .build());

            int MLGRDuelsWins = plazmixUser.getDatabaseValue("MLGRDuels", "wins");
            int MLGRDuelsKills = plazmixUser.getDatabaseValue("MLGRDuels", "kills");

            int SumoDuelsWins = plazmixUser.getDatabaseValue("SumoDuels", "wins");

            int NoDebuffWins = plazmixUser.getDatabaseValue("NoDebuff", "wins");

            int GAppleWins = plazmixUser.getDatabaseValue("GApple", "wins");

            int BuildUHCWins = plazmixUser.getDatabaseValue("BuildUHC", "wins");

            int BattleRushWins = plazmixUser.getDatabaseValue("BattleRush", "wins");

            int SpleefWins = plazmixUser.getDatabaseValue("Spleef", "wins");

            setOriginalItem(21, ItemUtil.newBuilder(Material.FISHING_ROD)
                    .setName("§eDuels §8§k§l|")

                    .addLore("")
                    .addLore("§eMLGRUSH:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(MLGRDuelsWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(MLGRDuelsKills))
                    .addLore("")
                    .addLore("§eSumo:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(SumoDuelsWins))
                    .addLore("")
                    .addLore("§eNoDebuff:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(NoDebuffWins))
                    .addLore("")
                    .addLore("§eGApple:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(GAppleWins))
                    .addLore("")
                    .addLore("§eBuildUHC:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(BuildUHCWins))
                    .addLore("")
                    .addLore("§eBattleRush:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(BattleRushWins))
                    .addLore("")
                    .addLore("§eSpleef:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(SpleefWins))
                    .build());

            setOriginalItem(24, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ0MDhjNTY5OGYyZDdhOGExNDE1ZWY5NTkyYWViNGJmNjJjOWFlN2NjZjE4ODQ5NzUzMGJmM2M4Yjk2NDhlNSJ9fX0=")

                    .setName("§2ArcadeGames §8§k§l|")

                    .addLore("")
                    .addLore("§2Build Battle:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("")
                    .addLore("§2Speed Builders:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SpeedBuilders", "wins")))
                    .addLore("§8▪ §fЛучшее время: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SpeedBuilders", "bestTime")))
                    .addLore("")
                    .addLore("§2Squid Game:")
                    .addLore("§8▪ §fПобед: §70")
                    .build());

            //int bedwarsSoloWins = (int) plazmixUser.getDatabaseValue("BedWarsSolo", "wins") + (int) plazmixUser.getDatabaseValue("SkywarsSolo", "wins");
            //int bedwarsSoloKills = (int) plazmixUser.getDatabaseValue("BedWarsSolo", "kills") + (int) plazmixUser.getDatabaseValue("SkywarsSolo", "kills");

            setOriginalItem(23, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiMjkwYTEzZGY4ODI2N2VhNWY1ZmNmNzk2YjYxNTdmZjY0Y2NlZTVjZDM5ZDQ2OTcyNDU5MWJhYmVlZDFmNiJ9fX0=")

                    .setName("§cBedWars §8§k§l|")

                    .addLore("")
                    .addLore("§cBedWars - §nОбщая статистика§c:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("§8▪ §fУбийств: §70")
                    .addLore("")
                    .addLore("§cBedWars Solo:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("§8▪ §fУбийств: §70")
                    .addLore("")
                    .addLore("§cBedWars Doubles:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("§8▪ §fУбийств: §70")
                    .build());

            int skywarsSoloWins = plazmixUser.getDatabaseValue("SkywarsSolo", "wins");
            int skywarsSoloKills = plazmixUser.getDatabaseValue("SkywarsSolo", "kills");

            int skywarsDoublesWins = plazmixUser.getDatabaseValue("SkywarsTeam", "wins");
            int skywarsDoublesKills = plazmixUser.getDatabaseValue("SkywarsTeam", "kills");

            int skywarsCrazyWins = plazmixUser.getDatabaseValue("SkywarsCrazy", "wins");
            int skywarsCrazyKills = plazmixUser.getDatabaseValue("SkywarsCrazy", "kills");

            int skywarsRankedWins = plazmixUser.getDatabaseValue("SkywarsRanked", "wins");
            int skywarsRankedKills = plazmixUser.getDatabaseValue("SkywarsRanked", "kills");
            int skywarsRankedRating = plazmixUser.getDatabaseValue("SkywarsRanked", "rating");


            setOriginalItem(22, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhiZThhYmQ2NmQwOWE1OGNlMTJkMzc3NTQ0ZDcyNmQyNWNhZDdlOTc5ZThjMjQ4MTg2NmJlOTRkM2IzMmYifX19")

                    .setName("§bSkyWars §8§k§l|")

                    .addLore("")
                    .addLore("§bSkyWars - §nОбщая статистика§b:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsSoloWins + skywarsDoublesWins + skywarsCrazyWins + skywarsRankedWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsSoloKills + skywarsDoublesKills + skywarsCrazyKills + skywarsRankedKills))
                    .addLore("§8▪ §fРейтиг: §7" + NumberUtil.spaced(skywarsRankedRating))
                    .addLore("")
                    .addLore("§bSkyWars Solo:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsSoloWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsSoloKills))
                    .addLore("")
                    .addLore("§bSkyWars Doubles:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsDoublesWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsDoublesKills))
                    .addLore("")
                    .addLore("§bSkyWars Crazy:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsCrazyWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsCrazyKills))
                    .addLore("")
                    .addLore("§bSkyWars Ranked:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsRankedWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsRankedKills))
                    .addLore("§8▪ §fРейтиг: §7" + NumberUtil.spaced(skywarsRankedRating))
                    .build());
        }
    }

}
