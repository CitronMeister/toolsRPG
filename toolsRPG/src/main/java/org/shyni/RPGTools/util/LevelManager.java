package org.shyni.RPGTools.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.shyni.RPGTools.Settings.ToolsSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelManager {

    public static void updateToolItem(Player player, Material tool, Material block) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        ToolType type = ToolType.fromMaterial(item.getType());
        if (type == null || type.isWeapon()) return;

        int currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 0);
        int maxLevel = ToolsSettings.getInstance().getMaxLevel();

        if (currentLevel >= maxLevel) {
            Component displayName = meta.hasDisplayName()
                    ? meta.displayName()
                    : Component.translatable(item.translationKey());
            Component updatedName = displayName.color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
            meta.displayName(updatedName);
        }

        currentXp += BlockXpData.getXpMultiplier(tool, block) * (int) Math.floor(ToolsSettings.getInstance().getDefaultXpMultiplier());
        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentLevel < maxLevel && currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

            Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                    .getEnchantmentsForLevel(type, currentLevel);

            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int newLevel = entry.getValue();
                int currentEnchantLevel = meta.hasEnchant(enchantment) ? meta.getEnchantLevel(enchantment) : 0;

                if (newLevel > currentEnchantLevel) {
                    meta.addEnchant(enchantment, newLevel, true);
                }
            }

            player.sendMessage(Component.text("Your tool leveled up to " + currentLevel + "!")
                    .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }

        meta.getPersistentDataContainer().set(Keys.TOOL_XP, PersistentDataType.INTEGER, currentXp);
        meta.getPersistentDataContainer().set(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, currentLevel);

        updateLore(meta, currentLevel, xpForNext, currentXp, maxLevel);
        item.setItemMeta(meta);
    }

    public static void updateWeaponItem(Player player, LivingEntity killedType) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        ToolType type = ToolType.fromMaterial(item.getType());
        if (type == null || !type.isWeapon()) return;

        int currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_XP, PersistentDataType.INTEGER, 0);
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, 0);
        int maxLevel = ToolsSettings.getInstance().getMaxLevel();

        // Gain XP based on killed entity type (placeholder)
        currentXp += MobXpData.getInstance().getXpForMob(killedType.getType()); // TODO: Use real XP value from entity

        if (currentLevel >= maxLevel) {
            Component displayName = meta.hasDisplayName()
                    ? meta.displayName()
                    : Component.translatable(item.translationKey());

            Component updatedName = displayName.color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
            meta.displayName(updatedName);
        }

        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentLevel < maxLevel && currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

            Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                    .getEnchantmentsForLevel(type, currentLevel);
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int newLevel = entry.getValue();
                int currentEnchantLevel = meta.hasEnchant(enchantment) ? meta.getEnchantLevel(enchantment) : 0;

                if (newLevel > currentEnchantLevel) {
                    meta.addEnchant(enchantment, newLevel, true);
                }
            }

            player.sendMessage(Component.text("Your weapon leveled up to " + currentLevel + "!")
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }

        meta.getPersistentDataContainer().set(Keys.WEAPON_XP, PersistentDataType.INTEGER, currentXp);
        meta.getPersistentDataContainer().set(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, currentLevel);

        updateLore(meta, currentLevel, xpForNext, currentXp, maxLevel);
        item.setItemMeta(meta);
        if(currentLevel < maxLevel){
            ActionBarUtil.sendXpActionBar(player, currentXp, xpForNext, currentLevel, true);
        }

    }

    public static int getXpForNextLevel(int currentLevel) {
        if(currentLevel == 0){
            return 100;
        } else {
            return (int) Math.floor(100 * Math.pow(1.6, currentLevel));
        }
    }

    private static void updateLore(ItemMeta meta, int level, int xpForNext, int currentXp, int maxLevel) {
        String itemRarity;
        NamedTextColor rarityColor;

        if (level >= maxLevel) {
            itemRarity = "\u272E\u272E\u272E\u272E\u272E"; // ✮✮✮✮✮
            rarityColor = NamedTextColor.GOLD;
        } else if (level >= maxLevel * 4 / 5) {
            itemRarity = "\u272E\u272E\u272E\u272E\u2606";
            rarityColor = NamedTextColor.LIGHT_PURPLE;
        } else if (level >= maxLevel * 3 / 5) {
            itemRarity = "\u272E\u272E\u272E\u2606\u2606";
            rarityColor = NamedTextColor.BLUE;
        } else if (level >= maxLevel * 2 / 5) {
            itemRarity = "\u272E\u272E\u2606\u2606\u2606";
            rarityColor = NamedTextColor.AQUA;
        } else if (level >= maxLevel / 5) {
            itemRarity = "\u272E\u2606\u2606\u2606\u2606";
            rarityColor = NamedTextColor.GREEN;
        } else {
            itemRarity = "\u2606\u2606\u2606\u2606\u2606";
            rarityColor = NamedTextColor.GRAY;
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(itemRarity).color(rarityColor));
        lore.add(Component.text("Level: " + level + "/" + maxLevel).color(NamedTextColor.AQUA));

        if (level < maxLevel) {
            lore.add(Component.text("XP: " + currentXp + "/" + xpForNext).color(NamedTextColor.GREEN));
        }

        meta.lore(lore);
    }
}
