package com.github.dig.video;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.mapmanager.controller.MapController;
import org.inventivetalent.mapmanager.manager.MapManager;
import org.inventivetalent.mapmanager.wrapper.MapWrapper;

import java.awt.image.BufferedImage;
import java.util.Set;

@AllArgsConstructor
public class FrameRunnable extends BukkitRunnable {

    private final MapManager mapManager = ItemFrameVideoPlugin.getInstance().getMapManager();

    private final ItemFrame itemFrame;
    private final Set<Player> viewers;
    private final MapController[] frameControllers;

    private int frame = 0;

    public FrameRunnable(@NonNull ItemFrame itemFrame,
                         @NonNull Set<Player> viewers,
                         @NonNull BufferedImage[] frames) {
        this.itemFrame = itemFrame;
        this.viewers = viewers;
        this.frameControllers = new MapController[frames.length];
        for (int i = 0; i < frames.length; i++) {
            MapWrapper mapWrapper = mapManager.wrapImage(frames[i]);
            MapController mapController = mapWrapper.getController();
            viewers.forEach(mapController::addViewer);
            viewers.forEach(mapController::sendContent);
            this.frameControllers[i] = mapController;
        }
    }

    @Override
    public void run() {
        MapController mapController = frameControllers[frame];
        viewers.forEach(player -> mapController.showInFrame(player, itemFrame));
        frame++;
    }
}
