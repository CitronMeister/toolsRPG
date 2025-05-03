package org.shyni.toolsRPG;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToolRPGCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("only players can use this command");
        }


        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            ToolsSettings.getInstance().load();
            sender.sendMessage(Component.text("ToolsRPG reloaded!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            return true;
        }



        return true;
    }
}
