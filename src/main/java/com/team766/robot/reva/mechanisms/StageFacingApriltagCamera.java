package com.team766.robot.reva.mechanisms;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Mechanism;

public class StageFacingApriltagCamera extends Mechanism {

	private GrayScaleCamera camera;

	public StageFacingApriltagCamera() {
		camera = new GrayScaleCamera("Main_Test_Camera_2024");
	}

	public GrayScaleCamera getCamera() {
		return camera;
	}
}
