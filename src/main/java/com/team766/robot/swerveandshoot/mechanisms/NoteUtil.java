package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;

import com.team766.ViSIONbase.*;

public class NoteUtil extends Mechanism {
	

	public NoteUtil(){
		
	}

	public void test(){
		try{
			StaticCameras.noteDetectorCamera.getTagIdOfBestTarget();

			log("yayyayay");

		} catch (AprilTagGeneralCheckedException e){
			log(e.toString());
		}
	}


}
