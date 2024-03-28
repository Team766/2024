package com.team766.robot.reva.constants;

import com.pathplanner.lib.util.GeometryUtil;
import edu.wpi.first.math.geometry.Translation2d;

public class VisionConstants {

    private VisionConstants() {}

    public static final int MAIN_BLUE_SPEAKER_TAG = 7;
    public static final int MAIN_RED_SPEAKER_TAG = 4;

    public static final Translation2d MAIN_BLUE_SPEAKER_TAG_POS = new Translation2d(0, 5.5);
    public static final Translation2d MAIN_RED_SPEAKER_TAG_POS =
            GeometryUtil.flipFieldPosition(MAIN_BLUE_SPEAKER_TAG_POS);
}
