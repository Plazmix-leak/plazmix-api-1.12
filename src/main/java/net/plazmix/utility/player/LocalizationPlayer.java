package net.plazmix.utility.player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.utility.localization.LocalizationResource;
import net.plazmix.coreconnector.utility.localization.LocalizedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LocalizationPlayer {
    
    private final LocalizedPlayer localizedPlayer;
    

    public void sendMessage(String messageKey) {
        localizedPlayer.sendMessage(messageKey);
    }

    public void sendMessage(MessageHandler<Object, LocalizationPlayer> messageHandler) {
        localizedPlayer.sendMessage(o -> messageHandler.handleMessage(this));
    }

    public void sendTitle(String titleKey, String subtitleKey, int fadeIn, int stay, int fadeOut) {
        localizedPlayer.sendTitle(titleKey, subtitleKey, fadeIn, stay, fadeOut);
    }

    public void sendTitle(MessageHandler<String, LocalizationPlayer> titleHandler, MessageHandler<String, LocalizationPlayer> subtitleHandler,
                              int fadeIn, int stay, int fadeOut) {

        localizedPlayer.sendTitle(o -> titleHandler.handleMessage(this), o -> subtitleHandler.handleMessage(this), fadeIn, stay, fadeOut);
    }

    public void sendTitle(String titleKey, String subtitleKey) {
        localizedPlayer.sendTitle(titleKey, subtitleKey);
    }

    public void sendTitle(MessageHandler<String, LocalizationPlayer> titleHandler, MessageHandler<String, LocalizationPlayer> subtitleHandler) {
        localizedPlayer.sendTitle(o -> titleHandler.handleMessage(this), o -> subtitleHandler.handleMessage(this));
    }

    public LocalizationMessage getMessage(String langKey) {
        return LocalizationMessage.create(getLocalizationResource(), langKey);
    }

    public List<String> getMessageList(String langKey) {
        return localizedPlayer.getLocalizationResource().getTextList(langKey);
    }

    public String getMessageText(String langKey) {
        return localizedPlayer.getLocalizationResource().getText(langKey);
    }

    public boolean isList(String langKey) {
        return localizedPlayer.getLocalizationResource().isList(langKey);
    }

    public boolean isText(String langKey) {
        return localizedPlayer.getLocalizationResource().isText(langKey);
    }

    public String getLocalizationUrl() {
        return localizedPlayer.getLocalizationResource().getResourceURL();
    }

    @Deprecated
    public LocalizationResource getLocalizationResource() {
        return localizedPlayer.getLocalizationResource();
    }

    public interface MessageHandler<R, O> {

        R handleMessage(O var1);
    }

    @RequiredArgsConstructor
    public static final class LocalizationMessage {

        private final String messageKey;
        private final LocalizationResource localizationResource;

        private Object handle;

        public static LocalizationMessage create(@NonNull LocalizationResource localizationResource, @NonNull String messageKey) {
            return new LocalizationMessage(messageKey, localizationResource);
        }

        public synchronized String toText() {
            return this.handle != null ? new String(this.handle.toString().getBytes()) : this.localizationResource.getText(this.messageKey);
        }

        @SuppressWarnings("unchecked")
        public synchronized List<String> toList() {
            return this.handle != null ? new ArrayList<>((List<String>)this.handle) : this.localizationResource.getTextList(this.messageKey);
        }

        public synchronized LocalizationMessage replace(@NonNull String placeholder, @NonNull Object value) {
            if (handle == null) {
                if (localizationResource.isText(messageKey)) {
                    handle = new String(toText().getBytes());
                }

                if (localizationResource.isList(messageKey)) {
                    handle = new ArrayList<>(toList());
                }
            }

            if (handle instanceof String) {
                handle = toText().replace(placeholder, value.toString());

            } else {

                handle = toList().stream().map((line) -> line.replace(placeholder, value.toString())).collect(Collectors.toList());
            }

            return this;
        }
    }
    
}
