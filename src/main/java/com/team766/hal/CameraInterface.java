package com.team766.hal;

import org.opencv.core.Mat;

public interface CameraInterface {
    void startAutomaticCapture();

    void getFrame(Mat img);

    void putFrame(Mat img);
}
