package com.github.dig.video;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.mapmanager.MapManagerPlugin;
import org.inventivetalent.mapmanager.manager.MapManager;

public class ItemFrameVideoPlugin extends JavaPlugin {

    @Getter
    private static ItemFrameVideoPlugin instance;

    @Getter
    private MapManager mapManager;

    public ItemFrameVideoPlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("MapManager") != null) {
            MapManagerPlugin mapManagerPlugin = (MapManagerPlugin) pluginManager.getPlugin("MapManager");
            mapManager = mapManagerPlugin.getMapManager();
        } else {
            System.out.println("[ItemFrameVideo] Unable to find MapManager, disabling...");
            pluginManager.disablePlugin(this);
        }
    }
}
