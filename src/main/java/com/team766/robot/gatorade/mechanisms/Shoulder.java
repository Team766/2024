package com.team766.robot.gatorade.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
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
import org.littletonrobotics.junction.AutoLogOutput;

/**
 * Basic shoulder mechanism.  Rotates the {@link Elevator} to different angles, to allow it (and the
 * attached {@link Wrist} and {@link Intake}) to reach different positions, from the floor to different
 * heights of nodes.
 */
public class Shoulder extends Subsystem<Shoulder.State, Shoulder.Goal> {

    /**
     * @param angle the current angle of the wrist.
     */
    public record State(@AutoLogOutput double rotations, @AutoLogOutput double angle) {
        public boolean isNearTo(RotateToPosition position) {
            return isNearTo(position.angle());
        }

        public boolean isNearTo(double angle) {
            return Math.abs(angle - this.angle()) < NEAR_THRESHOLD;
        }
    }

    public sealed interface Goal {}

    public record NudgeNoPID(double value) implements Goal {}

    public record StopShoulder() implements Goal {}

    public record NudgeUp() implements Goal {}

    public record NudgeDown() implements Goal {}

    /**
     * Starts rotating the wrist to the specified angle.
     */
    public record RotateToPosition(double angle) implements Goal {
        // TODO: adjust these!

        /** Shoulder is at the highest achievable position. */
        public static final RotateToPosition TOP = new RotateToPosition(45);

        /** Shoulder is in position to intake from the substation or score in the upper nodes. */
        public static final RotateToPosition RAISED = new RotateToPosition(40);

        /** Shoulder is in position to intake and outtake pieces from/to the floor. */
        public static final RotateToPosition FLOOR = new RotateToPosition(10);

        /** Shoulder is fully down.  Starting position. **/
        public static final RotateToPosition BOTTOM = new RotateToPosition(0);
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
                .setPosition(
                        EncoderUtils.shoulderDegreesToRotations(RotateToPosition.BOTTOM.angle()));

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

    @Override
    protected State updateState() {
        return new State(
                leftMotor.getEncoder().getPosition(),
                EncoderUtils.shoulderRotationsToDegrees(leftMotor.getEncoder().getPosition()));
    }

    @Override
    protected void dispatch(State state, Goal goal) {
        switch (goal) {
            case NudgeNoPID nudge -> {
                double clampedValue = MathUtil.clamp(nudge.value, -1, 1);
                clampedValue *=
                        NUDGE_DAMPENER; // make nudges less forceful. TODO: make this non-linear
                leftMotor.set(clampedValue);
            }
            case StopShoulder s -> {
                leftMotor.set(0);
            }
            case NudgeUp n -> {
                double targetAngle =
                        Math.min(state.angle + NUDGE_INCREMENT, RotateToPosition.TOP.angle());

                setGoal(new RotateToPosition(targetAngle));
            }
            case NudgeDown n -> {
                double targetAngle =
                        Math.max(state.angle - NUDGE_INCREMENT, RotateToPosition.BOTTOM.angle());
                setGoal(new RotateToPosition(targetAngle));
            }
            case RotateToPosition position -> {
                // set the PID controller values with whatever the latest is in the config
                pidController.setP(pGain.get());
                pidController.setI(iGain.get());
                pidController.setD(dGain.get());
                // pidController.setFF(ffGain.get());
                double ff = ffGain.get() * Math.cos(Math.toRadians(position.angle));
                SmartDashboard.putNumber("[SHOULDER] ff", ff);
                SmartDashboard.putNumber("[SHOULDER] reference", position.angle);

                pidController.setOutputRange(-0.4, 0.4);

                // convert the desired target degrees to rotations
                double rotations = EncoderUtils.shoulderDegreesToRotations(position.angle);
                SmartDashboard.putNumber("[SHOULDER] Setpoint", rotations);

                // set the reference point for the wrist
                pidController.setReference(rotations, ControlType.kPosition, 0, ff);
            }
        }
    }
}
