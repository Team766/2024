package com.team766.ViSIONbase;

import java.util.ArrayList;

public class StaticCameras {

    public static final CameraPlus camera2 = new CameraPlus("MainTestCamera2024");
    public static ArrayList<CameraPlus> apriltagCameras =
            new ArrayList<CameraPlus>() {
                {
                    add(camera2);
                }
            };

    public static final NoteCamera noteDetectorCamera = new NoteCamera("Note Detection Camera");
}
