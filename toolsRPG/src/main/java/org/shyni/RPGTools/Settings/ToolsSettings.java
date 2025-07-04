package org.shyni.RPGTools.Settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.shyni.RPGTools.RPGTools;
import org.shyni.RPGTools.util.ToolType;

import java.io.File;
import java.util.*;

public class ToolsSettings {

    private static final ToolsSettings instance = new ToolsSettings();

    private File file;
    private YamlConfiguration config;

    private int maxLevel;
    private int xpGainMultiplier;
    private double requiredXpMultiplier;
    private int requiredXpBase;
    private int premiumXpGainMultiplier;
    private String loreRaritySymbol1;
    private String loreRaritySymbol2;
    private List<String> loreRarityColours;
    private List<String> xpAndLevelColours;
    private String actionBarStyle;
    private Boolean repairOnLevelup;


    // Tool-specific level enchantments
    private final Map<ToolType, Map<Integer, Map<Enchantment, Integer>>> levelEnchantments = new HashMap<>();

    private ToolsSettings() {}

    public void load() {
        file = new File(RPGTools.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists()) {
            RPGTools.getInstance().saveResource("settings.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // level stuff
        maxLevel = config.getInt("max-level", 10);
        xpGainMultiplier = config.getInt("xp-gain-multiplier", 1);
        premiumXpGainMultiplier = config.getInt("premium-xp-gain-multiplier", 1);
        requiredXpMultiplier = config.getDouble("required-xp-multiplier", 1.6);
        requiredXpBase = config.getInt("required-xp-base", 100);
        repairOnLevelup = config.getBoolean("repair-on-levelup", false);

        // action bar
        actionBarStyle = config.getString("action-bar-style", "full");

        // customisation
        loreRaritySymbol1 = config.getString("lore-rarity-symbol-1", "★");
        loreRaritySymbol2 = config.getString("lore-rarity-symbol-2", "☆");

        loreRarityColours = Optional.of(config.getStringList("lore-colours"))
                .filter(list -> !list.isEmpty())
                .orElse(Arrays.asList("#AAAAAA", "#55FF55", "#55FFFF", "#5555FF", "#FF55FF", "#FFAA00"));

        xpAndLevelColours = Optional.of(config.getStringList("xp-and-level-colours"))
                .filter(list -> !list.isEmpty())
                .orElse(Arrays.asList("#555555", "#5555FF", "#55FF55", "#ffffff", "#55FFFF", "#AA0000"));

        // Load enchantments
        ConfigurationSection root = config.getConfigurationSection("Level_Enchantments");
        if (root != null) {
            for (String toolKey : root.getKeys(false)) {
                ToolType toolType;
                try {
                    toolType = ToolType.valueOf(toolKey.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    continue; // Invalid tool type
                }

                ConfigurationSection levelSection = root.getConfigurationSection(toolKey);
                if (levelSection == null) continue;

                Map<Integer, Map<Enchantment, Integer>> enchPerLevel = new HashMap<>();

                for (String levelKey : levelSection.getKeys(false)) {
                    int level;
                    try {
                        level = Integer.parseInt(levelKey);
                    } catch (NumberFormatException ex) {
                        continue;
                    }

                    ConfigurationSection enchantsSection = levelSection.getConfigurationSection(levelKey);
                    if (enchantsSection == null) continue;

                    Map<Enchantment, Integer> enchantments = new HashMap<>();
                    for (String enchKey : enchantsSection.getKeys(false)) {
                        Enchantment enchant = Enchantment.getByName(enchKey.toUpperCase());
                        if (enchant != null) {
                            enchantments.put(enchant, enchantsSection.getInt(enchKey));
                        }
                    }

                    enchPerLevel.put(level, enchantments);
                }

                levelEnchantments.put(toolType, enchPerLevel);
            }
        }
    }

    public Map<Enchantment, Integer> getEnchantmentsForLevel(ToolType toolType, int level) {
        Map<Integer, Map<Enchantment, Integer>> toolEnchants = levelEnchantments.get(toolType);
        if (toolEnchants == null) return Collections.emptyMap();

        return toolEnchants.entrySet().stream()
                .filter(entry -> entry.getKey() <= level)
                .max(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyMap());
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // This is used to edit a value in the config
    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getXpGainMultiplier() {
        return xpGainMultiplier;
    }

    public int getPremiumXpGainMultiplier() {
        return premiumXpGainMultiplier;
    }

    public double getRequiredXpMultiplier() {
        return requiredXpMultiplier;
    }
    public int getRequiredXpBase() {
        return requiredXpBase;
    }

    public Boolean getRepairOnLevelup() {return repairOnLevelup;}

    public String getLoreRaritySymbol1() {
        return loreRaritySymbol1;
    }
    public String getLoreRaritySymbol2() {
        return loreRaritySymbol2;
    }
    public List<String> getLoreRarityColours() {
        return loreRarityColours;
    }
    public List<String> getXpAndLevelColours() {
        return xpAndLevelColours;
    }
    public String getActionBarStyle() {
        return actionBarStyle;
    }


    public static ToolsSettings getInstance() {
        return instance;
    }
}
