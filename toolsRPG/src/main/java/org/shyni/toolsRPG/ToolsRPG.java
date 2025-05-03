package org.shyni.toolsRPG;

import org.bukkit.plugin.java.JavaPlugin;

public final class ToolsRPG extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin disabled");

    }

    public static ToolsRPG getInstance(){ return getPlugin(ToolsRPG.class); }
}
