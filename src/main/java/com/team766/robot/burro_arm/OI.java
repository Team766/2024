package com.team766.robot.burro_arm;

import static com.team766.framework.RulePersistence.ONCE;
import static com.team766.framework.RulePersistence.ONCE_AND_HOLD;
import static com.team766.framework.RulePersistence.REPEATEDLY;
import static com.team766.robot.burro_arm.constants.InputConstants.*;

import com.team766.framework.Rule;
import com.team766.framework.RuleEngine;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.burro_arm.mechanisms.*;
import com.team766.robot.burro_arm.procedures.*;
import com.team766.robot.common.mechanisms.BurroDrive;
import java.util.Set;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends RuleEngine {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;

    @Override
    public Category getLoggerCategory() {
        return Category.OPERATOR_INTERFACE;
    }

    public OI(BurroDrive drive, Arm arm, Gripper gripper) {
        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        joystick2 = RobotProvider.instance.getJoystick(2);

        // Add driver controls here.

        addRule(Rule.create("Drive Robot", () -> true)
                .withOnTriggeringProcedure(REPEATEDLY, Set.of(drive), () -> {
                    drive.setRequest(new BurroDrive.ArcadeDrive(
                            -joystick0.getAxis(AXIS_FORWARD_BACKWARD) * 0.5,
                            -joystick0.getAxis(AXIS_TURN) * 0.3));
                }));

        addRule(Rule.create("Arm Up", () -> joystick0.getButton(BUTTON_ARM_UP))
                .withOnTriggeringProcedure(
                        ONCE, Set.of(arm), () -> arm.setRequest(Arm.makeNudgeUp())));
        addRule(Rule.create("Arm Down", () -> joystick0.getButton(BUTTON_ARM_DOWN))
                .withOnTriggeringProcedure(
                        ONCE, Set.of(arm), () -> arm.setRequest(Arm.makeNudgeDown())));

        addRule(Rule.create("Intake", () -> joystick0.getButton(BUTTON_INTAKE))
                .withOnTriggeringProcedure(
                        ONCE_AND_HOLD,
                        Set.of(gripper),
                        () -> gripper.setRequest(new Gripper.Intake())));
        addRule(Rule.create("Outtake", () -> joystick0.getButton(BUTTON_OUTTAKE))
                .withOnTriggeringProcedure(
                        ONCE_AND_HOLD,
                        Set.of(gripper),
                        () -> gripper.setRequest(new Gripper.Outtake())));
    }
}
