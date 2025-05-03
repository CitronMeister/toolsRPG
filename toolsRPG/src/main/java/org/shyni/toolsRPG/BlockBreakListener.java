package org.shyni.toolsRPG;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        System.out.println(acceptedTool(event.getPlayer()));
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(acceptedTool(player)) {
            System.out.println("you broke: " + block);
            player.sendMessage("you broke a block with an accepted tool");
            sendActionBar(player); // TODO: make into xp bar

        }
    }

    public boolean acceptedTool(Player player) {
        Material type = player.getInventory().getItemInMainHand().getType();

        return switch (type) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE,
                 WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE,
                 WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
                 WOODEN_HOE, STONE_HOE, IRON_HOE, GOLDEN_HOE, DIAMOND_HOE, NETHERITE_HOE,
                 WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, FISHING_ROD -> true;
            default -> false;
        };
    }
    public void sendActionBar(Player player) {
        Component message = Component.text("You gained XP!").color(NamedTextColor.GREEN);

        player.sendActionBar(message);
    }
}
