package com.team766.robot.reva;

import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.IntakeUntilIn;

public class BoxOpOI extends OIFragment {
    private final JoystickReader gamepad;

    public BoxOpOI(OI oi, JoystickReader gamepad) {
        super(oi);
        this.gamepad = gamepad;
    }

    @Override
    protected void dispatch() {
        if (gamepad.getButton(InputConstants.XBOX_A) && gamepad.getButton(InputConstants.XBOX_B)) {
            // climber

            // move the shoulder out of the way
            onceAvailable((Superstructure ss) -> ss.setGoal(Shoulder.RotateToPosition.TOP));

            // if the sticks are being moving, move the corresponding climber(s)
            if (Math.abs(gamepad.getAxis(InputConstants.XBOX_LS_Y)) > InputConstants.XBOX_DEADZONE
                    || Math.abs(gamepad.getAxis(InputConstants.XBOX_RS_Y))
                            > InputConstants.XBOX_DEADZONE) {
                whileAvailable(
                        (Superstructure ss) -> {
                            boolean overrideSoftLimits =
                                    gamepad.getButton(InputConstants.XBOX_X)
                                            && gamepad.getButton(InputConstants.XBOX_Y);
                            ss.setGoal(
                                    new Climber.MotorPowers(
                                            gamepad.getAxis(InputConstants.XBOX_LS_Y),
                                            gamepad.getAxis(InputConstants.XBOX_RS_Y),
                                            overrideSoftLimits));
                        });
            } else {
                whileAvailable((Superstructure ss) -> ss.setGoal(new Climber.Stop()));
            }
        } else {
            onceAvailable(
                    (Superstructure ss) -> {
                        // restore the shoulder (and stop the climber)
                        ss.setGoal(new Shoulder.RotateToPosition(85));
                    });

            // shoulder positions
            if (gamepad.getButton(InputConstants.XBOX_A)) {
                // intake
                whileAvailable(
                        (Superstructure ss) -> ss.setGoal(Shoulder.RotateToPosition.INTAKE_FLOOR));
            } else if (gamepad.getButton(InputConstants.XBOX_B)) {
                // shoot closer to speaker
                whileAvailable(
                        (Superstructure ss) -> ss.setGoal(Shoulder.RotateToPosition.SHOOT_LOW));
            } else if (gamepad.getButton(InputConstants.XBOX_X)) {
                // amp shot
                whileAvailable((Superstructure ss) -> ss.setGoal(Shoulder.RotateToPosition.AMP));
            } else if (gamepad.getButton(InputConstants.XBOX_Y)) {
                // shooter assist
                whileAvailable(
                        (Superstructure ss) ->
                                ss.setGoal(Shoulder.RotateToPosition.SHOOTER_ASSIST));
                // Currently it will only modify the speed if the right trigger is already held.
                // TODO: Make this more tolerant for when Y is pressed before right trigger.
                if (getStatus(Shooter.Status.class).get().targetSpeed() != 0.0) {
                    whileAvailable(
                            (Shooter shooter) ->
                                    shooter.setGoal(Shooter.ShootAtSpeed.SHOOTER_ASSIST_SPEED));
                }
            } else if (gamepad.getPOV() == 0) {
                whileAvailable((Superstructure ss) -> ss.setGoal(new Shoulder.NudgeUp()));
            } else if (gamepad.getPOV() == 180) {
                whileAvailable((Superstructure ss) -> ss.setGoal(new Shoulder.NudgeDown()));
            }
        }

        // shooter
        if (gamepad.getAxis(InputConstants.XBOX_RT) > 0) {
            whileAvailable((Shooter shooter) -> shooter.setGoal(new Shooter.ShootAtSpeed(4800)));
        }
        byDefault((Shooter shooter) -> shooter.setGoal(new Shooter.Stop()));

        // intake
        if (gamepad.getButton(InputConstants.XBOX_RB)) {
            whileAvailable((Intake intake) -> intake.setGoal(new Intake.Out()));
        }
        if (gamepad.getButton(InputConstants.XBOX_LB)) {
            whileAvailable((Intake intake) -> new IntakeUntilIn(intake));
        }
        byDefault((Intake intake) -> intake.setGoal(new Intake.Stop()));

        // rumble
        // if (intake.getStatus().hasNoteInIntake()) {
        //   ((GenericHID) gamepad).setRumble(RumbleType.kBothRumble, 0.5);
        // }
    }
}
