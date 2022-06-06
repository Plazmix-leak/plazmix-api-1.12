package net.plazmix.game.utility;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import net.plazmix.game.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@UtilityClass
public class GameSchedulers {

    public BukkitTask submit(Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        return Bukkit.getScheduler().runTask(GamePlugin.getInstance(), runnable);
    }

    public BukkitTask submitAsync(Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        return Bukkit.getScheduler().runTaskAsynchronously(GamePlugin.getInstance(), runnable);
    }

    public BukkitTask runTimer(long delay, long period, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        return Bukkit.getScheduler().runTaskTimer(GamePlugin.getInstance(), runnable, delay, period);
    }

    public BukkitTask runTimerAsync(long delay, long period, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        return Bukkit.getScheduler().runTaskTimerAsynchronously(GamePlugin.getInstance(), runnable, delay, period);
    }

    public BukkitTask runLater(long delay, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        return Bukkit.getScheduler().runTaskLater(GamePlugin.getInstance(), runnable, delay);
    }

    public BukkitTask runLaterAsync(long delay, Runnable runnable) {
        Preconditions.checkArgument(GamePlugin.getInstance() != null, "GamePlugin");
        Preconditions.checkArgument(runnable != null, "runnable");

        return Bukkit.getScheduler().runTaskLaterAsynchronously(GamePlugin.getInstance(), runnable, delay);
    }

}
