package net.plazmix.holographic.impl;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.holographic.ProtocolHolographicUpdater;
import net.plazmix.holographic.line.*;
import net.plazmix.protocollib.entity.FakeEntityScope;
import net.plazmix.utility.player.LocalizationPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class SimpleHolographic implements ProtocolHolographic {

    private Location location;

    private ProtocolHolographicUpdater holographicUpdater;

    private final List<ProtocolHolographicLine> holographicLines = new LinkedList<>();

    private final Set<Player> receivers    = new LinkedHashSet<>();
    private final Set<Player> viewers      = new LinkedHashSet<>();


    public SimpleHolographic(Location location) {
        this.location = location;
    }


    @Override
    public ProtocolHolographicLine getHolographicLine(int lineIndex) {
        if (lineIndex >= holographicLines.size())
            return null;

        return holographicLines.get(lineIndex);
    }


    @Override
    public void setHolographicLine(int lineIndex, @NonNull ProtocolHolographicLine holographicLine) {
        if (lineIndex >= holographicLines.size()) {
            addHolographicLine(holographicLine);
            return;
        }

        ProtocolHolographicLine oldLine = getHolographicLine(lineIndex);
        if (oldLine != null && oldLine.getClass().equals(holographicLine.getClass())) {

            if (holographicLine.isLocalized()) {
                ((LangHolographicLine) oldLine).setMessageHandler(((LangHolographicLine) holographicLine).getMessageHandler());

            } else {

                oldLine.setLineText(holographicLine.getLineText());
                oldLine.update();
            }
            return;
        }

        holographicLines.set(lineIndex, holographicLine);

        holographicLine.initialize();

        if (oldLine != null) {

            Collection<Player> receiverCollection = oldLine.getFakeArmorStand().getReceiverCollection();
            oldLine.remove();

            if (oldLine.getFakeArmorStand().getEntityScope().equals(FakeEntityScope.PUBLIC)) {
                holographicLine.spawn();

            } else {

                holographicLine.addReceivers(receiverCollection.toArray(new Player[0]));
            }
        }
    }

    @Override
    public void setOriginalHolographicLine(int lineIndex, String holographicLine) {
        setHolographicLine(lineIndex, new SimpleHolographicLine(lineIndex, holographicLine, this));
    }

    @Override
    public void setLangHolographicLine(int lineIndex, Function<LocalizationPlayer, String> messageHandler) {
        setHolographicLine(lineIndex, new LangHolographicLine(lineIndex, messageHandler, this));
    }

    @Override
    public void setLangClickHolographicLine(int lineIndex, Function<LocalizationPlayer, String> messageHandler, Consumer<Player> clickAction) {
        setHolographicLine(lineIndex, new LangActionHolographicLine(lineIndex, messageHandler, this, clickAction));
    }

    @Override
    public void setClickHolographicLine(int lineIndex, String holographicLine, Consumer<Player> clickAction) {
        setHolographicLine(lineIndex, new ActionHolographicLine(lineIndex, holographicLine, this, clickAction));
    }

    @Override
    public void setHeadHolographicLine(int lineIndex, String headTexture, boolean small) {
        setHolographicLine(lineIndex, new HeadHolographicLine(lineIndex, headTexture, small, this));
    }

    @Override
    public void setItemHolographicLine(int lineIndex, ItemStack itemStack) {
        setHolographicLine(lineIndex, new ItemHolographicLine(lineIndex, itemStack, this));
    }

    @Override
    public void setEmptyHolographicLine(int lineIndex) {
        setHolographicLine(lineIndex, new EmptyHolographicLine(lineIndex, this));
    }


    @Override
    public void addHolographicLine(@NonNull ProtocolHolographicLine holographicLine) {
        holographicLine.initialize();

        holographicLines.add(holographicLine);

        if (isPublic) {
            holographicLine.spawn();

        } else {

            holographicLine.addReceivers(receivers.toArray(new Player[0]));
        }
    }

    @Override
    public void addOriginalHolographicLine(String holographicLine) {
        addHolographicLine(new SimpleHolographicLine(holographicLines.size(), holographicLine, this));
    }

    @Override
    public void addLangHolographicLine(Function<LocalizationPlayer, String> messageHandler) {
        addHolographicLine(new LangHolographicLine(holographicLines.size(), messageHandler, this));
    }

    @Override
    public void addLangClickHolographicLine(Function<LocalizationPlayer, String> messageHandler, Consumer<Player> clickAction) {
        addHolographicLine(new LangActionHolographicLine(holographicLines.size(), messageHandler, this, clickAction));
    }

    @Override
    public void addClickHolographicLine(String holographicLine, Consumer<Player> clickAction) {
        addHolographicLine(new ActionHolographicLine(holographicLines.size(), holographicLine, this, clickAction));
    }

    @Override
    public void addHeadHolographicLine(String headTexture, boolean small) {
        addHolographicLine(new HeadHolographicLine(holographicLines.size(), headTexture, small, this));
    }

    @Override
    public void addItemHolographicLine(ItemStack itemStack) {
        addHolographicLine(new ItemHolographicLine(holographicLines.size(), itemStack, this));
    }

    @Override
    public void addEmptyHolographicLine() {
        addHolographicLine(new EmptyHolographicLine(holographicLines.size(), this));
    }


    @Override
    public boolean hasReceiver(@NonNull Player player) {
        return receivers.contains(player);
    }

    @Override
    public void addReceivers(@NonNull Player... players) {
        for (Player player : players) {
            PlazmixApi.HOLOGRAPHIC_MANAGER.addProtocolHolographic(player, this);
        }

        receivers.addAll(Arrays.asList(players));
        addViewers(players);

        for (ProtocolHolographicLine holographicLine : holographicLines) {
            holographicLine.addReceivers(players);
        }
    }

    @Override
    public void removeReceivers(@NonNull Player... players) {
        for (Player player : players) {
            PlazmixApi.HOLOGRAPHIC_MANAGER.getPlayerHolographics().remove(player, this);
        }

        receivers.removeAll(Arrays.asList(players));
        removeViewers(players);

        for (ProtocolHolographicLine holographicLine : holographicLines) {
            holographicLine.removeReceivers(players);
        }
    }

    @Override
    public boolean hasViewer(@NonNull Player player) {
        return viewers.contains(player);
    }

    @Override
    public void addViewers(@NonNull Player... players) {
        viewers.addAll(Arrays.asList(players));

        for (ProtocolHolographicLine holographicLine : holographicLines) {
            holographicLine.addViewers(players);
        }
    }

    @Override
    public void removeViewers(@NonNull Player... players) {
        viewers.removeAll(Arrays.asList(players));

        for (ProtocolHolographicLine holographicLine : holographicLines) {
            holographicLine.removeViewers(players);
        }
    }


    private boolean isPublic;

    @Override
    public void spawn() {
        isPublic = true;

        for (ProtocolHolographicLine holographicLine : holographicLines)
            holographicLine.spawn();
    }

    @Override
    public void remove() {
        isPublic = false;

        for (ProtocolHolographicLine holographicLine : holographicLines)
            holographicLine.remove();
    }

    @Override
    public void update() {
        for (ProtocolHolographicLine holographicLine : holographicLines) {

            holographicLine.addReceivers(receivers.toArray(new Player[0]));
            holographicLine.addViewers(viewers.toArray(new Player[0]));

            holographicLine.update();
        }
    }


    @Override
    public void teleport(@NonNull Location location) {
        this.location = location;

        for (ProtocolHolographicLine holographicLine : holographicLines) {
            holographicLine.teleport(location);
        }
    }

    @Override
    public void setFullClickAction(Consumer<Player> clickAction) {
        for (ProtocolHolographicLine line : holographicLines) {
            // Если линия имеет арморстенд (если она не пустая)
            if (line.getFakeArmorStand()!= null) {
                line.getFakeArmorStand().setClickAction(clickAction);
            }
        }
    }

    @Override
    public void setHolographicUpdater(long updateTicks, @NonNull ProtocolHolographicUpdater holographicUpdater) {
        this.holographicUpdater = holographicUpdater;

        holographicUpdater.setEnable(true);
        holographicUpdater.startUpdater(updateTicks);
    }

}
