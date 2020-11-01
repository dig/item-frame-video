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

@Log
public class FrameRunnable extends BukkitRunnable {

    private final ItemFrame[][] itemFrames;
    private final Set<Player> viewers;
    private final MapWrapper[] mapWrappers;

    private int frame = 0;

    public FrameRunnable(@NonNull ItemFrame[][] itemFrames,
                         @NonNull Set<Player> viewers,
                         @NonNull MapWrapper[] mapWrappers) {
        this.itemFrames = itemFrames;
        this.viewers = viewers;
        this.mapWrappers = mapWrappers;
    }

    @Override
    public void run() {
        if (mapWrappers.length > frame) {
            MapWrapper mapWrapper = mapWrappers[frame];
            MultiMapController mapController = (MultiMapController) mapWrapper.getController();
            viewers.forEach(player -> mapController.showInFrames(player, itemFrames));
            frame++;
        } else {
            this.cancel();
        }
    }
}
