package com.github.dig.video;

import com.github.dig.video.decoder.Decoder;
import com.github.dig.video.decoder.MP4Decoder;
import com.github.dig.video.exception.PlayerAlreadyPlayingException;
import com.github.dig.video.exception.VideoReadException;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.mapmanager.controller.MapController;
import org.inventivetalent.mapmanager.manager.MapManager;
import org.inventivetalent.mapmanager.wrapper.MapWrapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

@Log
public class ItemFramePlayer {

    private final MapManager mapManager = ItemFrameVideoPlugin.getInstance().getMapManager();

    private final Plugin plugin;
    @Getter
    private final ItemFrame[][] itemFrames;
    @Getter
    private final File video;

    private final Set<Player> viewers;
    private final int width;
    private final int height;

    private Decoder decoder;
    private MapWrapper[] mapWrappers;
    private BukkitRunnable runnable;

    public ItemFramePlayer(@NonNull Plugin plugin,
                           @NonNull ItemFrame[][] itemFrames,
                           @NonNull File video)
            throws FileNotFoundException, UnsupportedOperationException, VideoReadException {
        this.plugin = plugin;
        this.itemFrames = itemFrames;
        this.video = video;
        this.viewers = new HashSet<>();
        this.width = itemFrames.length;
        this.height = itemFrames[0].length;
        init();
    }

    private void init() throws FileNotFoundException, UnsupportedOperationException, VideoReadException {
        Optional<String> fileTypeOptional = getExtension(video.getName());
        if (fileTypeOptional.isPresent()) {
            switch (fileTypeOptional.get()) {
                case "mp4":
                    decoder = new MP4Decoder();
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported file type");
            }

            if (decoder != null && !decoder.read(new FileInputStream(video))) {
                throw new VideoReadException("Unable to read video");
            } else {
                this.mapWrappers = new MapWrapper[decoder.getFrameCount() / 4];
                int next = 0;
                for (int i = 0; i < decoder.getFrameCount(); i++) {
                    BufferedImage bufferedImage = decoder.getNextFrame();
                    if (i % 4 == 0 && this.mapWrappers.length > next) {
                        bufferedImage = scaleImage(bufferedImage);
                        MapWrapper mapWrapper = mapManager.wrapMultiImage(bufferedImage,
                                height, width);
                        this.mapWrappers[next] = mapWrapper;
                        next++;
                    }
                }
                decoder.close();
            }
        } else {
            throw new UnsupportedOperationException("Unsupported file type");
        }
    }

    public void addViewer(@NonNull Player player) {
        log.log(Level.INFO, "addViewer 1");
        viewers.add(player);
        log.log(Level.INFO, "mapWrappers.length = " + mapWrappers.length);
        for (MapWrapper mapWrapper : mapWrappers) {
            MapController mapController = mapWrapper.getController();
            mapController.addViewer(player);
            mapController.sendContent(player);
        }
    }

    public void play() {
        if (isPlaying())
            throw new PlayerAlreadyPlayingException("Player already running");

        long delay = (long) Math.max((double) 1000 / decoder.getFrameRate(), 16.6);
        System.out.println("delay = " + delay);
        runnable = new FrameRunnable(itemFrames, viewers, mapWrappers, delay * 4);
        runnable.runTaskAsynchronously(plugin);
    }

    public boolean isPlaying() {
        return runnable != null && !runnable.isCancelled();
    }

    private Optional<String> getExtension(@NonNull String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }

    private BufferedImage scaleImage(@NonNull BufferedImage original) {
        int type = original.getType();
        BufferedImage scaledImage = new BufferedImage(128 * this.width, 128 * this.height, type);
        Graphics scaledGraphics = scaledImage.getGraphics();

        Image instance = original.getScaledInstance(128 * this.width, 128 * this.height, Image.SCALE_FAST);
        scaledGraphics.drawImage(instance, 0, 0, null);

        instance.flush();
        scaledGraphics.dispose();
        return scaledImage;
    }

    private void save(@NonNull String fileName, @NonNull BufferedImage frame) {
        try {
            ImageIO.write(frame, "jpeg", new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to save frame", e);
        }
    }
}
