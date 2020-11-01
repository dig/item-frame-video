package com.github.dig.video;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class BlackRenderer extends MapRenderer {

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                mapCanvas.setPixel(x, y, (byte) 0);
            }
        }
    }
}
