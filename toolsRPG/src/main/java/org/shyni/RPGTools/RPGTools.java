package org.shyni.RPGTools;

import org.bukkit.plugin.java.JavaPlugin;

public final class RPGTools extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

        // Commands
        getCommand("rpgtools").setExecutor(new RPGToolCommand());

        // Load config on plugin enable
        ToolsSettings.getInstance().load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin disabled");


    }

    public static RPGTools getInstance(){ return getPlugin(RPGTools.class); }
}
