package com.team766.ViSIONbase;

import com.team766.framework.AprilTagGeneralCheckedException;

public class VisionUtil{
	public static CameraPlus findCameraThatHas(int tagId) throws AprilTagGeneralCheckedException{

		for(CameraPlus camera : StaticCameras.cameras){
			if(camera.getTagIdOfBestTarget() == tagId){
				return camera;
			}
		}

		throw new AprilTagGeneralCheckedException("No cameras had the camera with tagId: " + tagId);
	}

}

