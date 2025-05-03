package org.shyni.toolsRPG;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ToolsSettings {

    private static final ToolsSettings instance = new ToolsSettings();

    private File file;
    private YamlConfiguration config;

    private int maxLevel;
    private double defaultXpMultiplier;

    private ToolsSettings() {}

    public void load() {
        file = new File(ToolsRPG.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists()) {
            ToolsRPG.getInstance().saveResource("settings.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        maxLevel = config.getInt("max-level", 10);
        defaultXpMultiplier = config.getDouble("default-xp-multiplier", 1.0);
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getDefaultXpMultiplier() {
        return defaultXpMultiplier;
    }

    public static ToolsSettings getInstance() {
        return instance;
    }
}
