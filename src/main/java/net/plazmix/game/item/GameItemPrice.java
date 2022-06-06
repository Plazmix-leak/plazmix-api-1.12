package net.plazmix.game.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.game.user.GameUser;
import net.plazmix.utility.NumberUtil;
import org.bukkit.ChatColor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class GameItemPrice {

    public static GameItemPrice create(int priceCount, @NonNull PriceCurrency currency) {
        return new GameItemPrice(priceCount, currency);
    }

    public static GameItemPrice createDefault(int priceCount) {
        return create(priceCount, PriceCurrency.COINS);
    }


    private final int count;
    private final PriceCurrency currency;

    public String formattingDisplay(boolean stripColor) {
        String display = currency.formattingDisplay(count);
        return stripColor ? ChatColor.stripColor(display) : display;
    }

    public String formattingDisplay() {
        return formattingDisplay(false);
    }

    @RequiredArgsConstructor
    public enum PriceCurrency {

        COINS(ChatColor.YELLOW, "монета", "монеты", "монет"),
        GOLDS(ChatColor.LIGHT_PURPLE, "плазма", "плазмы", "плазмы"),
        EXPERIENCE(ChatColor.DARK_AQUA, "опыт", "опыта", "опыта"),
        ;

        @Getter
        private final ChatColor chatColor;

        private final String single;
        private final String second;
        private final String other;

        public String formattingDisplay(int count) {
            return chatColor + NumberUtil.formattingSpaced(count, single, second, other);
        }

        public boolean has(int count, @NonNull GameUser gameUser) {
            switch (this) {

                case COINS: {
                    return gameUser.getPlazmixHandle().hasCoins(count);
                }

                case GOLDS: {
                    return gameUser.getPlazmixHandle().hasGolds(count);
                }

                case EXPERIENCE: {
                    return gameUser.getPlazmixHandle().getExperience() >= count;
                }
            }

            return false;
        }

        public void set(int newCount, @NonNull GameUser gameUser) {
            switch (this) {

                case COINS: {
                    gameUser.getPlazmixHandle().setCoins(newCount);
                    break;
                }

                case GOLDS: {
                    gameUser.getPlazmixHandle().setGolds(newCount);
                    break;
                }

                case EXPERIENCE: {
                    gameUser.getPlazmixHandle().setExperience(newCount);
                    break;
                }
            }
        }

        public void add(int addCount, @NonNull GameUser gameUser) {
            switch (this) {

                case COINS: {
                    gameUser.getPlazmixHandle().addCoins(addCount);
                    break;
                }

                case GOLDS: {
                    gameUser.getPlazmixHandle().addGolds(addCount);
                    break;
                }

                case EXPERIENCE: {
                    gameUser.getPlazmixHandle().addExperience(addCount);
                    break;
                }
            }
        }

        public void take(int takeCount, @NonNull GameUser gameUser) {
            switch (this) {

                case COINS: {
                    gameUser.getPlazmixHandle().removeCoins(takeCount);
                    break;
                }

                case GOLDS: {
                    gameUser.getPlazmixHandle().removeGolds(takeCount);
                    break;
                }

                case EXPERIENCE: {
                    gameUser.getPlazmixHandle().takeExperience(takeCount);
                    break;
                }
            }
        }

    }
}
