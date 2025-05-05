package org.shyni.RPGTools.util;

import org.bukkit.Material;

public enum ToolType {
    AXE,
    PICKAXE,
    SHOVEL,
    HOE,
    SWORD,
    BOW,
    CROSSBOW,
    TRIDENT,
    MACE;

    public boolean isWeapon(){
        return switch (this) {
            case SWORD, TRIDENT, BOW, CROSSBOW, MACE -> true;
            default -> false;
        };
    }


    public static ToolType fromMaterial(Material material) {
        String name = material.name();
        if (name.endsWith("_AXE")) return AXE;
        if (name.endsWith("_PICKAXE")) return PICKAXE;
        if (name.endsWith("_SHOVEL")) return SHOVEL;
        if (name.endsWith("_HOE")) return HOE;
        if (name.endsWith("_SWORD")) return SWORD;
        if (name.endsWith("TRIDENT")) return TRIDENT;
        if (name.endsWith("BOW")) return BOW;
        if (name.endsWith("CROSSBOW")) return CROSSBOW;
        if (name.endsWith("MACE")) return MACE;

        return null;
    }
}
