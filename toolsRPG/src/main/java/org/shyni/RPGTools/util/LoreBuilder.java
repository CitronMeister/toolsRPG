package org.shyni.RPGTools.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoreBuilder {

    public static List<Component> buildWeaponLore(int level, int xp, int maxLevel, int xpForNext) {
        List<Component> lore = new ArrayList<>();

        lore.add(Component.text("Weapon Level: " + level + "/" + maxLevel).color(NamedTextColor.RED));

        if (level < maxLevel) {
            lore.add(Component.text("XP: " + xp + "/" + xpForNext).color(NamedTextColor.GOLD));
        } else {
            lore.add(Component.text("MAX LEVEL").color(NamedTextColor.GOLD));
        }

        return lore;
    }

    public static List<Component> buildToolLore(int level, int xp, int maxLevel, int xpForNext, Component rarityStars) {
        List<Component> lore = new ArrayList<>();

        lore.add(rarityStars);
        lore.add(Component.text("Tool Level: " + level + "/" + maxLevel).color(NamedTextColor.AQUA));

        if (level < maxLevel) {
            lore.add(Component.text("XP: " + xp + "/" + xpForNext).color(NamedTextColor.GREEN));
        } else {
            lore.add(Component.text("MAX LEVEL").color(NamedTextColor.GOLD));
        }

        return lore;
    }

    public static void applyLore(ItemMeta meta, List<Component> lore) {
        meta.lore(lore);
    }
}
