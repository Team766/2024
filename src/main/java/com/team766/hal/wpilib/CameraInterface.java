package com.team766.hal.wpilib;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.VideoSource;
import org.opencv.core.Mat;

public class CameraInterface implements com.team766.hal.CameraInterface {

    private CvSource vidSource;

    @Override
    public void startAutomaticCapture() {
        try {
            CameraServer.startAutomaticCapture(VideoSource.enumerateSources()[0]);
        } catch (Exception e) {
            Logger.get(Category.CAMERA).logRaw(Severity.ERROR, e.toString());
        }
    }

    @Override
    public void getFrame(final Mat img) {
        CameraServer.getVideo().grabFrame(img);
    }

    @Override
    public void putFrame(final Mat img) {
        if (vidSource == null) {
            vidSource = CameraServer.putVideo("VisionTracking", img.width(), img.height());
        }

        vidSource.putFrame(img);
    }
}
