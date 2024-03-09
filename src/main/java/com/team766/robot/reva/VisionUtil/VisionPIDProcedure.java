package com.team766.robot.reva.VisionUtil;

import com.team766.ViSIONbase.AnywhereScoringPosition;
import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.controllers.PIDController;
import com.team766.framework.Procedure;
import java.util.ArrayList;

public abstract class VisionPIDProcedure extends Procedure {
    protected PIDController xPID = new PIDController(0.4, 0, 0, 0, -0.75, 0.75, 0.02);
    protected PIDController yPID = new PIDController(0.18, 0, 0, 0, -0.75, 0.75, 0.02);

    protected PIDController yawPID = new PIDController(0.02, 0.001, 0, 0, -0.25, 0.25, 3);

    protected PIDController anglePID = new PIDController(0.01, 0, 0, 0, -0.25, 0.25, 1);

    /*
     * Scoringposition distances need to be in sequential order. Ie, the first one added needs to be the closest distance.
     */
    private static AnywhereScoringPosition a1 = new AnywhereScoringPosition(2.185, 0.75, 20);
    private static AnywhereScoringPosition a2 = new AnywhereScoringPosition(2.791, 0.95, 25);
    private static AnywhereScoringPosition a3 = new AnywhereScoringPosition(3.140, 1, 32.22);
    private static AnywhereScoringPosition a4 = new AnywhereScoringPosition(3.631, 1, 32.5);
    private static AnywhereScoringPosition a5 = new AnywhereScoringPosition(4.001, 1, 32.25);
    private static AnywhereScoringPosition a6 = new AnywhereScoringPosition(0, 0, 0);
    private static AnywhereScoringPosition a7 = new AnywhereScoringPosition(0, 0, 0);
    private static AnywhereScoringPosition a8 = new AnywhereScoringPosition(0, 0, 0);
    private static AnywhereScoringPosition a9 = new AnywhereScoringPosition(0, 0, 0);
    private static AnywhereScoringPosition a10 = new AnywhereScoringPosition(0, 0, 0);
    

    protected static ArrayList<AnywhereScoringPosition> scoringPositions =
            new ArrayList<AnywhereScoringPosition>() {
                {
                    add(a1);
                    add(a2);
                    add(a3);
                    add(a4);
                    add(a5);
                    // add(a6);
                    // add(a7);
                    // add(a8);
                    // add(a9);
                    // add(a10);
                }
            };

    public static double getBestPowerToUse(double distanceFromCenterApriltag)
            throws AprilTagGeneralCheckedException {
        for (int i = 0; i < scoringPositions.size(); i++) {
            if (distanceFromCenterApriltag
                    <= scoringPositions.get(i).distanceFromCenterApriltag()) {
                if (i == 0) {
                    return scoringPositions.get(i).powerToSetShooter();
                }
                double powerToUse =
                        ((scoringPositions.get(i).powerToSetShooter()
                                                * (distanceFromCenterApriltag
                                                        - scoringPositions
                                                                .get(i - 1)
                                                                .distanceFromCenterApriltag()))
                                        + (scoringPositions.get(i - 1).powerToSetShooter()
                                                * (scoringPositions
                                                                .get(i)
                                                                .distanceFromCenterApriltag()
                                                        - distanceFromCenterApriltag)))
                                / (scoringPositions.get(i).distanceFromCenterApriltag()
                                        - scoringPositions.get(i - 1).distanceFromCenterApriltag());
                return powerToUse;
            }
        }
        throw new AprilTagGeneralCheckedException("No sutiable shooter power found.");
    }

    public static double getBestArmAngleToUse(double distanceFromCenterApriltag)
            throws AprilTagGeneralCheckedException {
        for (int i = 0; i < scoringPositions.size(); i++) {
            if (distanceFromCenterApriltag
                    <= scoringPositions.get(i).distanceFromCenterApriltag()) {
                if (i == 0) {
                    return scoringPositions.get(i).angleToSetArm();
                }

                double angleToUse =
                        ((scoringPositions.get(i).angleToSetArm()
                                                * (distanceFromCenterApriltag
                                                        - scoringPositions
                                                                .get(i - 1)
                                                                .distanceFromCenterApriltag()))
                                        + (scoringPositions.get(i - 1).angleToSetArm()
                                                * (scoringPositions
                                                                .get(i)
                                                                .distanceFromCenterApriltag()
                                                        - distanceFromCenterApriltag)))
                                / (scoringPositions.get(i).distanceFromCenterApriltag()
                                        - scoringPositions.get(i - 1).distanceFromCenterApriltag());

                return angleToUse;
            }
        }
        throw new AprilTagGeneralCheckedException("No sutiable arm angle found.");
    }
}
