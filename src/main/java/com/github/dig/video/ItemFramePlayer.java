package com.github.dig.video;

import com.github.dig.video.decoder.Decoder;
import com.github.dig.video.decoder.MP4Decoder;
import com.github.dig.video.exception.VideoReadException;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.ItemFrame;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

public class ItemFramePlayer {

    @Getter
    private final ItemFrame itemFrame;
    @Getter
    private final File video;

    private Decoder decoder;
    private BukkitRunnable runnable;

    public ItemFramePlayer(@NonNull ItemFrame itemFrame,
                           @NonNull File video)
            throws FileNotFoundException, UnsupportedOperationException, VideoReadException {
        this.itemFrame = itemFrame;
        this.video = video;
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
            }
        } else {
            throw new UnsupportedOperationException("Unsupported file type");
        }
    }

    public void play() {
        runnable = new FrameRunnable();
    }

    public boolean isPlaying() {
        return runnable == null || !runnable.isCancelled();
    }

    private Optional<String> getExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }
}
