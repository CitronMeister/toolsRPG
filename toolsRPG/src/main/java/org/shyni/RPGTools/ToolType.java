package org.shyni.RPGTools;

import org.bukkit.Material;

public enum ToolType {
    AXE,
    PICKAXE,
    SHOVEL,
    HOE,
    SWORD;

    public static ToolType fromMaterial(Material material) {
        String name = material.name();
        if (name.endsWith("_AXE")) return AXE;
        if (name.endsWith("_PICKAXE")) return PICKAXE;
        if (name.endsWith("_SHOVEL")) return SHOVEL;
        if (name.endsWith("_HOE")) return HOE;
        if (name.endsWith("_SWORD")) return SWORD;
        return null;
    }
}
