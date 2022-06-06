package net.plazmix.advancement.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.plazmix.utility.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Getter
public class CustomAdvancement {

    private final NamespacedKey id;

    private final String parent, icon, background;
    private final TextComponent title, description;

    private final AdvancementFrame frame;

    private final boolean announce, toast, hidden;

    private final Set<AdvancementTrigger.TriggerBuilder> triggers;


    private CustomAdvancement(@NonNull NamespacedKey id, String parent,
                              @NonNull String icon, @NonNull String background,
                              @NonNull TextComponent title, @NonNull TextComponent description,
                              @NonNull AdvancementFrame frame,

                              boolean announce,
                              boolean toast,
                              boolean hidden,

                              Set<AdvancementTrigger.TriggerBuilder> triggers) {
        this.id = id;
        this.parent = parent;
        this.icon = icon;
        this.background = background;
        this.title = title;
        this.description = description;
        this.frame = frame;
        this.announce = announce;
        this.toast = toast;
        this.hidden = hidden;
        this.triggers = triggers;
    }

    public static AdvancementBuilder builder(@NonNull NamespacedKey id) {
        return new AdvancementBuilder().id(id);
    }

    public String toJson() {
        JsonObject json = new JsonObject();

        JsonObject icon = new JsonObject();
        icon.addProperty("item", this.icon);

        JsonObject display = new JsonObject();
        display.add("icon", icon);
        display.add("title", getJsonFromComponent(title));
        display.add("description", getJsonFromComponent(description));
        display.addProperty("background", background);
        display.addProperty("frame", frame.toString());
        display.addProperty("announce_to_chat", announce);
        display.addProperty("show_toast", toast);
        display.addProperty("hidden", hidden);

        json.addProperty("parent", parent);

        JsonObject criteria = new JsonObject();

        for (AdvancementTrigger.TriggerBuilder triggerBuilder : getTriggers()) {
            AdvancementTrigger advancementTrigger = triggerBuilder.build();

            criteria.add(advancementTrigger.name, advancementTrigger.toJsonObject());
        }

        json.add("criteria", criteria);
        json.add("display", display);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }

    public static JsonElement getJsonFromComponent(@NonNull TextComponent textComponent) {
        return JsonUtil.fromJson(ComponentSerializer.toString(textComponent), JsonElement.class);
    }

    public Set<AdvancementTrigger.TriggerBuilder> getTriggers() {
        return this.triggers;
    }

    public CustomAdvancement grant(@NonNull Player... players) {
        Advancement advancement = getAdvancement();

        for (Player player : players) {
            if (!player.getAdvancementProgress(advancement).isDone()) {
                Collection<String> remainingCriteria = player.getAdvancementProgress(advancement).getRemainingCriteria();

                for (String remainingCriterion : remainingCriteria)
                    player.getAdvancementProgress(advancement).awardCriteria(remainingCriterion);
            }
        }

        return this;
    }

    public CustomAdvancement revoke(@NonNull Player... players) {
        Advancement advancement = getAdvancement();

        for (Player player : players) {
            if (player.getAdvancementProgress(advancement).isDone()) {
                Collection<String> awardedCriteria = player.getAdvancementProgress(advancement).getAwardedCriteria();

                for (String awardedCriterion : awardedCriteria)
                    player.getAdvancementProgress(advancement).revokeCriteria(awardedCriterion);
            }
        }

        return this;
    }

    public CustomAdvancement remove() {
        Bukkit.getUnsafe().removeAdvancement(id);
        return this;
    }

    public Advancement getAdvancement() {
        Advancement advancement = Bukkit.getAdvancement(id);

        if (advancement == null) {
            advancement = Bukkit.getUnsafe().loadAdvancement(id, toJson());
        }

        return advancement;
    }

    public boolean counterUp(@NonNull Player player) {
        String criteriaString = null;
        for (String criteria : getAdvancement().getCriteria()) {
            if (player.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) != null) {
                criteriaString = criteria;
            } else {
                break;
            }
        }
        if (criteriaString == null) return false;
        player.getAdvancementProgress(getAdvancement()).awardCriteria(criteriaString);
        return true;
    }

    public boolean counterDown(@NonNull Player player) {
        String criteriaString = null;
        for (String criteria : getAdvancement().getCriteria()) {
            if (player.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) != null) {
                criteriaString = criteria;
            } else {
                break;
            }
        }
        if (criteriaString == null) return false;
        player.getAdvancementProgress(getAdvancement()).revokeCriteria(criteriaString);
        return true;
    }

    public void counterReset(@NonNull Player player) {
        for (String criteria : getAdvancement().getCriteria()) {
            if (player.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) != null) {
                player.getAdvancementProgress(getAdvancement()).revokeCriteria(criteria);
            }
        }
    }

    @Getter
    public static class AdvancementBuilder {

        private NamespacedKey id;

        private String parent;
        private String icon;
        private String background;

        private TextComponent title;
        private TextComponent description;

        private AdvancementFrame frame;

        private boolean announce;
        private boolean toast;
        private boolean hidden;

        private ArrayList<AdvancementTrigger.TriggerBuilder> triggers;


        public AdvancementBuilder title(@NonNull String title) {

            this.title = new TextComponent(title);

            return this;
        }

        public AdvancementBuilder title(@NonNull TextComponent title) {
            this.title = title;

            return this;
        }

        public AdvancementBuilder description(@NonNull String description) {
            this.description = new TextComponent(description);
            return this;
        }

        public AdvancementBuilder description(@NonNull TextComponent description) {
            this.description = description;
            return this;
        }


        public AdvancementBuilder id(@NonNull NamespacedKey id) {
            this.id = id;
            return this;
        }

        public AdvancementBuilder parent(@NonNull String parent) {
            this.parent = parent;
            return this;
        }

        public AdvancementBuilder icon(@NonNull String icon) {
            this.icon = icon;
            return this;
        }

        public AdvancementBuilder background(@NonNull String background) {
            this.background = background;
            return this;
        }

        public AdvancementBuilder frame(@NonNull AdvancementFrame frame) {
            this.frame = frame;
            return this;
        }

        public AdvancementBuilder announce(boolean announce) {
            this.announce = announce;
            return this;
        }

        public AdvancementBuilder toast(boolean toast) {
            this.toast = toast;
            return this;
        }

        public AdvancementBuilder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public AdvancementBuilder trigger(@NonNull AdvancementTrigger.TriggerBuilder trigger) {
            if (this.triggers == null)
                this.triggers = new ArrayList<>();
            this.triggers.add(trigger);
            return this;
        }

        public AdvancementBuilder triggers(@NonNull Collection<? extends AdvancementTrigger.TriggerBuilder> triggers) {
            if (this.triggers == null)
                this.triggers = new ArrayList<>();
            this.triggers.addAll(triggers);
            return this;
        }

        public AdvancementBuilder clearTriggers() {
            if (this.triggers != null)
                this.triggers.clear();

            return this;
        }

        public CustomAdvancement build() {
            Set<AdvancementTrigger.TriggerBuilder> triggers;
            switch (this.triggers == null ? 0 : this.triggers.size()) {
                case 0:
                    triggers = java.util.Collections.singleton(AdvancementTrigger.builder(AdvancementTrigger.TriggerType.IMPOSSIBLE, RandomStringUtils.randomAlphanumeric(16)));
                    break;
                case 1:
                    triggers = java.util.Collections.singleton(this.triggers.get(0));
                    break;
                default:
                    triggers = new java.util.LinkedHashSet<>(this.triggers.size() < 1073741824 ? 1 + this.triggers.size() + (this.triggers.size() - 3) / 3 : Integer.MAX_VALUE);
                    triggers.addAll(this.triggers);
                    triggers = java.util.Collections.unmodifiableSet(triggers);
            }

            CustomAdvancement customAdvancement = new CustomAdvancement(id, parent, icon, background, title, description, frame, announce, toast, hidden, triggers);
            customAdvancement.getAdvancement();

            Bukkit.reloadData();
            return customAdvancement;
        }

        public String toString() {
            return getClass().getName() + "(id=" + this.id + ", parent=" + this.parent + ", icon=" + this.icon + ", background=" + this.background + ", title=" + this.title + ", description=" + this.description + ", frame=" + this.frame + ", announce=" + this.announce + ", toast=" + this.toast + ", hidden=" + this.hidden + ", triggers=" + this.triggers + ")";
        }
    }

}