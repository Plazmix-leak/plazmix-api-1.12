package net.plazmix.lobby.playertop;

import gnu.trove.map.TIntIntMap;
import lombok.*;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.holographic.line.EmptyHolographicLine;
import net.plazmix.holographic.updater.SimpleHolographicUpdater;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class PlayerTopsHolographic {

    private final String statsName;
    private final Location location;

    private ProtocolHolographic protocolHolographic;

    @Setter
    private TIntIntMap topDataMap;

    @Setter
    private String[] description = new String[0];


    public ProtocolHolographic create(@NonNull String valueSuffix) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        // Create holographic
        ProtocolHolographic protocolHolographic = new SimpleHolographic(location);
        protocolHolographic.addOriginalHolographicLine("§6§l§n" + statsName);

        for (String descriptionLine : description)
            protocolHolographic.addOriginalHolographicLine(ChatColor.GRAY + descriptionLine);

        protocolHolographic.addEmptyHolographicLine();

        // Update player statistics
        int statsLineIndex = description.length + 2;
        updateTops(statsLineIndex, protocolHolographic, valueSuffix);

        // Add animation & data updater.
        protocolHolographic.setHolographicUpdater(5, new SimpleHolographicUpdater(protocolHolographic) {
            private int currentLineIndex = 0;

            private int previousLineIndex;
            private String previousLineText;

            @Override
            public void accept(ProtocolHolographic protocolHolographic) {
                if (previousLineText != null) {
                    protocolHolographic.setOriginalHolographicLine(statsLineIndex + previousLineIndex, previousLineText);

                } else {

                    updateTops(statsLineIndex, protocolHolographic, valueSuffix);
                }

                ProtocolHolographicLine currentLine = protocolHolographic.getHolographicLine(statsLineIndex + currentLineIndex);

                if (currentLine != null && !(currentLine instanceof EmptyHolographicLine)) {
                    previousLineIndex = currentLineIndex;
                    previousLineText = currentLine.getLineText();

                    currentLine.setLineText("§e→ " + currentLine.getLineText().replace("   §f", "   §e") + " §e←");
                    currentLine.update();
                }

                currentLineIndex++;

                if (currentLineIndex > topDataMap.size()) {

                    currentLineIndex = 0;
                    previousLineText = null;
                }
            }
        });

        return this.protocolHolographic = protocolHolographic;
    }

    private void updateTops(int statsLineIndex, ProtocolHolographic protocolHolographic, String valueSuffix) {
        int place = 1;

        for (int playerId : Arrays.stream(topDataMap.keys())
                .boxed()
                .sorted(Collections.reverseOrder()) // сначала по id
                .sorted(Collections.reverseOrder(Comparator.comparing(playerId -> topDataMap.get(playerId)))) // потом по значениям
                .mapToInt(i -> i)
                .toArray()) {

            protocolHolographic.setOriginalHolographicLine(statsLineIndex + place - 1, lineFormat(place, playerId, valueSuffix));
            place++;
        }
    }

    private String lineFormat(int place, int playerId, String valueSuffix) {
        String value            = NumberUtil.spaced( topDataMap.get(playerId) );

        String playerName       = playerId > 0 ? NetworkManager.INSTANCE.getPlayerName(playerId) : "Не найдено";
        String playerDisplay    = playerId > 0 ? PlazmixUser.of(playerName).getDisplayName() : (ChatColor.RED + playerName);

        String playerSpaces     = StringUtils.repeat(" ", 20 - ChatColor.stripColor(playerDisplay).length());
        String valueSpaces      = StringUtils.repeat(" ", 15 - value.length());
        String placeSpaces      = StringUtils.repeat(" ", 4 - String.valueOf(place).length());

        return placeSpaces + place + ".  " + playerDisplay

                + playerSpaces
                + valueSpaces

                + (ChatColor.WHITE + value + " " + valueSuffix)
                + "   ";
    }

}
