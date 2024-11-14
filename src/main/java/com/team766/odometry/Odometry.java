package com.team766.odometry;

import com.team766.hal.GyroReader;
import com.team766.library.RateLimiter;
import com.team766.robot.common.mechanisms.SwerveModule;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/*
/*
 * Method which calculates the position of the robot based on wheel positions.
 */
public class Odometry {

    private static final double RATE_LIMITER_TIME = 0.05;

    // probably good at every 100th of a second but test
    private RateLimiter odometryLimiter;

    private GyroReader gyro;
    private SwerveModule[] moduleList;
    // The order of CANCoders should be the same as in motorList
    private int moduleCount;

    private Rotation2d[] prevWheelRotation;
    private Rotation2d[] wheelRotationChange;

    private double[] prevDriveDisplacement;
    private double[] driveDisplacementChange;
    

    private Rotation2d gyroPosition;


    // In meters
    private double wheelCircumference;
    public double gearRatio;
    public int encoderToRevolutionConstant;

    // In the same order as motorList, relative to the center of the robot
    private Translation2d[] wheelPositions;

    /**
     * Constructor for Odometry, taking in several defines for the robot.
     * @param gyro The gyro sensor used to determine heading, etc.
     * @param motors A list of every wheel-controlling motor on the robot.
     * @param CANCoders A list of the CANCoders corresponding to each wheel, in the same order as motors.
     * @param wheelLocations A list of the locations of each wheel, in the same order as motors.
     * @param wheelCircumference The circumfrence of the wheels, including treads.
     * @param gearRatio The gear ratio of the wheels.
     * @param encoderToRevolutionConstant The encoder to revolution constant of the wheels.
     */
    public Odometry(
            GyroReader gyro,
            SwerveModule[] moduleList,
            Translation2d[] wheelLocations,
            double wheelCircumference,
            double gearRatio,
            int encoderToRevolutionConstant) {

        this.gyro = gyro;
        odometryLimiter = new RateLimiter(RATE_LIMITER_TIME);
        this.moduleList = moduleList;
        moduleCount = moduleList.length;

        prevWheelRotation = new Rotation2d[moduleCount];
        wheelRotationChange = new Rotation2d[moduleCount];

        prevDriveDisplacement = new double[moduleCount];
        driveDisplacementChange = new double[moduleCount];

        wheelPositions = wheelLocations;
        this.wheelCircumference = wheelCircumference;
        this.gearRatio = gearRatio;
        this.encoderToRevolutionConstant = encoderToRevolutionConstant;

        for (int i = 0; i < moduleCount; i++) {
            prevWheelRotation[i] = new Rotation2d();
            wheelRotationChange[i] = new Rotation2d();

            prevDriveDisplacement[i] = 0;
            driveDisplacementChange[i] = 0;
        }
    }

    /**
     * Updates the odometry encoder values to the robot encoder values.
     */
    private void updateDisplacementAndRotation() {
        for (int i = 0; i < moduleCount; i++) {
            Rotation2d currentWheelRotation = gyroPosition.plus(moduleList[i].getSteerAngle());
            wheelRotationChange[i] = currentWheelRotation.minus(prevWheelRotation[i]);
            prevWheelRotation[i] = currentWheelRotation;

            double currentDriveDisplacement = moduleList[i].getDriveDisplacement();
            driveDisplacementChange[i] = currentDriveDisplacement - prevDriveDisplacement[i];
            prevDriveDisplacement[i] = currentDriveDisplacement;
        }
    }

    /**
     * Updates the position of each wheel of the robot by assuming each wheel moved in an arc.
     */
    public Translation2d predictCurrentPositionChange() {
        double radius;
        double deltaX;
        double deltaY;
        gyroPosition = Rotation2d.fromDegrees(gyro.getAngle());

        double sumX = 0;
        double sumY = 0;

        updateDisplacementAndRotation();
        
        for (int i = 0; i < moduleCount; i++) {

            double yaw = Math.toRadians(gyro.getAngle());
            double roll = Math.toRadians(gyro.getRoll());
            double pitch = Math.toRadians(gyro.getPitch());

            double w = moduleList[i].getSteerAngle().getRadians();
            Vector2D u =
                    new Vector2D(Math.cos(yaw) * Math.cos(pitch), Math.sin(yaw) * Math.cos(pitch));
            Vector2D v =
                    new Vector2D(
                            Math.cos(yaw) * Math.sin(pitch) * Math.sin(roll)
                                    - Math.sin(yaw) * Math.cos(roll),
                            Math.sin(yaw) * Math.sin(pitch) * Math.sin(roll)
                                    + Math.cos(yaw) * Math.cos(roll));
            Vector2D a = u.scalarMultiply(Math.cos(w)).add(v.scalarMultiply(Math.sin(w)));
            Vector2D b = u.scalarMultiply(-Math.sin(w)).add(v.scalarMultiply(Math.cos(w)));
            Vector2D wheelMotion;
            // log("u: " + u + " v: " + v + " a: " + a + " b: " + b);

            // double oldWheelX;
            // double oldWheelY;

            
            if (Math.abs(wheelRotationChange[i].getDegrees()) != 0) {
                // estimates the bot moved in a circle to calculate new position
                radius = driveDisplacementChange[i] / wheelRotationChange[i].getRadians(); 

                deltaX = radius * Math.sin(wheelRotationChange[i].getRadians());
                deltaY = radius * (1 - Math.cos(wheelRotationChange[i].getRadians()));

                wheelMotion = a.scalarMultiply(deltaX).add(b.scalarMultiply(-deltaY));

            } else {
                wheelMotion = a.scalarMultiply(driveDisplacementChange[i]);

            }
            wheelMotion =
                    wheelMotion.scalarMultiply(
                            wheelCircumference / (gearRatio * encoderToRevolutionConstant));
            
            sumX += wheelMotion.getX();
            sumY += wheelMotion.getY();
        }

        return new Translation2d(sumX /  moduleCount, sumY / moduleCount);
    }
}
