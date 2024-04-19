package com.team766.hal.mock;

import com.team766.hal.CameraReader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class MockCamera implements CameraReader {

    private String nextImage;

    @Override
    public Mat getImage() {
        if (nextImage == null) {
            return null;
        }

        return Imgcodecs.imread(nextImage);
    }

    public void setNextImage(final String nextImage_) {
        this.nextImage = this.getClass().getClassLoader().getResource(nextImage_).getPath();
    }
}
