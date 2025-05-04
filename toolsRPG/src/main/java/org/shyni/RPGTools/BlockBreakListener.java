package org.shyni.RPGTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        Material tool = player.getInventory().getItemInMainHand().getType();

        if(BlockXpData.shouldGiveXp(tool, block)) {
            UpdateItem(player, tool, block);
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int xp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
                int level = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 1);
                int xpToLevel = getXpForNextLevel(level);
                System.out.println(level);
                if (level < ToolsSettings.getInstance().getMaxLevel()){
                    sendXpActionBar(player, xp, xpToLevel, level);
                }
            }
        }
    }
    public void sendXpActionBar(Player player, int currentXp, int xpToLevel, int level) {
        int totalBars = 20; // Length of the XP bar
        float progress = Math.min((float) currentXp / xpToLevel, 1.0f);
        int filledBars = Math.round(progress * totalBars);
        boolean detailedActionBar = true;
        if(detailedActionBar){
            Component bar = Component.text("Level " + level + " ", NamedTextColor.YELLOW)
                    .append(Component.text("[", NamedTextColor.GRAY));

            for (int i = 0; i < totalBars; i++) {
                bar = bar.append(Component.text("|", i < filledBars ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY));
            }

            bar = bar.append(Component.text("]", NamedTextColor.GRAY))
                    .append(Component.text(" " + currentXp + "/" + xpToLevel + " XP", NamedTextColor.YELLOW));

            player.sendActionBar(bar);
        }  else {
            Component bar = Component.text("");
            for (int i = 0; i < totalBars; i++) {
                bar = bar.append(Component.text("|", i < filledBars ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY));
            }
            player.sendActionBar(bar);

        }


    }

    public void UpdateItem(Player player, Material tool, Material block) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        int currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 0);
        int maxLevel = ToolsSettings.getInstance().getMaxLevel();

        // Ensure the name is always gold and bold at max level
        if (currentLevel >= maxLevel) {
            Component displayName = meta.hasDisplayName()
                    ? meta.displayName()
                    : Component.translatable(item.translationKey()); // fallback to default name

            Component updatedName = displayName.color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
            meta.displayName(updatedName);
        }


        currentXp += BlockXpData.getXpMultiplier(tool, block) * (int) Math.floor(ToolsSettings.getInstance().getDefaultXpMultiplier());
        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentLevel < maxLevel && currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

            Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                    .getEnchantmentsForLevel(ToolType.fromMaterial(tool), currentLevel);

            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int newLevel = entry.getValue();
                int currentEnchantLevel = meta.hasEnchant(enchantment) ? meta.getEnchantLevel(enchantment) : 0;

                if (newLevel > currentEnchantLevel) {
                    meta.addEnchant(enchantment, newLevel, true);
                }
            }

            player.sendMessage(Component.text("Your tool leveled up to " + currentLevel + "!")
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }

        meta.getPersistentDataContainer().set(Keys.TOOL_XP, PersistentDataType.INTEGER, currentXp);
        meta.getPersistentDataContainer().set(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, currentLevel);

        // Rarity display
        String itemRarity;
        NamedTextColor rarityColor;
        if (currentLevel >= maxLevel) {
            itemRarity = "✮✮✮✮✮";
            rarityColor = NamedTextColor.GOLD;
        } else if (currentLevel >= maxLevel * 4 / 5) {
            itemRarity = "✮✮✮✮☆";
            rarityColor = NamedTextColor.LIGHT_PURPLE;
        } else if (currentLevel >= maxLevel * 3 / 5) {
            itemRarity = "✮✮✮☆☆";
            rarityColor = NamedTextColor.BLUE;
        } else if (currentLevel >= maxLevel * 2 / 5) {
            itemRarity = "✮✮☆☆☆";
            rarityColor = NamedTextColor.AQUA;
        } else if (currentLevel >= maxLevel / 5) {
            itemRarity = "✮☆☆☆☆";
            rarityColor = NamedTextColor.GREEN;
        } else {
            itemRarity = "☆☆☆☆☆";
            rarityColor = NamedTextColor.GRAY;
        }

        // Build lore dynamically
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(itemRarity).color(rarityColor));
        lore.add(Component.text("Level: " + currentLevel + "/" + maxLevel).color(NamedTextColor.AQUA));

        if (currentLevel < maxLevel) {
            lore.add(Component.text("XP: " + currentXp + "/" + xpForNext).color(NamedTextColor.GREEN));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    public void blockWhitelist() {
        // logic that checks up against a list to see if the block should give XP
        // currently not in use since I use BlockXpData class instead
    }
    public int getXpForNextLevel(int level) {
        if(level == 0){
            return 100;
        } else {
            return (int) Math.floor(100 * Math.pow(1.6, level));
        }

    }
    // deprecated
    public boolean acceptedTool(Player player) {
        Material type = player.getInventory().getItemInMainHand().getType();
        //TODO : MAKE WEAPONS AND TOOLS SEPARATE!
        return switch (type) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE,
                 WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE,
                 WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
                 WOODEN_HOE, STONE_HOE, IRON_HOE, GOLDEN_HOE, DIAMOND_HOE, NETHERITE_HOE,
                 WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, FISHING_ROD -> true;
            default -> false;
        };
    }

}
