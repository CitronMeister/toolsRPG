package org.shyni.RPGTools.Listeners;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.shyni.RPGTools.util.LevelManager;
import org.shyni.RPGTools.util.ActionBarUtil;
import org.shyni.RPGTools.util.BlockXpData;
import org.shyni.RPGTools.util.Keys;
import org.shyni.RPGTools.Settings.ToolsSettings;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        Material tool = player.getInventory().getItemInMainHand().getType();

        if(BlockXpData.getInstance().shouldGiveXp(tool, block)) {
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
