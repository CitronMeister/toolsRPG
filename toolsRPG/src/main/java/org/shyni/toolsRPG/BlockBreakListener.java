package org.shyni.toolsRPG;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        System.out.println(acceptedTool(event.getPlayer()));
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        Material tool = player.getInventory().getItemInMainHand().getType();

        if(BlockXpData.shouldGiveXp(tool, block)) {
            System.out.println("you broke: " + block);
            addEnchantment(player, tool, block);
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
    public void addEnchantment(Player player, Material tool, Material block) {
        // TODO: Add option to combine levels when repairing

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        // Increment blocks broken
        int brokenBlocks = meta.getPersistentDataContainer().getOrDefault(Keys.BLOCKS_BROKEN, PersistentDataType.INTEGER, 0);
        brokenBlocks++;
        meta.getPersistentDataContainer().set(Keys.BLOCKS_BROKEN, PersistentDataType.INTEGER, brokenBlocks);

        // XP & Level
        int currentXp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
        int currentLevel = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 0);

        currentXp = currentXp + BlockXpData.getXpMultiplier(tool, block) * (int) Math.floor(ToolsSettings.getInstance().getDefaultXpMultiplier()); // XP per block broken
//        currentXp++; // XP per block
        int xpForNext = getXpForNextLevel(currentLevel);

        if (currentXp >= xpForNext) {
            currentXp -= xpForNext;
            currentLevel++;

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
        meta.displayName(Component.text("Custom Item")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        meta.lore(List.of(
                Component.text("This is a custom item").color(NamedTextColor.GRAY),
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
        //TODO : MAKE WEAPONS AND TOOLS SEPERATE!
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
