package org.shyni.RPGTools;

import org.bukkit.plugin.java.JavaPlugin;
import org.shyni.RPGTools.Commands.RPGToolCommand;
import org.shyni.RPGTools.Listeners.BlockBreakListener;
import org.shyni.RPGTools.Listeners.MobKillListener;
import org.shyni.RPGTools.Settings.ToolsSettings;
import org.shyni.RPGTools.util.BlockXpData;
import org.shyni.RPGTools.util.MobXpData;

public final class RPGTools extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);

        // Commands
        getCommand("rpgtools").setExecutor(new RPGToolCommand());

        // Load config on plugin enable
        ToolsSettings.getInstance().load();
        MobXpData.getInstance().load();
        BlockXpData.getInstance().load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin disabled");


    }

    public static RPGTools getInstance(){ return getPlugin(RPGTools.class); }
}
