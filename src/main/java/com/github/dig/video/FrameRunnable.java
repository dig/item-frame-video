package com.github.dig.video;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.mapmanager.controller.MapController;
import org.inventivetalent.mapmanager.wrapper.MapWrapper;

import java.util.Set;

@Log
public class FrameRunnable extends BukkitRunnable {

    private final ItemFrame itemFrame;
    private final Set<Player> viewers;
    private final MapWrapper[] mapWrappers;

    private int frame = 0;

    public FrameRunnable(@NonNull ItemFrame itemFrame,
                         @NonNull Set<Player> viewers,
                         @NonNull MapWrapper[] mapWrappers) {
        this.itemFrame = itemFrame;
        this.viewers = viewers;
        this.mapWrappers = mapWrappers;
    }

    @Override
    public void run() {
        if (mapWrappers.length > frame) {
            MapWrapper mapWrapper = mapWrappers[frame];
            MapController mapController = mapWrapper.getController();
            viewers.forEach(player -> mapController.showInFrame(player, itemFrame));
            frame++;
        } else {
            this.cancel();
        }
    }
}
