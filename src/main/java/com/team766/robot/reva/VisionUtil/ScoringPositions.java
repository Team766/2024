package com.team766.robot.reva.VisionUtil;

import com.team766.ViSIONbase.ScoringPosition;
import java.util.ArrayList;

public class ScoringPositions {
    public static final ScoringPosition RED_ALLIANCE_STAGE_LEFT =
            new ScoringPosition(0, 0, 0, 0, 0, 11);
    public static final ScoringPosition RED_ALLIANCE_STAGE_RIGHT =
            new ScoringPosition(0, 0, 0, 0, 0, 12);
    public static final ScoringPosition RED_ALLIANCE_CENTERSTAGE =
            new ScoringPosition(0, 0, 0, 0, 0, 13);

    public static final ScoringPosition BLUE_ALLIANCE_CENTERSTAGE =
            new ScoringPosition(0, 0, 0, 0, 0, 14);
    public static final ScoringPosition BLUE_ALLIANCE_STAGE_LEFT =
            new ScoringPosition(0, 0, 0, 0, 0, 15);
    public static final ScoringPosition BLUE_ALLIANCE_STAGE_RIGHT =
            new ScoringPosition(0, 0, 0, 0, 0, 16);

    public static final ArrayList<ScoringPosition> trapScoringPositions =
            new ArrayList<ScoringPosition>() {
                {
                    add(RED_ALLIANCE_STAGE_LEFT);
                    add(RED_ALLIANCE_STAGE_RIGHT);
                    add(RED_ALLIANCE_CENTERSTAGE);
                    add(BLUE_ALLIANCE_CENTERSTAGE);
                    add(BLUE_ALLIANCE_STAGE_LEFT);
                    add(BLUE_ALLIANCE_STAGE_RIGHT);
                }
            };
}
