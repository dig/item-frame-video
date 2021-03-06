package com.github.dig.video.decoder;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface Decoder {

    boolean read(InputStream in);

    int getFrameCount();

    BufferedImage getNextFrame();

    double getFrameRate();

    void close();

}
