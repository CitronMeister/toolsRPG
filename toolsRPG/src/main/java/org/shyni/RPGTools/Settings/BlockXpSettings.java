package org.shyni.RPGTools.Settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shyni.RPGTools.RPGTools;
import org.shyni.RPGTools.util.ToolType;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlockXpSettings {

    private static final BlockXpSettings instance = new BlockXpSettings();

    private final Map<ToolType, Map<Material, Double>> xpMap = new HashMap<>();

    private File file;
    private YamlConfiguration config;

    private BlockXpSettings() {}

    public void load() {
        file = new File(RPGTools.getInstance().getDataFolder(), "blocks.yml");

        if (!file.exists()) {
            RPGTools.getInstance().saveResource("blocks.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (String toolKey : config.getKeys(false)) {
            ToolType toolType;
            try {
                toolType = ToolType.valueOf(toolKey.toUpperCase());
            } catch (IllegalArgumentException ex) {
                Bukkit.getLogger().warning("[RPGTools] Invalid tool type in blocks.yml: " + toolKey);
                continue;
            }

            ConfigurationSection section = config.getConfigurationSection(toolKey);
            if (section == null) continue;

            Map<Material, Double> toolBlocks = new HashMap<>();
            for (String blockKey : section.getKeys(false)) {
                Material mat = Material.getMaterial(blockKey.toUpperCase());
                if (mat != null) {
                    toolBlocks.put(mat, section.getDouble(blockKey));
                } else {
                    Bukkit.getLogger().warning("[RPGTools] Invalid block material in blocks.yml: " + blockKey);
                }
            }

            xpMap.put(toolType, toolBlocks);
        }

        Bukkit.getLogger().info("[RPGTools] Block XP data loaded.");
    }

    public boolean shouldGiveXp(Material tool, Material block) {
        ToolType type = ToolType.fromMaterial(tool);
        return type != null && xpMap.getOrDefault(type, Collections.emptyMap()).containsKey(block);
    }

    public int getXpMultiplier(Material tool, Material block) {
        ToolType type = ToolType.fromMaterial(tool);
        if (type == null) return 0;

        double value = xpMap.getOrDefault(type, Collections.emptyMap()).getOrDefault(block, 0.0);
        return (int) Math.round(value);
    }

    public static BlockXpSettings getInstance() {
        return instance;
    }
}
