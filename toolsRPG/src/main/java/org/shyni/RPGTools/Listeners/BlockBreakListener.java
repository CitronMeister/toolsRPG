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
import org.shyni.RPGTools.Settings.BlockXpSettings;
import org.shyni.RPGTools.util.Keys;
import org.shyni.RPGTools.Settings.ToolsSettings;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        Material tool = player.getInventory().getItemInMainHand().getType();

        if(BlockXpSettings.getInstance().shouldGiveXp(tool, block)) {
            LevelManager.updateToolItem(player, tool, block);
        }
    }
}
