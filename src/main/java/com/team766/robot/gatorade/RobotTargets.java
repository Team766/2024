package com.team766.robot.gatorade;

import edu.wpi.first.math.geometry.Translation2d;

/**
 * Constants defining translation2ds the robot may want to travel to
 */
public final class RobotTargets {

    private static final double BLUE_Y = 1.88595;
    private static final double RED_Y = 14.6558;

    private static final double ROW_1 = 0.508;
    private static final double ROW_2 = 1.071626;
    private static final double ROW_3 = 1.6256;
    private static final double ROW_4 = 2.1844;
    private static final double ROW_5 = 2.748026;
    private static final double ROW_6 = 3.302;
    private static final double ROW_7 = 3.8608;
    private static final double ROW_8 = 4.424426;
    private static final double ROW_9 = 4.9784;

    // Translation2ds corresponding to nodes, in the format: NODES_COLOR_GRID_COLUMN
    // This is from the perspective of the driver
    public static final Translation2d NODES_BLUE_RIGHT_RIGHT = new Translation2d(BLUE_Y, ROW_1);
    public static final Translation2d NODES_BLUE_RIGHT_CENTER = new Translation2d(BLUE_Y, ROW_2);
    public static final Translation2d NODES_BLUE_RIGHT_LEFT = new Translation2d(BLUE_Y, ROW_3);
    public static final Translation2d NODES_BLUE_CENTER_RIGHT = new Translation2d(BLUE_Y, ROW_4);
    public static final Translation2d NODES_BLUE_CENTER_CENTER = new Translation2d(BLUE_Y, ROW_5);
    public static final Translation2d NODES_BLUE_CENTER_LEFT = new Translation2d(BLUE_Y, ROW_6);
    public static final Translation2d NODES_BLUE_LEFT_RIGHT = new Translation2d(BLUE_Y, ROW_7);
    public static final Translation2d NODES_BLUE_LEFT_CENTER = new Translation2d(BLUE_Y, ROW_8);
    public static final Translation2d NODES_BLUE_LEFT_LEFT = new Translation2d(BLUE_Y, ROW_9);

    public static final Translation2d NODES_RED_LEFT_LEFT = new Translation2d(RED_Y, ROW_1);
    public static final Translation2d NODES_RED_LEFT_CENTER = new Translation2d(RED_Y, ROW_2);
    public static final Translation2d NODES_RED_LEFT_RIGHT = new Translation2d(RED_Y, ROW_3);
    public static final Translation2d NODES_RED_CENTER_LEFT = new Translation2d(RED_Y, ROW_4);
    public static final Translation2d NODES_RED_CENTER_CENTER = new Translation2d(RED_Y, ROW_5);
    public static final Translation2d NODES_RED_CENTER_RIGHT = new Translation2d(RED_Y, ROW_6);
    public static final Translation2d NODES_RED_RIGHT_LEFT = new Translation2d(RED_Y, ROW_7);
    public static final Translation2d NODES_RED_RIGHT_CENTER = new Translation2d(RED_Y, ROW_8);
    public static final Translation2d NODES_RED_RIGHT_RIGHT = new Translation2d(RED_Y, ROW_9);

    public static final Translation2d[] NODES = {
        NODES_BLUE_RIGHT_RIGHT,
        NODES_BLUE_RIGHT_CENTER,
        NODES_BLUE_RIGHT_LEFT,
        NODES_BLUE_CENTER_RIGHT,
        NODES_BLUE_CENTER_CENTER,
        NODES_BLUE_CENTER_LEFT,
        NODES_BLUE_LEFT_RIGHT,
        NODES_BLUE_LEFT_CENTER,
        NODES_BLUE_LEFT_LEFT,
        NODES_RED_RIGHT_RIGHT,
        NODES_RED_RIGHT_CENTER,
        NODES_RED_RIGHT_LEFT,
        NODES_RED_CENTER_RIGHT,
        NODES_RED_CENTER_CENTER,
        NODES_RED_CENTER_LEFT,
        NODES_RED_LEFT_RIGHT,
        NODES_RED_LEFT_CENTER,
        NODES_RED_LEFT_LEFT
    };

    public static final Translation2d[] CONE_NODES = {
        NODES_BLUE_RIGHT_RIGHT,
        NODES_BLUE_RIGHT_LEFT,
        NODES_BLUE_CENTER_RIGHT,
        NODES_BLUE_CENTER_LEFT,
        NODES_BLUE_LEFT_RIGHT,
        NODES_BLUE_LEFT_LEFT,
        NODES_RED_RIGHT_RIGHT,
        NODES_RED_RIGHT_LEFT,
        NODES_RED_CENTER_RIGHT,
        NODES_RED_CENTER_LEFT,
        NODES_RED_LEFT_RIGHT,
        NODES_RED_LEFT_LEFT
    };

    public static final Translation2d[] CUBE_NODES = {
        NODES_BLUE_RIGHT_CENTER,
        NODES_BLUE_CENTER_CENTER,
        NODES_BLUE_LEFT_CENTER,
        NODES_RED_RIGHT_CENTER,
        NODES_RED_CENTER_CENTER,
        NODES_RED_LEFT_CENTER,
    };

    public static final double[] CUBE_ROWS = {ROW_2, ROW_5, ROW_8};
}
