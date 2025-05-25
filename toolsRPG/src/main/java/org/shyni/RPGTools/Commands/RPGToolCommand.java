package org.shyni.RPGTools.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shyni.RPGTools.Settings.BlockXpSettings;
import org.shyni.RPGTools.util.*;
import org.shyni.RPGTools.Settings.ToolsSettings;

import java.util.List;
import java.util.Map;

public class RPGToolCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("rpgtools.admin")) {
                sender.sendMessage(Component.text("You don’t have permission to use this.").color(NamedTextColor.RED));
                return true;
            }
            ToolsSettings.getInstance().load();
            MobXpData.getInstance().load();
            BlockXpSettings.getInstance().load();
            sender.sendMessage(Component.text("RPGTools reloaded!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("givexp")) {
            if (!sender.hasPermission("rpgtools.admin")) {
                sender.sendMessage(Component.text("You don’t have permission to use this.").color(NamedTextColor.RED));
                return true;
            }

            int xpToAdd;
            try {
                xpToAdd = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid XP amount.").color(NamedTextColor.RED));
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return true;

            ToolType type = ToolType.fromMaterial(item.getType());
            if (type == null) return true;

            boolean isWeapon = type.isWeapon();
            NamespacedKey levelKey = isWeapon ? Keys.WEAPON_LEVEL : Keys.TOOL_LEVEL;
            NamespacedKey xpKey = isWeapon ? Keys.WEAPON_XP : Keys.TOOL_XP;
            String itemType = isWeapon ? "weapon" : "tool";

            int level = meta.getPersistentDataContainer().getOrDefault(levelKey, PersistentDataType.INTEGER, 1);
            int xp = meta.getPersistentDataContainer().getOrDefault(xpKey, PersistentDataType.INTEGER, 0);
            int maxLevel = ToolsSettings.getInstance().getMaxLevel();

            xp += xpToAdd;

            while (level < maxLevel && xp >= LevelManager.getXpForNextLevel(level)) {
                xp -= LevelManager.getXpForNextLevel(level);
                level++;


                // repair
                if (ToolsSettings.getInstance().getRepairOnLevelup()) {
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(0);
                    }
                }

                // Apply new enchantments if any
                Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                        .getEnchantmentsForLevel(type, level);

                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int newLevel = entry.getValue();

                    int currentEnchantLevel = meta.hasEnchant(enchantment)
                            ? meta.getEnchantLevel(enchantment)
                            : 0;

                    if (newLevel > currentEnchantLevel) {
                        meta.addEnchant(enchantment, newLevel, true);
                    }
                }
            }

            meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
            meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
            LevelManager.updateLore(meta, type);

            item.setItemMeta(meta);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            sender.sendMessage(Component.text("Gave " + xpToAdd + " XP to " + itemType + ".").color(NamedTextColor.GREEN));

            return true;
        }



        if (args.length >= 1 && args[0].equalsIgnoreCase("levelup")) {
            if (!sender.hasPermission("rpgtools.admin")) {
                sender.sendMessage(Component.text("You don’t have permission to use this.").color(NamedTextColor.RED));
                return true;
            }
            int levelsToAdd = 1;
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            int maxLevel = ToolsSettings.getInstance().getMaxLevel();

            if (meta == null) return true;

            if (args.length == 2) {
                try {
                    levelsToAdd = Math.max(1, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("Invalid level amount.").color(NamedTextColor.RED));
                    return true;
                }
            }

            // check if weapon or tool
            ToolType type = ToolType.fromMaterial(item.getType());
            // WEAPON
            if (type != null && type.isWeapon()){
                int level = meta.getPersistentDataContainer().getOrDefault(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, 1);

                level += levelsToAdd;

                meta.getPersistentDataContainer().set(Keys.WEAPON_LEVEL, PersistentDataType.INTEGER, level);
                meta.getPersistentDataContainer().set(Keys.WEAPON_XP, PersistentDataType.INTEGER, 0);

                // repair
                if (ToolsSettings.getInstance().getRepairOnLevelup()) {
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(0);
                    }
                }


                // Apply enchantments
                Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                        .getEnchantmentsForLevel(ToolType.fromMaterial(item.getType()), level);

                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int newLevel = entry.getValue();

                    int currentEnchantLevel = meta.hasEnchant(enchantment)
                            ? meta.getEnchantLevel(enchantment)
                            : 0;

                    // Only apply if new level is higher than existing
                    if (newLevel > currentEnchantLevel) {
                        meta.addEnchant(enchantment, newLevel, true);
                    }
                }
                LevelManager.updateLore(meta, type);
                item.setItemMeta(meta);
                player.getInventory().setItemInMainHand(item);
                player.sendMessage(Component.text("Leveled weapon up to level " + level + "!").color(NamedTextColor.GOLD));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                return true;
            // TOOL
            }
            else if(type != null) {
                int level = meta.getPersistentDataContainer().getOrDefault(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, 1);
                level += levelsToAdd;

                meta.getPersistentDataContainer().set(Keys.TOOL_LEVEL, PersistentDataType.INTEGER, level);
                meta.getPersistentDataContainer().set(Keys.TOOL_XP, PersistentDataType.INTEGER, 0);

                // repair
                if (ToolsSettings.getInstance().getRepairOnLevelup()) {
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(0);
                    }
                }


                // Apply enchantments
                Map<Enchantment, Integer> enchants = ToolsSettings.getInstance()
                        .getEnchantmentsForLevel(ToolType.fromMaterial(item.getType()), level);

                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int newLevel = entry.getValue();

                    int currentEnchantLevel = meta.hasEnchant(enchantment)
                            ? meta.getEnchantLevel(enchantment)
                            : 0;

                    // Only apply if new level is higher than existing
                    if (newLevel > currentEnchantLevel) {
                        meta.addEnchant(enchantment, newLevel, true);
                    }
                }
                LevelManager.updateLore(meta, type);
                item.setItemMeta(meta);
                player.getInventory().setItemInMainHand(item);
                player.sendMessage(Component.text("Leveled tool up to level " + level + "!").color(NamedTextColor.GOLD));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                return true;
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            if(sender.hasPermission("rpgtools.admin") || sender.isOp()){
                sender.sendMessage(Component.textOfChildren(
                        Component.text("RPGTools", NamedTextColor.AQUA, TextDecoration.BOLD),
                        Component.newline(),
                        Component.text("A tool and weapon leveling system that rewards usage with XP, levels, and enchantments.", NamedTextColor.GRAY),
                        Component.newline(),
                        Component.newline(),
                        Component.text("Commands:", NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.newline(),
                        Component.text("/rpgtools help", NamedTextColor.YELLOW),
                        Component.text(" - Show this help menu", NamedTextColor.GRAY),
                        Component.newline(),
                        Component.text("/rpgtools levelup [amount]", NamedTextColor.YELLOW),
                        Component.text(" - Instantly level up your tool/weapon", NamedTextColor.GRAY),
                        Component.newline(),
                        Component.text("/rpgtools givexp [amount]", NamedTextColor.YELLOW),
                        Component.text(" - Give XP to your tool/weapon", NamedTextColor.GRAY),
                        Component.newline(),
                        Component.text("/rpgtools reload", NamedTextColor.YELLOW),
                        Component.text(" - reload the settings.yml", NamedTextColor.GRAY)
                ));
            } else {
                sender.sendMessage(Component.textOfChildren(
                        Component.text("RPGTools", NamedTextColor.AQUA, TextDecoration.BOLD),
                        Component.newline(),
                        Component.text("A tool and weapon leveling system that rewards usage with XP, levels, and enchantments.", NamedTextColor.GRAY),
                        Component.newline(),
                        Component.newline(),
                        Component.text("Commands:", NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.newline(),
                        Component.text("/rpgtools help", NamedTextColor.YELLOW),
                        Component.text(" - Show this help menu", NamedTextColor.GRAY)
                ));
            }
            return true;
        }

        if(sender.hasPermission("rpgtools.admin") || sender.isOp()){
            sender.sendMessage(Component.text("Usage:")
                    .appendNewline().append(Component.text("/rpgtools help"))
                    .appendNewline().append(Component.text("/rpgtools reload"))
                    .appendNewline().append(Component.text("/rpgtools givexp <amount>"))
                    .appendNewline().append(Component.text("/rpgtools levelup <amount>"))
                    .color(NamedTextColor.YELLOW));
        } else {
            sender.sendMessage(Component.text("Usage:")
                    .appendNewline().append(Component.text("/rpgtools help"))
                    .color(NamedTextColor.YELLOW));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1){
            if(sender.hasPermission("rpgtools.admin")){
                return List.of("reload", "givexp", "levelup", "help");
            } else {return List.of("help");}
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("givexp")) {
            if (sender.hasPermission("rpgtools.admin")) {
                return List.of("<amount>");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("levelup")) {
            if (sender.hasPermission("rpgtools.admin")) {
                return List.of("<amount>");
            }
        }

        return List.of();
    }
}
