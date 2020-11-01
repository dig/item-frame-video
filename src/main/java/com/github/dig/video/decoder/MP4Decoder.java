package com.github.dig.video.decoder;

import lombok.NonNull;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MP4Decoder implements Decoder {

    private BufferedImage[] frames;

    @Override
    public boolean read(InputStream in) {
        FFmpegFrameGrabber video = new FFmpegFrameGrabber(in);
        try {
            video.start();
            frames = new BufferedImage[video.getFrameNumber()];
            for (int i = 0; i < video.getFrameNumber(); i++) {
                Frame image = video.grab();
                frames[i] = toBufferedImage(image);
            }
            video.stop();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public int getFrameCount() {
        return frames.length;
    }

    @Override
    public BufferedImage getFrame(int frame) {
        return frames[frame];
    }

    @Override
    public BufferedImage[] getFrames() {
        return frames;
    }

    public BufferedImage toBufferedImage(@NonNull Frame frame) {
        Java2DFrameConverter bimConverter = new Java2DFrameConverter();
        BufferedImage img = bimConverter.convert(frame);
        BufferedImage result = (BufferedImage) img.getScaledInstance(
                img.getWidth(), img.getHeight(), java.awt.Image.SCALE_DEFAULT);
        img.flush();
        return result;
    }
}
