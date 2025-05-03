package org.shyni.toolsRPG;

import org.bukkit.Material;

import javax.tools.Tool;
import java.util.HashMap;
import java.util.Map;

public class BlockXpData {

    private static final Map<ToolType, Map<Material, Double>> xpMap = new HashMap<>();

    public static void register(ToolType toolType, Material block, double xpMultiplier) {
        xpMap.computeIfAbsent(toolType, k -> new HashMap<>()).put(block, xpMultiplier);
    }

    public static boolean shouldGiveXp(Material tool, Material block) {
        ToolType toolType = ToolType.fromMaterial(tool);
        if (toolType == null) return false;

        return xpMap.getOrDefault(toolType, Map.of()).containsKey(block);
    }

    public static int getXpMultiplier(Material tool, Material block) {
        ToolType toolType = ToolType.fromMaterial(tool);
        if (toolType == null) return 0;

        double multiplier = xpMap.getOrDefault(toolType, Map.of()).getOrDefault(block, 0.0);
        System.out.println("xp before rounding " + Math.round(multiplier));
        return (int) Math.round(multiplier);
    }

    static {
        /* TODO LIST:
            1. add all default blocks
            2. add ability to add more in a config file

        * */
        // Example registrations:

        //Pickaxe stones
        register(ToolType.PICKAXE, Material.STONE, 1.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE, 1.0);

        register(ToolType.PICKAXE, Material.GRANITE, 1.0);
        register(ToolType.PICKAXE, Material.DIORITE, 1.0);
        register(ToolType.PICKAXE, Material.ANDESITE, 1.0);
        register(ToolType.PICKAXE, Material.TUFF, 1.0);
        register(ToolType.PICKAXE, Material.CALCITE, 1.0);
        register(ToolType.PICKAXE, Material.DRIPSTONE_BLOCK, 1.0);

        register(ToolType.PICKAXE, Material.SANDSTONE, 1.0);
        register(ToolType.PICKAXE, Material.RED_SANDSTONE, 1.0);
        register(ToolType.PICKAXE, Material.OBSIDIAN, 5.0);
        register(ToolType.PICKAXE, Material.TERRACOTTA, 1.0);
        register(ToolType.PICKAXE, Material.NETHERRACK, 1.0);
        register(ToolType.PICKAXE, Material.END_STONE, 1.0);
        register(ToolType.PICKAXE, Material.BLACKSTONE, 1.0);
        register(ToolType.PICKAXE, Material.BASALT, 1.0);
        register(ToolType.PICKAXE, Material.SMOOTH_BASALT, 1.0);

        //Pickaxe ores
        register(ToolType.PICKAXE, Material.COAL_ORE, 2.0);
        register(ToolType.PICKAXE, Material.IRON_ORE, 3.0);
        register(ToolType.PICKAXE, Material.GOLD_ORE, 3.0);
        register(ToolType.PICKAXE, Material.DIAMOND_ORE, 10.0);
        register(ToolType.PICKAXE, Material.EMERALD_ORE, 15.0);
        register(ToolType.PICKAXE, Material.LAPIS_ORE, 2.0);
        register(ToolType.PICKAXE, Material.REDSTONE_ORE, 2.0);
        register(ToolType.PICKAXE, Material.COPPER_ORE, 2.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_COPPER_ORE, 2.0);
        register(ToolType.PICKAXE, Material.NETHER_QUARTZ_ORE, 2.0);
        register(ToolType.PICKAXE, Material.ANCIENT_DEBRIS, 20.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_COAL_ORE, 2.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_IRON_ORE, 3.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_GOLD_ORE, 3.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_DIAMOND_ORE, 10.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_EMERALD_ORE, 15.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_LAPIS_ORE, 2.0);
        register(ToolType.PICKAXE, Material.DEEPSLATE_REDSTONE_ORE, 2.0);
        register(ToolType.PICKAXE, Material.NETHER_GOLD_ORE, 2.0);

        // AXE
        register(ToolType.AXE, Material.OAK_LOG, 1.0);
        register(ToolType.AXE, Material.SPRUCE_LOG, 1.0);
        register(ToolType.AXE, Material.BIRCH_LOG, 1.0);
        register(ToolType.AXE, Material.JUNGLE_LOG, 1.0);
        register(ToolType.AXE, Material.ACACIA_LOG, 1.0);
        register(ToolType.AXE, Material.DARK_OAK_LOG, 1.0);
        register(ToolType.AXE, Material.MANGROVE_LOG, 1.0);
        register(ToolType.AXE, Material.CHERRY_LOG, 1.0);
        register(ToolType.AXE, Material.PALE_OAK_LOG, 1.0);
        register(ToolType.AXE, Material.BAMBOO, 1.0);
        register(ToolType.AXE, Material.CRIMSON_STEM, 1.0);
        register(ToolType.AXE, Material.WARPED_STEM, 1.0);
        register(ToolType.AXE, Material.MELON, 1.0);
        register(ToolType.AXE, Material.PUMPKIN, 1.0);


        // SHOVEL
        register(ToolType.SHOVEL, Material.GRASS_BLOCK, 1.0);
        register(ToolType.SHOVEL, Material.COARSE_DIRT, 1.0);
        register(ToolType.SHOVEL, Material.DIRT_PATH, 1.0);
        register(ToolType.SHOVEL, Material.MUD, 1.0);
        register(ToolType.SHOVEL, Material.MYCELIUM, 1.0);
        register(ToolType.SHOVEL, Material.PODZOL, 1.0);
        register(ToolType.SHOVEL, Material.RED_SAND, 1.0);
        register(ToolType.SHOVEL, Material.SAND, 1.0);
        register(ToolType.SHOVEL, Material.SNOW_BLOCK, 1.0);
        register(ToolType.SHOVEL, Material.SOUL_SAND, 1.0);
        register(ToolType.SHOVEL, Material.SOUL_SOIL, 1.0);
        register(ToolType.SHOVEL, Material.DIRT, 1.0);
        register(ToolType.SHOVEL, Material.GRAVEL, 1.0);

        // HOE
        register(ToolType.HOE, Material.SHORT_GRASS, 1.0);
        register(ToolType.HOE, Material.TALL_GRASS, 1.0);
        register(ToolType.HOE, Material.SCULK, 1.0);
        register(ToolType.HOE, Material.SCULK_SHRIEKER, 5.0);
        register(ToolType.HOE, Material.SCULK_SENSOR, 3.0);
        register(ToolType.HOE, Material.SCULK_CATALYST, 5.0);
        register(ToolType.HOE, Material.NETHER_WART_BLOCK, 2.0);
        register(ToolType.HOE, Material.WARPED_WART_BLOCK, 2.0);
        register(ToolType.HOE, Material.SHROOMLIGHT, 2.0);
        register(ToolType.HOE, Material.HAY_BLOCK, 1.0);
        register(ToolType.HOE, Material.MOSS_BLOCK, 1.0);


        // SWORD
        register(ToolType.SWORD, Material.COBWEB, 1.0);
    }
}

