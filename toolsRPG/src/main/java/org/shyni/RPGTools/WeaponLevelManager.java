package org.shyni.RPGTools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.shyni.RPGTools.Settings.ToolsSettings;
import org.shyni.RPGTools.util.*;

public class WeaponLevelManager {

    public static void handleKill(Player player, LivingEntity target) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) return;

        ToolType type = ToolType.fromMaterial(item.getType());
        if (type == null || !type.isWeapon()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        int currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_XP, PersistentDataType.INTEGER, 0);
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, 0);
        int maxLevel = ToolsSettings.getInstance().getMaxLevel(); // TODO: add option in settings for seperate max level of weapons

        if (currentLevel >= maxLevel) return;

        int xpGain = 10; // TODO: 1. Add custom per mob xp 2. add config file options for mob xp
        currentXp += xpGain;

        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

            // TODO: Apply weapon-related enchantments
            player.sendMessage(Component.text("Your weapon leveled up to " + currentLevel + "!")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        }

        meta.getPersistentDataContainer().set(Keys.WEAPON_XP, PersistentDataType.INTEGER, currentXp);
        meta.getPersistentDataContainer().set(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, currentLevel);



        meta.lore(LoreBuilder.buildWeaponLore(currentLevel, currentXp, maxLevel, xpForNext));
        item.setItemMeta(meta);
        ActionBarUtil.sendXpActionBar(player, currentXp, xpForNext, currentLevel, true);
    }

    private static int getXpForNextLevel(int currentLevel) {
        if(currentLevel == 0){
            return 100;
        } else {
            return (int) Math.floor(100 * Math.pow(1.6, currentLevel));
        }
    }
}
