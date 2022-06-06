package net.plazmix.advancement.api;

import org.bukkit.Bukkit;

/**
 * Created by ysl3000
 */
public enum AdvancementFrame {
    TASK("task"),
    GOAL("goal"),
    CHALLENGE("challenge");

    private String name;

    AdvancementFrame(String name) {
        this.name = name;
    }

    public static AdvancementFrame getFromString(String frameType) {
        if (frameType.equalsIgnoreCase("random")) return AdvancementFrame.RANDOM();
        else try {
            return AdvancementFrame.valueOf(frameType);
        } catch (EnumConstantNotPresentException e) {
            Bukkit.getLogger().info("[AdvancementAPI] Unknown FrameType given. Using default (TASK)");
            return AdvancementFrame.TASK;
        }
    }

    public static AdvancementFrame RANDOM() {
        AdvancementFrame[] advancementFrames = AdvancementFrame.values();
        return advancementFrames[(int) (Math.random() * (advancementFrames.length - 1))];
    }

    public String toString() {
        return name;
    }
}