package com.github.dig.video;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.mapmanager.controller.MapController;
import org.inventivetalent.mapmanager.controller.MultiMapController;
import org.inventivetalent.mapmanager.wrapper.MapWrapper;

import java.util.Set;
import java.util.logging.Level;

@Log
public class FrameRunnable extends BukkitRunnable {

    private final ItemFrame[][] itemFrames;
    private final Set<Player> viewers;
    private final MapWrapper[] mapWrappers;
    private final long delay;

    private int frame = 0;

    public FrameRunnable(@NonNull ItemFrame[][] itemFrames,
                         @NonNull Set<Player> viewers,
                         @NonNull MapWrapper[] mapWrappers,
                         @NonNull long delay) {
        this.itemFrames = itemFrames;
        this.viewers = viewers;
        this.mapWrappers = mapWrappers;
        this.delay = delay;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "Starting frames");
        while (mapWrappers.length > frame) {
            MapWrapper mapWrapper = mapWrappers[frame];
            MultiMapController mapController = (MultiMapController) mapWrapper.getController();
            viewers.forEach(player -> mapController.showInFrames(player, itemFrames));
            frame++;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, "Unable to sleep thread", e);
            }
        }
        log.log(Level.INFO, "Frame stop");
        this.cancel();
    }
}
