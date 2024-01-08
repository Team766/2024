package com.team766.ViSIONbase;

import java.util.ArrayList;

public class StaticCameras {

	public static final CameraPlus camera2 = new CameraPlus("Vision Camera 2 1690");
	public static ArrayList<CameraPlus> cameras = new ArrayList<CameraPlus>(){
		{
			add(camera2);
		}
	};
}