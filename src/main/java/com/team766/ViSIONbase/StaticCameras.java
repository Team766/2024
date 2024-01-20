package com.team766.ViSIONbase;

import java.util.ArrayList;

public class StaticCameras {

    public static final CameraPlus camera2 = new CameraPlus("Vision Camera 2 1690");
    public static ArrayList<CameraPlus> apriltagCameras =
            new ArrayList<CameraPlus>() {
                {
                    add(camera2);
                }
            };

    public static final CameraPlus noteDetectorCamera = new CameraPlus("Note Detection Camera");
}
