package com.github.dig.video;

import com.github.dig.video.decoder.Decoder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.mapmanager.controller.MapController;
import org.inventivetalent.mapmanager.manager.MapManager;
import org.inventivetalent.mapmanager.wrapper.MapWrapper;

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.logging.Level;

@Log
@AllArgsConstructor
public class FrameRunnable extends BukkitRunnable {

    private final MapManager mapManager = ItemFrameVideoPlugin.getInstance().getMapManager();

    private final ItemFrame itemFrame;
    private final Set<Player> viewers;
    private final MapController[] frameControllers;

    private int frame = 0;

    public FrameRunnable(@NonNull ItemFrame itemFrame,
                         @NonNull Set<Player> viewers,
                         @NonNull Decoder decoder) {
        this.itemFrame = itemFrame;
        this.viewers = viewers;
        this.frameControllers = new MapController[decoder.getFrameCount()];
        for (int i = 0; i < 2; i++) {
            BufferedImage bufferedImage = decoder.getNextFrame();
            if (bufferedImage != null) {
                MapWrapper mapWrapper = mapManager.wrapImage(bufferedImage);
                MapController mapController = mapWrapper.getController();
                viewers.forEach(mapController::addViewer);
                viewers.forEach(mapController::sendContent);
                this.frameControllers[i] = mapController;
            } else {
                log.log(Level.SEVERE, "Null image at " + i);
            }
        }
        decoder.close();
    }

    @Override
    public void run() {
        if (frameControllers.length > frame) {
            MapController mapController = frameControllers[frame];
            viewers.forEach(player -> mapController.showInFrame(player, itemFrame));
            frame++;
        } else {
            this.cancel();
        }
    }
}
