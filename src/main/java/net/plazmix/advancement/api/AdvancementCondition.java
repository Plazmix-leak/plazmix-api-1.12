package net.plazmix.advancement.api;

import com.google.gson.JsonObject;
import net.plazmix.utility.JsonUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class AdvancementCondition {
    protected String name;
    protected JsonObject set;

    private AdvancementCondition(String name, JsonObject set) {
        this.name = name;
        this.set = set;
    }


    public static ConditionBuilder builder(String name, ConfigurationSerializable configurationSerializable) {
        JsonObject jsonObject = new JsonObject();

        configurationSerializable.serialize().forEach((propertyName, propertyObject) ->
                jsonObject.addProperty(propertyName, JsonUtil.toJson(propertyObject))
        );

        return new ConditionBuilder().name(name).set(jsonObject);
    }

    public static ConditionBuilder builder(String name, JsonObject jsonObject) {
        return new ConditionBuilder().name(name).set(jsonObject);
    }

    public static ConditionBuilder builder(String name, ItemStack itemStack) {
        return AdvancementCondition.builder(name,convertItemToJSON(itemStack));
    }


    //BEGIN UTIL
    private static JsonObject convertItemToJSON(ItemStack item) {
        JsonObject itemJSON = new JsonObject();
        itemJSON.addProperty("item", "minecraft:" + item.getType().name().toLowerCase());
        itemJSON.addProperty("amount", item.getAmount());
        itemJSON.addProperty("data", item.getData().getData());
        return itemJSON;
    }



    public static class ConditionBuilder {
        private String name;
        private JsonObject set;

        ConditionBuilder() {
        }

        public ConditionBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ConditionBuilder set(JsonObject set) {
            this.set = set;
            return this;
        }

        public AdvancementCondition build() {
            return new AdvancementCondition(name, set);
        }

        public String toString() {
            return "io.chazza.advancementapi.Condition.ConditionBuilder(name=" + this.name + ", set=" + this.set + ")";
        }
    }
}