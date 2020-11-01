package com.github.dig.video;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
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
    }
}
