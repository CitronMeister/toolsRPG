package org.shyni.RPGTools.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.shyni.RPGTools.RPGTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobXpData {

    private static final MobXpData instance = new MobXpData();
    EntityType type = EntityType.ZOMBIE;

    private final Map<EntityType, Integer> mobXpMap = new HashMap<>();
    private File file;
    private YamlConfiguration config;

    private MobXpData() {}

    public void load() {
        file = new File(RPGTools.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists()) {
            RPGTools.getInstance().saveResource("settings.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        mobXpMap.clear();
        if (config.isConfigurationSection("Mobs")) {
            for (String key : config.getConfigurationSection("Mobs").getKeys(false)) {
                try {
                    EntityType type = EntityType.valueOf(key.toUpperCase());
                    int xp = config.getInt("Mobs." + key);
                    mobXpMap.put(type, xp);
                } catch (IllegalArgumentException e) {
                    // Skip invalid entries
                }
            }
        }
    }

    public int getXpForMob(EntityType type) {
        return mobXpMap.getOrDefault(type, 0); // 0 XP if not listed
    }

    public static MobXpData getInstance() {
        return instance;
    }
}


