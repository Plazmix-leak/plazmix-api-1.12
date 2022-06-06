package net.plazmix.utility;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.plazmix.utility.mojang.MojangSkin;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class ItemUtil {

    public final Material EMPTY_ITEM_TYPE       = Material.AIR;

    public final MaterialData EMPTY_ITEM_DATA   = EMPTY_ITEM_TYPE.getNewData((byte) 0);
    public final ItemStack EMPTY_ITEM           = EMPTY_ITEM_DATA.toItemStack(1);


    public ItemStack parseItem(@NonNull ConfigurationSection configurationSection,
                               String... placeholders) {

        Material material = Material.matchMaterial(configurationSection.getString("type", "BEDROCK"));

        int amount = configurationSection.getInt("amount", 1);
        int durability = configurationSection.getInt("data", 0);

        String displayName = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("name", ChatColor.RESET.toString()));
        String playerSkull = configurationSection.getString("player-skull");


        List<String> displayLore = configurationSection.getStringList("lore");

        if (displayLore != null) {
            displayLore = displayLore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
        }

        List<String> enchantmentList = configurationSection.getStringList("enchantments");
        List<String> itemFlagList = configurationSection.getStringList("flags");


        ItemBuilder itemBuilder = newBuilder(material);

        itemBuilder.setAmount(amount);
        itemBuilder.setDurability(durability);

        itemBuilder.setName(displayName);
        itemBuilder.setLore(displayLore);

        if (playerSkull != null && durability == 3) {

            if (playerSkull.length() >= 32) {
                itemBuilder.setPlayerSkull(playerSkull);

            } else {

                itemBuilder.setTextureValue(playerSkull);
            }
        }

        if (enchantmentList != null) {
            for (String enchantmentString : enchantmentList) {
                String[] enchantmentData = enchantmentString.split(":", 2);

                Enchantment enchantmentType = Enchantment.getByName(enchantmentData[0].toUpperCase());
                int enchantmentLevel = Integer.parseInt(enchantmentData[1]);

                itemBuilder.addEnchantment(enchantmentType, enchantmentLevel);
            }
        }

        if (itemFlagList != null) {
            for (String itemFlagString : itemFlagList) {

                ItemFlag itemFlag = ItemFlag.valueOf(itemFlagString.toUpperCase());
                itemBuilder.addItemFlag(itemFlag);
            }
        }

        if (placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {

                String placeholder = placeholders[i];
                String value = placeholders[i + 1];

                itemBuilder.setName( displayName.replace(placeholder, value) );

                // Display lore
                if (displayLore != null) {

                    displayLore.replaceAll(s -> s.replace(placeholder, value));
                    itemBuilder.setLore(displayLore);
                }

                // Display name
                displayName = displayName.replace(placeholder, value);
                itemBuilder.setName(displayName);
            }
        }

        return itemBuilder.build();
    }

    public boolean matchMeta(@NonNull ItemStack itemStack1, @NonNull ItemStack itemStack2) {
        if(itemStack1.equals(itemStack2) || itemStack1.isSimilar(itemStack2))
            return true;

        return itemStack1.hasItemMeta() == itemStack2.hasItemMeta() && itemStack1.hasItemMeta()
                && JsonUtil.toJson(itemStack1.getItemMeta()).equals(JsonUtil.toJson(itemStack2.getItemMeta()));
    }

    public boolean matchType(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.getType().equals(itemStack2.getType());
    }

    public ItemStack getNamedItemStack(@NonNull Material material, int durability,
                                       @NonNull String displayName) {

        return newBuilder(material)

                .setDurability(durability)
                .setName(displayName)
                .build();
    }

    public ItemStack getNamedItemStack(@NonNull ItemStack itemStack,
                                       @NonNull String displayName) {

        return newBuilder(itemStack)
                .setName(displayName)

                .build();
    }

    public ItemStack getNamedItemStack(@NonNull MaterialData materialData,
                                       @NonNull String displayName) {

        return newBuilder(materialData.toItemStack(1))
                .setName(displayName)

                .build();
    }


    public ItemStack getSkull(@NonNull String playerName) {
        return newBuilder(Material.SKULL_ITEM)

                .setDurability(3)
                .setPlayerSkull(playerName)

                .build();
    }

    public ItemStack getNamedSkull(@NonNull String playerName,
                                   @NonNull String displayName) {

        return newBuilder(Material.SKULL_ITEM)
                .setDurability(3)

                .setName(displayName)
                .setPlayerSkull(playerName)

                .build();
    }

    public ItemStack getSkullByTexture(@NonNull String textureValue) {
        return newBuilder(Material.SKULL_ITEM)

                .setDurability(3)
                .setTextureValue(textureValue)

                .build();
    }

    public ItemStack getNamedSkullByTexture(@NonNull String textureValue,
                                            @NonNull String displayName) {

        return newBuilder(Material.SKULL_ITEM)
                .setDurability(3)

                .setName(displayName)
                .setTextureValue(textureValue)

                .build();
    }

    public ItemStack getPotion(@NonNull PotionEffect... potionEffects) {
        return getColouredPotion(Color.PURPLE, potionEffects);
    }

    public ItemStack getColouredPotion(@NonNull Color potionColor,
                                       @NonNull PotionEffect... potionEffects) {

        ItemBuilder itemBuilder = newBuilder(Material.POTION);
        itemBuilder.setPotionColor(potionColor);

        for (PotionEffect potionEffect : potionEffects) {
            itemBuilder.addCustomPotionEffect(potionEffect, true);
        }

        return itemBuilder.build();
    }


    public ItemBuilder newBuilder() {
        return newBuilder(EMPTY_ITEM);
    }

    public ItemBuilder newBuilder(@NonNull Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public ItemBuilder newBuilder(@NonNull MaterialData materialData) {
        return newBuilder(materialData.toItemStack(1));
    }

    public ItemBuilder newBuilder(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }


    @AllArgsConstructor
    public class ItemBuilder {

        private ItemStack itemStack;

        public ItemBuilder ifPresent(Predicate<ItemStack> itemPredicate,
                                     Consumer<ItemBuilder> itemConsumer) {

            if (itemPredicate == null) {
                return this;
            }

            return ifPresent(itemPredicate.test(itemStack), itemConsumer);
        }

        public ItemBuilder ifPresent(boolean itemPredicate,
                                     Consumer<ItemBuilder> itemConsumer) {

            if (itemConsumer != null && itemPredicate) {
                itemConsumer.accept(this);
            }

            return this;
        }

        public ItemBuilder setDurability(int durability) {
            itemStack.setDurability((byte) durability);
            return this;
        }

        public ItemBuilder setDyeColor(DyeColor dyeColor) {
            return setDurability(dyeColor.getWoolData());
        }

        public ItemBuilder setUnbreakable(boolean flag) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.spigot().setUnbreakable(flag);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder setMaterial(@NonNull Material material) {
            itemStack.setType(material);
            return this;
        }

        public ItemBuilder setAmount(int amount) {
            itemStack.setAmount(amount);
            return this;
        }

        public ItemBuilder setName(String name) {
            if (name == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(name);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder setLore(String... loreArray) {
            if (loreArray == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(Arrays.asList(loreArray));

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder setLore(List<String> loreList) {
            if (loreList == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(loreList);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder addLore(@NonNull String lore) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> loreList = itemMeta.getLore();

            if (loreList == null) {
                loreList = new ArrayList<>();
            }

            loreList.add(lore);
            return setLore(loreList);
        }

        public ItemBuilder addLoreArray(@NonNull String... loreArray) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> loreList = itemMeta.getLore();

            if (loreList == null) {
                loreList = new ArrayList<>();
            }

            loreList.addAll(Arrays.asList(loreArray));
            return setLore(loreList);
        }

        public ItemBuilder setGlowing(boolean glowing) {
            Enchantment glowEnchantment = Enchantment.ARROW_DAMAGE;

            if (glowing) {
                addItemFlag(ItemFlag.HIDE_ENCHANTS);
                addEnchantment(glowEnchantment, 1);
            } else {

                for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                    removeEnchantment(enchantment);
                }
            }

            return this;
        }

        public ItemBuilder toCraftItem() {
            itemStack = CraftItemStack.asCraftCopy(itemStack);

            return this;
        }

        @SneakyThrows
        public ItemBuilder addNbtTag(String name, NbtType nbtType) {
            toCraftItem();

            NbtCompound nbtCompound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            nbtCompound.put(NbtFactory.ofWrapper(nbtType, name));

            NbtFactory.setItemTag(itemStack, nbtCompound);
            return this;
        }

        @SneakyThrows
        public ItemBuilder removeNbtTag(String name) {
            NbtCompound nbtCompound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            nbtCompound.remove(name);

            NbtFactory.setItemTag(itemStack, nbtCompound);
            return this;
        }

        public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
            if (enchantment == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addEnchant(enchantment, level, true);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder removeEnchantment(Enchantment enchantment) {
            if (enchantment == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (!itemMeta.hasEnchant(enchantment) || itemMeta.hasConflictingEnchant(enchantment)) {
                return this;
            }

            itemMeta.removeEnchant(enchantment);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder addCustomPotionEffect(PotionEffect potionEffect, boolean isAdd) {
            if (potionEffect == null) {
                return this;
            }

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.addCustomEffect(potionEffect, isAdd);

            itemStack.setItemMeta(potionMeta);
            return this;
        }

        public ItemBuilder setMainPotionEffect(PotionEffectType potionEffectType) {
            if (potionEffectType == null) {
                return this;
            }

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setMainEffect(potionEffectType);

            itemStack.setItemMeta(potionMeta);
            return this;
        }

        public ItemBuilder setPotionColor(Color color) {
            if (color == null) {
                return this;
            }

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setColor(color);

            itemStack.setItemMeta(potionMeta);
            return this;
        }

        public ItemBuilder setPlayerSkull(String playerSkull) {
            if (playerSkull == null) {
                return this;
            }

            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(playerSkull);

            itemStack.setItemMeta(skullMeta);
            return this;
        }

        public ItemBuilder setTextureValue(String texture) {
            if (texture == null) {
                return this;
            }

            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

            UUID profileUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + texture).getBytes(Charsets.UTF_8));

            GameProfile gameProfile = new GameProfile(profileUuid, "Steve");
            gameProfile.getProperties().put("textures", new Property("textures", texture));

            ReflectionUtil.setField(skullMeta, "profile", gameProfile);

            itemStack.setItemMeta(skullMeta);
            return this;
        }

        public ItemBuilder setTextures(String value, String signature) {
            if (value == null || signature == null) {
                return this;
            }

            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", value, signature));

            ReflectionUtil.setField(skullMeta, "profile", gameProfile);

            itemStack.setItemMeta(skullMeta);
            return this;
        }

        public ItemBuilder setMojangSkin(MojangSkin mojangSkin) {
            if (mojangSkin == null) {
                return this;
            }

            return setTextures(mojangSkin.getValue(), mojangSkin.getSignature());
        }

        public ItemBuilder setLeatherColor(Color color) {
            if (color == null) {
                return this;
            }

            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            leatherArmorMeta.setColor(color);

            itemStack.setItemMeta(leatherArmorMeta);
            return this;
        }

        public ItemBuilder addItemFlag(ItemFlag itemFlag) {
            if (itemFlag == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(itemFlag);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder removeItemFlag(ItemFlag itemFlag) {
            if (itemFlag == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (!itemMeta.hasItemFlag(itemFlag)) {
                return this;
            }

            itemMeta.removeItemFlags(itemFlag);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStack build() {
            return itemStack;
        }
    }

}
