package org.shyni.RPGTools.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.shyni.RPGTools.Settings.BlockXpSettings;
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
        if (player.hasPermission("rpgtools.premium")) {
            currentXp += BlockXpSettings.getInstance().getXpMultiplier(tool, block) * (ToolsSettings.getInstance().getXpGainMultiplier() + ToolsSettings.getInstance().getPremiumXpGainMultiplier());
        } else {
            currentXp += BlockXpSettings.getInstance().getXpMultiplier(tool, block) * ToolsSettings.getInstance().getXpGainMultiplier();
        }

        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentLevel < maxLevel && currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

            if (ToolsSettings.getInstance().getRepairOnLevelup()) {
                if (meta instanceof Damageable damageable) {
                    damageable.setDamage(0);
                }
            }

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

        updateLore(meta, type);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        // Send actionbar
        if(currentLevel < maxLevel){
            ActionBarUtil.sendXpActionBar(player, currentXp, xpForNext, currentLevel);
        }

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

        if(player.hasPermission("rpgtools.premium")){
            currentXp += MobXpData.getInstance().getXpForMob(killedType.getType()) * (ToolsSettings.getInstance()
                    .getXpGainMultiplier() + ToolsSettings.getInstance().getPremiumXpGainMultiplier());
        } else {
            currentXp += MobXpData.getInstance().getXpForMob(killedType.getType()) * ToolsSettings.getInstance().getXpGainMultiplier();
        }

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

            if (ToolsSettings.getInstance().getRepairOnLevelup()) {
                if (meta instanceof Damageable damageable) {
                    damageable.setDamage(0);
                }
            }

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

        updateLore(meta, type);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        if(currentLevel < maxLevel){
            ActionBarUtil.sendXpActionBar(player, currentXp, xpForNext, currentLevel);
        }

    }

    public static int getXpForNextLevel(int currentLevel) {
        int requiredXpBase = ToolsSettings.getInstance().getRequiredXpBase();
        double requiredXpMultiplier = ToolsSettings.getInstance().getRequiredXpMultiplier();
        if(currentLevel == 0){
            return requiredXpBase;
        } else {
            return (int) Math.floor(requiredXpBase * Math.pow(requiredXpMultiplier, currentLevel));
        }
    }

    public static void updateLore(ItemMeta meta, ToolType type) {
        String itemRarity;
        String rarityColor;
        int level = 0;
        int currentXp = 0;
        List<String> levelColours = ToolsSettings.getInstance().getXpAndLevelColours();
        List<String> rarityColours = ToolsSettings.getInstance().getLoreRarityColours();

        String symbol1 = ToolsSettings.getInstance().getLoreRaritySymbol1();
        String symbol2 = ToolsSettings.getInstance().getLoreRaritySymbol2();


        if (type.isWeapon()) {
            level = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, 0);
            currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_XP, PersistentDataType.INTEGER, 0);
        } else {
            level = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 0);
            currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
        }

        int xpForNext = getXpForNextLevel(level);
        int maxLevel = ToolsSettings.getInstance().getMaxLevel();

        if (level >= maxLevel) {
            itemRarity = symbol1+symbol1+symbol1+symbol1+symbol1; // ✮✮✮✮✮
            rarityColor = rarityColours.get(5);
        } else if (level >= maxLevel * 4 / 5) {
            itemRarity = symbol1+symbol1+symbol1+symbol1+symbol2;
            rarityColor = rarityColours.get(4);
        } else if (level >= maxLevel * 3 / 5) {
            itemRarity = symbol1+symbol1+symbol1+symbol2+symbol2;
            rarityColor = rarityColours.get(3);
        } else if (level >= maxLevel * 2 / 5) {
            itemRarity = symbol1+symbol1+symbol2+symbol2+symbol2;
            rarityColor = rarityColours.get(2);
        } else if (level >= maxLevel / 5) {
            itemRarity = symbol1+symbol2+symbol2+symbol2+symbol2;
            rarityColor = rarityColours.get(1);
        } else {
            itemRarity = symbol2+symbol2+symbol2+symbol2+symbol2;
            rarityColor = rarityColours.getFirst();
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(itemRarity, TextColor.fromHexString(rarityColor)));
        if(level < maxLevel){
            lore.add(Component.text("level: " + level + "/" + maxLevel, TextColor.fromHexString(levelColours.getFirst())));
        } else {
            lore.add(Component.text("level: " + level + "/" + maxLevel, TextColor.fromHexString(levelColours.get(5))));
        }


        if (level < maxLevel) {
            Component xpLine = Component.text()
                    .append(Component.text("XP: ", TextColor.fromHexString(levelColours.get(1))))
                    .append(Component.text(currentXp, TextColor.fromHexString(levelColours.get(2))))
                    .append(Component.text("/", TextColor.fromHexString(levelColours.get(3))))
                    .append(Component.text(xpForNext, TextColor.fromHexString(levelColours.get(4))))
                    .build();
            lore.add(xpLine);
        }

        meta.lore(lore);
    }
}
