package net.plazmix.protocollib.entity;

import lombok.NonNull;
import net.plazmix.protocollib.entity.animation.FakeEntityAnimation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface FakeEntityLiving
        extends FakeEntity {


    void playAnimationAll(@NonNull FakeEntityAnimation fakeEntityAnimation);

    void playAnimation(@NonNull FakeEntityAnimation fakeEntityAnimation, @NonNull Player player);


    void setArrowCount(int arrowCount);

    void setHealthScale(float healthScale);

    void setAmbientPotionEffect(boolean ambientPotionEffect);

    void setPotionEffectColor(@NonNull ChatColor potionEffectColor);


    ChatColor getPotionEffectColor();

    boolean isAmbientPotionEffect();

    float getHealthScale();

    int getArrowCount();
}
