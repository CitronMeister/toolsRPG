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
                sendXpActionBar(player, xp, xpToLevel, level);
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
        // TODO: Add option to combine levels when repairing

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        // XP & Level
        int currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 0);

        currentXp = currentXp + BlockXpData.getXpMultiplier(tool, block) * (int) Math.floor(ToolsSettings.getInstance().getDefaultXpMultiplier()); // XP per block broken
        //currentXp++; // XP per block
        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

            // set Enchantments
            Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                    .getEnchantmentsForLevel(ToolType.fromMaterial(tool), currentLevel);

            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int newLevel = entry.getValue();

                int currentEnchantLevel = meta.hasEnchant(enchantment)
                        ? meta.getEnchantLevel(enchantment)
                        : 0;

                // Only apply if new level is higher than existing
                if (newLevel > currentEnchantLevel) {
                    meta.addEnchant(enchantment, newLevel, true);
//                    System.out.println("Applied enchant " + enchantment.getKey().getKey() + " at level " + newLevel);
                }
            }


            // Notify player
            player.sendMessage(Component.text("Your tool leveled up to " + currentLevel + "!")
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }

        // Save updated XP and level
        meta.getPersistentDataContainer().set(Keys.TOOL_XP, PersistentDataType.INTEGER, currentXp);
        meta.getPersistentDataContainer().set(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, currentLevel);

        // Display name and lore
        String itemRarity = "";
        NamedTextColor rarityColor = NamedTextColor.GRAY;
        // TODO: Make this mess a method of its own
        if(currentLevel >= ToolsSettings.getInstance().getMaxLevel()) {
            itemRarity = "✮✮✮✮✮";
            rarityColor = NamedTextColor.GOLD;
        } else if (currentLevel >= (int) Math.floor((ToolsSettings.getInstance().getMaxLevel() / 5.0) * 4)) {
            itemRarity = "✮✮✮✮☆";
            rarityColor = NamedTextColor.LIGHT_PURPLE;
        }else if (currentLevel >= (int) Math.floor((ToolsSettings.getInstance().getMaxLevel() / 5.0) * 3)) {
            itemRarity = "✮✮✮☆☆";
            rarityColor = NamedTextColor.BLUE;
        } else if (currentLevel >= (int) Math.floor((ToolsSettings.getInstance().getMaxLevel() / 5.0) * 2)) {
            itemRarity = "✮✮☆☆☆";
            rarityColor = NamedTextColor.AQUA;
        }else if (currentLevel >= (int) Math.floor((ToolsSettings.getInstance().getMaxLevel() / 5.0) * 1)) {
            itemRarity = "✮☆☆☆☆";
            rarityColor = NamedTextColor.GREEN;
        } else {
            itemRarity = "☆☆☆☆☆";
        }
        meta.lore(List.of(
                Component.text(itemRarity).color(rarityColor),
//                Component.text("Blocks broken: " + brokenBlocks).color(NamedTextColor.YELLOW),
                Component.text("Level: " + currentLevel + "/" + ToolsSettings.getInstance().getMaxLevel()).color(NamedTextColor.AQUA),
                Component.text("XP: " + currentXp + "/" + xpForNext).color(NamedTextColor.GREEN)
        ));

        item.setItemMeta(meta);
    }
    public void blockWhitelist() {
        // logic that checks up against a list to see if the block should give XP
    }
    public int getXpForNextLevel(int level) {
        if(level == 0){
            return 100;
        } else {
            return (int) Math.floor(100 * Math.pow(1.6, level));
        }

    }
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
