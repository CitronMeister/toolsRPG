package org.shyni.RPGTools.Listeners;
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
import org.shyni.RPGTools.LevelManager;
import org.shyni.RPGTools.util.ActionBarUtil;
import org.shyni.RPGTools.util.BlockXpData;
import org.shyni.RPGTools.util.Keys;
import org.shyni.RPGTools.util.ToolType;
import org.shyni.RPGTools.Settings.ToolsSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        Material tool = player.getInventory().getItemInMainHand().getType();

        if(BlockXpData.shouldGiveXp(tool, block)) {
            LevelManager.updateToolItem(player, tool, block);
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int xp = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);
                int level = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 1);
                int xpToLevel = LevelManager.getXpForNextLevel(level);
                if (level < ToolsSettings.getInstance().getMaxLevel()){
                    ActionBarUtil.sendXpActionBar(player, xp, xpToLevel, level, true);
                }
            }
        }
    }
}
