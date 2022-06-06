package net.plazmix.holographic.line;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.protocollib.entity.impl.FakeSilverfish;
import net.plazmix.utility.player.LocalizationPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class LangActionHolographicLine extends LangHolographicLine implements ProtocolHolographicLine {

    @Setter
    private FakeSilverfish clickableSilverfish;


    public LangActionHolographicLine(int lineIndex, Function<LocalizationPlayer, String> messageHandler, ProtocolHolographic holographic, Consumer<Player> clickAction) {
        super(lineIndex, messageHandler, holographic);

        this.clickAction = clickAction;
    }

    @Override
    public void initialize() {
        //armor stand holographic
        setInvisible(true);
        setBasePlate(false);
        setCustomNameVisible(true);
        setCustomName(lineText);

        updateLine(viewerCollection.toArray(new Player[0]));


        //clickable silverfish
        setClickableSilverfish(new FakeSilverfish(getLocation().clone().add(0, 2.2, 0)));

        clickableSilverfish.setInvisible(true);
        clickableSilverfish.setClickAction(clickAction);
    }

    @Override
    public void addReceivers(@NonNull Player... receivers) {
        super.addReceivers(receivers);
        clickableSilverfish.addReceivers(receivers);
    }

    @Override
    public void removeReceivers(@NonNull Player... receivers) {
        super.removeReceivers(receivers);
        clickableSilverfish.removeReceivers(receivers);
    }

    @Override
    public void addViewers(@NonNull Player... viewers) {
        super.addViewers(viewers);
        clickableSilverfish.addViewers(viewers);

        updateLine(viewers);
    }

    @Override
    public void removeViewers(@NonNull Player... viewers) {
        super.removeViewers(viewers);
        clickableSilverfish.removeViewers(viewers);
    }

    @Override
    public void spawn() {
        super.spawn();
        clickableSilverfish.spawn();
    }

    @Override
    public void remove() {
        super.remove();
        clickableSilverfish.remove();
    }

    @Override
    public void teleport(Location location) {
        this.location = location.clone().add(0, -(0.25 * lineIndex), 0);

        super.teleport(this.location);
        clickableSilverfish.teleport(getLocation().clone().add(0, 2.2, 0));
    }

}
