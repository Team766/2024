package com.team766.robot.gatorade.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Subsystem;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Basic wrist mechanism.  Used in conjunction with the {@link Intake} and {@link Elevator}.
 * Can be moved up and down as part of teleop or autonomous control to move the {@link Intake}
 * (attached to the end of the Wrist) closer to a game piece or game element (eg node in the
 * field, human player station), at which point the {@link Intake} can grab or release the game
 * piece as appropriate.
 */
public class Wrist extends Subsystem<Wrist.Status, Wrist.Goal> {

    /**
     * @param angle the current angle of the wrist.
     */
    public record Status(double rotations, double angle) {
        public boolean isNearTo(RotateToPosition position) {
            return isNearTo(position.angle());
        }

        public boolean isNearTo(double angle) {
            return Math.abs(angle - this.angle()) < NEAR_THRESHOLD;
        }
    }

    public sealed interface Goal {}

    public record NudgeNoPID(double value) implements Goal {}

    public record Stop() implements Goal {}

    public record HoldPosition() implements Goal {}

    public record NudgeUp() implements Goal {}

    public record NudgeDown() implements Goal {}

    /**
     * Starts rotating the wrist to the specified angle.
     */
    public record RotateToPosition(double angle) implements Goal {
        /** Wrist is in top position.  Starting position. */
        public static final RotateToPosition TOP = new RotateToPosition(-180);

        /** Wrist is in the position for moving around the field. */
        public static final RotateToPosition RETRACTED = new RotateToPosition(-175.0);

        /** Wrist is level with ground. */
        public static final RotateToPosition LEVEL = new RotateToPosition(-65);

        public static final RotateToPosition HIGH_NODE = new RotateToPosition(-20);
        public static final RotateToPosition MID_NODE = new RotateToPosition(-25.5);
        public static final RotateToPosition HUMAN_CONES = new RotateToPosition(-4);
        public static final RotateToPosition HUMAN_CUBES = new RotateToPosition(-8);

        /** Wrist is fully down. **/
        public static final RotateToPosition BOTTOM = new RotateToPosition(60);
    }

    private static final double NUDGE_INCREMENT = 5.0;
    private static final double NUDGE_DAMPENER = 0.15;

    private static final double NEAR_THRESHOLD = 5.0;

    private final CANSparkMax motor;
    private final SparkPIDController pidController;
    private final ValueProvider<Double> pGain;
    private final ValueProvider<Double> iGain;
    private final ValueProvider<Double> dGain;
    private final ValueProvider<Double> ffGain;

    /**
     * Contructs a new Wrist.
     */
    public Wrist() {
        MotorController halMotor = RobotProvider.instance.getMotor(WRIST_MOTOR);
        if (!(halMotor instanceof CANSparkMax)) {
            log(Severity.ERROR, "Motor is not a CANSparkMax!");
            throw new IllegalStateException("Motor is not a CANSparkMax!");
        }
        motor = (CANSparkMax) halMotor;

        motor.getEncoder()
                .setPosition(EncoderUtils.wristDegreesToRotations(RotateToPosition.TOP.angle()));

        // stash the PIDController for convenience.  will update the PID values to the latest from
        // the config
        // file each time we use the motor.
        pidController = motor.getPIDController();
        pidController.setFeedbackDevice(motor.getEncoder());

        // grab config values for PID.
        pGain = ConfigFileReader.getInstance().getDouble(WRIST_PGAIN);
        iGain = ConfigFileReader.getInstance().getDouble(WRIST_IGAIN);
        dGain = ConfigFileReader.getInstance().getDouble(WRIST_DGAIN);
        ffGain = ConfigFileReader.getInstance().getDouble(WRIST_FFGAIN);
    }

    @Override
    protected Status updateState() {
        return new Status(
                motor.getEncoder().getPosition(),
                EncoderUtils.wristRotationsToDegrees(motor.getEncoder().getPosition()));
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        switch (goal) {
            case NudgeNoPID nudge -> {
                if (!goalChanged) break;
                double clampedValue = MathUtil.clamp(nudge.value, -1, 1);
                clampedValue *=
                        NUDGE_DAMPENER; // make nudges less forceful. TODO: make this non-linear
                motor.set(clampedValue);
            }
            case Stop s -> {
                if (!goalChanged) break;
                motor.stopMotor();
            }
            case HoldPosition s -> {
                if (!goalChanged) break;
                applyPID(status.angle);
            }
            case NudgeUp n -> {
                double targetAngle =
                        Math.max(status.angle - NUDGE_INCREMENT, RotateToPosition.TOP.angle());

                setGoal(new RotateToPosition(targetAngle));
            }
            case NudgeDown n -> {
                double targetAngle =
                        Math.min(status.angle + NUDGE_INCREMENT, RotateToPosition.BOTTOM.angle());
                setGoal(new RotateToPosition(targetAngle));
            }
            case RotateToPosition position -> {
                applyPID(position.angle);
            }
        }
    }

    private void applyPID(double targetAngle) {
        // set the PID controller values with whatever the latest is in the config
        pidController.setP(pGain.get());
        pidController.setI(iGain.get());
        pidController.setD(dGain.get());
        // pidController.setFF(ffGain.get());
        double ff = ffGain.get() * Math.cos(Math.toRadians(targetAngle));
        SmartDashboard.putNumber("[WRIST] ff", ff);
        SmartDashboard.putNumber("[WRIST] reference", targetAngle);

        pidController.setOutputRange(-1, 1);

        // convert the desired target degrees to rotations
        double rotations = EncoderUtils.wristDegreesToRotations(targetAngle);

        // set the reference point for the wrist
        pidController.setReference(rotations, ControlType.kPosition, 0, ff);
    }
}
