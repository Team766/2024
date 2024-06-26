package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Mechanism;

public class ForwardApriltagCamera extends Mechanism {

    private GrayScaleCamera camera;

    public ForwardApriltagCamera() {

        camera = new GrayScaleCamera("Main_Test_Camera_2024");
    }

    public GrayScaleCamera getCamera() {
        return camera;
    }
}
