package org.shyni.RPGTools.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class ActionBarUtil {

    private static final int TOTAL_BARS = 20;

    public static void sendXpActionBar(Player player, int currentXp, int xpToLevel, int level, boolean detailed) {
        float progress = Math.min((float) currentXp / xpToLevel, 1.0f);
        int filledBars = Math.round(progress * TOTAL_BARS);

        Component bar;

        if (detailed) {
            bar = Component.text("Level " + level + " ", NamedTextColor.YELLOW)
                    .append(Component.text("[", NamedTextColor.GRAY));

            for (int i = 0; i < TOTAL_BARS; i++) {
                bar = bar.append(Component.text("|", i < filledBars ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY));
            }

            bar = bar.append(Component.text("]", NamedTextColor.GRAY))
                    .append(Component.text(" " + currentXp + "/" + xpToLevel + " XP", NamedTextColor.YELLOW));
        } else {
            bar = Component.text("");
            for (int i = 0; i < TOTAL_BARS; i++) {
                bar = bar.append(Component.text("|", i < filledBars ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY));
            }
        }

        player.sendActionBar(bar);
    }
}
