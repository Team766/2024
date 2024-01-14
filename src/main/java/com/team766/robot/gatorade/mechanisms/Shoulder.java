package com.team766.robot.gatorade.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Basic shoulder mechanism.  Rotates the {@link Elevator} to different angles, to allow it (and the
 * attached {@link Wrist} and {@link Intake}) to reach different positions, from the floor to different
 * heights of nodes.
 */
public class Shoulder extends Mechanism {

    /**
     * Pre-set positions for the shoulder.
     */
    public enum Position {

        // TODO: adjust these!

        /** Shoulder is at the highest achievable position. */
        TOP(45),

        /** Shoulder is in position to intake from the substation or score in the upper nodes. */
        RAISED(40),

        /** Shoulder is in position to intake and outtake pieces from/to the floor. */
        FLOOR(10),

        /** Shoulder is fully down.  Starting position. **/
        BOTTOM(0);

        private final double angle;

        Position(double angle) {
            this.angle = angle;
        }

        public double getAngle() {
            return angle;
        }
    }

    private static final double NUDGE_INCREMENT = 5.0;
    private static final double NUDGE_DAMPENER = 0.15;

    private static final double NEAR_THRESHOLD = 5.0;

    private final CANSparkMax leftMotor;
    private final CANSparkMax rightMotor;
    private final SparkPIDController pidController;
    private final ValueProvider<Double> pGain;
    private final ValueProvider<Double> iGain;
    private final ValueProvider<Double> dGain;
    private final ValueProvider<Double> ffGain;
    private final ValueProvider<Double> maxVelocity;
    private final ValueProvider<Double> minOutputVelocity;
    private final ValueProvider<Double> maxAccel;

    private final RateLimiter rateLimiter = new RateLimiter(1.0 /* seconds */);

    /**
     * Constructs a new Shoulder.
     */
    public Shoulder() {
        MotorController halLeftMotor = RobotProvider.instance.getMotor(SHOULDER_LEFT_MOTOR);
        MotorController halRightMotor = RobotProvider.instance.getMotor(SHOULDER_RIGHT_MOTOR);

        if (!((halLeftMotor instanceof CANSparkMax) && (halRightMotor instanceof CANSparkMax))) {
            log(Severity.ERROR, "Motors are not CANSparkMaxes!");
            throw new IllegalStateException("Motor are not CANSparkMaxes!");
        }

        halLeftMotor.setNeutralMode(NeutralMode.Brake);
        halRightMotor.setNeutralMode(NeutralMode.Brake);

        leftMotor = (CANSparkMax) halLeftMotor;
        rightMotor = (CANSparkMax) halRightMotor;

        rightMotor.follow(leftMotor, true /* invert */);

        leftMotor
                .getEncoder()
                .setPosition(EncoderUtils.shoulderDegreesToRotations(Position.BOTTOM.getAngle()));

        pidController = leftMotor.getPIDController();
        pidController.setFeedbackDevice(leftMotor.getEncoder());

        pGain = ConfigFileReader.getInstance().getDouble(SHOULDER_PGAIN);
        iGain = ConfigFileReader.getInstance().getDouble(SHOULDER_IGAIN);
        dGain = ConfigFileReader.getInstance().getDouble(SHOULDER_DGAIN);
        ffGain = ConfigFileReader.getInstance().getDouble(SHOULDER_FFGAIN);
        maxVelocity = ConfigFileReader.getInstance().getDouble(SHOULDER_MAX_VELOCITY);
        minOutputVelocity = ConfigFileReader.getInstance().getDouble(SHOULDER_MIN_OUTPUT_VELOCITY);
        maxAccel = ConfigFileReader.getInstance().getDouble(SHOULDER_MAX_ACCEL);
    }

    public double getRotations() {
        return leftMotor.getEncoder().getPosition();
    }

    /**
     * Returns the current angle of the wrist.
     */
    public double getAngle() {
        return EncoderUtils.shoulderRotationsToDegrees(leftMotor.getEncoder().getPosition());
    }

    public boolean isNearTo(Position position) {
        return isNearTo(position.getAngle());
    }

    public boolean isNearTo(double angle) {
        return Math.abs(angle - getAngle()) < NEAR_THRESHOLD;
    }

    public void nudgeNoPID(double value) {
        checkContextOwnership();
        double clampedValue = MathUtil.clamp(value, -1, 1);
        clampedValue *= NUDGE_DAMPENER; // make nudges less forceful. TODO: make this non-linear
        leftMotor.set(clampedValue);
    }

    public void stopShoulder() {
        checkContextOwnership();
        leftMotor.set(0);
    }

    public void nudgeUp() {
        System.err.println("Nudging up.");
        double angle = getAngle();
        double targetAngle = Math.min(angle + NUDGE_INCREMENT, Position.TOP.getAngle());

        rotate(targetAngle);
    }

    public void nudgeDown() {
        System.err.println("Nudging down.");
        double angle = getAngle();
        double targetAngle = Math.max(angle - NUDGE_INCREMENT, Position.BOTTOM.getAngle());
        rotate(targetAngle);
    }

    /**
     * Rotates the wrist to a pre-set {@link Position}.
     */
    public void rotate(Position position) {
        rotate(position.getAngle());
    }

    /**
     * Starts rotating the wrist to the specified angle.
     * NOTE: this method returns immediately.  Check the current wrist position of the wrist
     * with {@link #getAngle()}.
     */
    public void rotate(double angle) {
        checkContextOwnership();

        System.err.println("Setting target angle to " + angle);
        // set the PID controller values with whatever the latest is in the config
        pidController.setP(pGain.get());
        pidController.setI(iGain.get());
        pidController.setD(dGain.get());
        // pidController.setFF(ffGain.get());
        double ff = ffGain.get() * Math.cos(Math.toRadians(angle));
        SmartDashboard.putNumber("[SHOULDER] ff", ff);
        SmartDashboard.putNumber("[SHOULDER] reference", angle);

        pidController.setOutputRange(-0.4, 0.4);

        // convert the desired target degrees to rotations
        double rotations = EncoderUtils.shoulderDegreesToRotations(angle);
        SmartDashboard.putNumber("[SHOULDER] Setpoint", rotations);

        // set the reference point for the wrist
        pidController.setReference(rotations, ControlType.kPosition, 0, ff);
    }

    @Override
    public void run() {
        if (rateLimiter.next()) {
            SmartDashboard.putNumber("[SHOULDER] Angle", getAngle());
            SmartDashboard.putNumber("[SHOULDER] Rotations", getRotations());
            SmartDashboard.putNumber("[SHOULDER] Left Effort", leftMotor.getOutputCurrent());
            SmartDashboard.putNumber("[SHOULDER] Right Effort", rightMotor.getOutputCurrent());
        }
    }
}
