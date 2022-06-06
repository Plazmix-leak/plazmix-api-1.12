package net.plazmix.utility.mojang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.utility.JsonUtil;
import org.apache.commons.lang.RandomStringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class MojangUtil {

    protected final String UUID_URL_STRING = "https://api.mojang.com/users/profiles/minecraft/";
    protected final String SKIN_URL_STRING = "https://sessionserver.mojang.com/session/minecraft/profile/";

    protected final Map<String, MojangSkin> MOJANG_SKIN_MAP = new HashMap<>();


    @SneakyThrows
    protected String readURL(String url) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", RandomStringUtils.randomAlphanumeric(16));
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);

        StringBuilder output = new StringBuilder();

        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            while (bufferedReader.ready()) {
                output.append(bufferedReader.readLine());
            }
        }

        return output.toString();
    }

    public MojangSkin createSkinObject(String playerName, String value, String signature) {
        return new MojangSkin(playerName, UUID.randomUUID().toString(), value, signature, System.currentTimeMillis());
    }

    public MojangSkin createSkinObject(String value, String signature) {
        return createSkinObject(org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(16), value, signature);
    }

    public String getUserUUID(@NonNull String playerName) {
        JsonElement jsonElement = JsonUtil.JSON_PARSER.parse(readURL(UUID_URL_STRING + playerName));

        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject().get("id").getAsString();
        }

        return null;
    }

    public String getOriginalName(@NonNull String playerName) {
        JsonElement jsonElement = JsonUtil.JSON_PARSER.parse(readURL(UUID_URL_STRING + playerName));

        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject().get("name").getAsString();
        }

        return playerName;
    }

    public boolean isPremium(@NonNull String playerName, @NonNull UUID playerUuid) {
        String mojangUuid = MojangApi.getUserUUID(playerName);
        return playerUuid.toString().replace("-", "").equals(mojangUuid);
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrDefault(@NonNull String playerSkin) {
        return getMojangSkinOrDefault(playerSkin, "Steve");
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrDefault(@NonNull String playerSkin, String defaultSkin) {
        MojangSkin mojangSkin = getMojangSkinOrNull(playerSkin);

        if (mojangSkin == null) {
            return getMojangSkinOrNull(defaultSkin);
        }

        return mojangSkin;
    }

    @SneakyThrows
    public MojangSkin getMojangSkin(@NonNull String playerSkin) {
        MojangSkin mojangSkin = MOJANG_SKIN_MAP.get(playerSkin.toLowerCase());

        if (mojangSkin != null && !mojangSkin.isExpired()) {
            return mojangSkin;
        }

        String playerUUID = getUserUUID(playerSkin);

        if (playerUUID == null) {
            return getMojangSkin("Steve");
        }

        String skinUrl = readURL(SKIN_URL_STRING + playerUUID + "?unsigned=false");

        JsonObject textureProperty = JsonUtil.parse(skinUrl)
                .getAsJsonObject()

                .get("properties")
                .getAsJsonArray()

                .get(0)
                .getAsJsonObject();

        String texture = textureProperty.get("value").getAsString();
        String signature = textureProperty.get("signature").getAsString();

        mojangSkin = new MojangSkin(playerSkin, playerUUID, texture, signature, System.currentTimeMillis());

        MOJANG_SKIN_MAP.put(playerSkin.toLowerCase(), mojangSkin);
        return mojangSkin;
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrNull(@NonNull String playerSkin) {
        MojangSkin mojangSkin = MOJANG_SKIN_MAP.get(playerSkin.toLowerCase());

        if (mojangSkin != null && !mojangSkin.isExpired()) {
            return mojangSkin;
        }

        String playerUUID = getUserUUID(playerSkin);

        if (playerUUID == null) {
            return null;
        }

        String skinUrl = readURL(SKIN_URL_STRING + playerUUID + "?unsigned=false");

        JsonObject textureProperty = JsonUtil.parse(skinUrl)
                .getAsJsonObject()

                .get("properties")
                .getAsJsonArray()

                .get(0)
                .getAsJsonObject();

        String texture = textureProperty.get("value").getAsString();
        String signature = textureProperty.get("signature").getAsString();

        mojangSkin = new MojangSkin(playerSkin, playerUUID, texture, signature, System.currentTimeMillis());

        MOJANG_SKIN_MAP.put(playerSkin.toLowerCase(), mojangSkin);
        return mojangSkin;
    }

}
