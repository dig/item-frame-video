package com.github.dig.video.decoder;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;

@Log
public class MP4Decoder implements Decoder {

    private final Java2DFrameConverter converter = new Java2DFrameConverter();

    private FFmpegFrameGrabber video;

    @Override
    public boolean read(InputStream in) {
        video = new FFmpegFrameGrabber(in);
        try {
            video.setFormat("mp4");
            video.start();
        } catch (FrameGrabber.Exception e) {
            log.log(Level.SEVERE, "Unable to decode mp4", e);
            return false;
        }
        return true;
    }

    @Override
    public int getFrameCount() {
        return video.getLengthInFrames();
    }

    @Override
    public BufferedImage getNextFrame() {
        Frame frame = null;
        try {
            frame = video.grabImage();
        } catch (FrameGrabber.Exception e) {
            log.log(Level.SEVERE, "Unable to get next frame", e);
        }
        return converter.convert(frame);
    }

    @Override
    public double getFrameRate() {
        return video.getFrameRate();
    }

    @Override
    public void close() {
        try {
            video.stop();
            video.close();
        } catch (FrameGrabber.Exception e) {
            log.log(Level.SEVERE, "Unable to close decoder", e);
        }
    }
}
