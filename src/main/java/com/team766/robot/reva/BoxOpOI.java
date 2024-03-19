package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Climber.ClimberPosition;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;
import com.team766.robot.reva.procedures.IntakeUntilIn;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class BoxOpOI extends OIFragment {
    // allows soft limits to be overridden when true 
    private boolean climberOverride;
    // allows climber to move
    private boolean canClimb;

    private final JoystickReader gamepad;
    // private final XboxController xboxController;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
    private final Climber climber;

    private final OICondition shooterShoot;
    private final OICondition intakeOut;
    private final OICondition intakeIn;
    private final OICondition climberClimb;

    public BoxOpOI(
            JoystickReader gamepad,
            // XboxController xboxController,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Climber climber) {
        this.gamepad = gamepad;
        /// this.xboxController = xboxController;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;

        shooterShoot = new OICondition(() -> gamepad.getAxis(InputConstants.XBOX_RT) > 0);
        intakeOut = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_RB));
        intakeIn = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_LB));
        climberClimb =
                new OICondition(
                        () ->
                                Math.abs(gamepad.getAxis(InputConstants.XBOX_LS_Y))
                                                > InputConstants.XBOX_DEADZONE
                                        || Math.abs(gamepad.getAxis(InputConstants.XBOX_RS_Y))
                                                > InputConstants.XBOX_DEADZONE);
    }

    @Override
    protected void handleOI(Context context) {
        // climber override
        if(gamepad.getButtonPressed(InputConstants.XBOX_A) && gamepad.getButtonPressed(InputConstants.XBOX_B) && gamepad.getButtonPressed(InputConstants.XBOX_X) && gamepad.getButtonPressed(InputConstants.XBOX_Y)){
            context.takeOwnership(climber);
            climber.enableSoftLimits(false);
            climberOverride = true;
            context.releaseOwnership(climber);
        } else {
            context.takeOwnership(climber);
            climber.enableSoftLimits(true);
            context.releaseOwnership(climber);
            climberOverride = false;
        }

        // climber condition
        if(gamepad.getButtonPressed(InputConstants.XBOX_A) && gamepad.getButtonPressed(InputConstants.XBOX_B)){
            canClimb = true;
        } else {
            canClimb = false;
        }

        // shoulder positions
        if(!climberOverride && !canClimb){
            if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
                context.takeOwnership(shoulder);
                shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
            } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
                context.takeOwnership(shoulder);
                shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
            } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
                context.takeOwnership(shoulder);
                shoulder.rotate(ShoulderPosition.TOP);
            } else if (gamepad.getButtonPressed(InputConstants.XBOX_Y)) {
                context.takeOwnership(shoulder);
                shoulder.rotate(ShoulderPosition.SHOOT_LOW);
            } else if (gamepad.getPOV() == 0) {
                context.takeOwnership(shoulder);
                shoulder.nudgeUp();
            } else if (gamepad.getPOV() == 180) {
                context.takeOwnership(shoulder);
                shoulder.nudgeDown();
            } else {
                context.releaseOwnership(shoulder);
            }
        }
        

        // climber
  
            if (climberClimb.isTriggering() && canClimb) {
                if (climberClimb.isNewlyTriggering()) {
                    context.takeOwnership(climber);
                }
                climber.setLeftPower(gamepad.getAxis(InputConstants.XBOX_LS_Y));
                climber.setRightPower(gamepad.getAxis(InputConstants.XBOX_RS_Y));
            } else if (climberClimb.isFinishedTriggering()) {
                climber.stop();
                context.releaseOwnership(climber);
            }
       
        

        // shooter
        if (shooterShoot.isNewlyTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot();
        } else if (shooterShoot.isFinishedTriggering()) {
            shooter.stop();
            context.releaseOwnership(shooter);
        }

        // intake
        if (intakeOut.isNewlyTriggering()) {
            context.takeOwnership(intake);
            intake.out();
        } else if (intakeIn.isNewlyTriggering()) {
            context.takeOwnership(intake);
            context.startAsync(new IntakeUntilIn());
        } else if (intakeOut.isFinishedTriggering() || intakeIn.isFinishedTriggering()) {
            intake.stop();
            context.releaseOwnership(intake);
        }

        // rumble
        if(Robot.intake.hasNoteInIntake()){
            // xboxController.setRumble(RumbleType.kBothRumble, 0.5);
        }



    }
}
