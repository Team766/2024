package com.team766.robot.rookie_bot.mechanisms;

import com.revrobotics.CANSparkMax;
import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;
import com.team766.framework.Mechanism;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.rookie_bot.procedures.*;

public class Intake extends Mechanism {
    private MotorController intakeChainLeft;
    private MotorController intakeChainRight;
    private final double INNER_LIMIT = 20;
    private final double OUTER_LIMIT = -400;

    public Intake() {
        intakeChainLeft = RobotProvider.instance.getMotor("intake.ChainLeft");
        intakeChainRight = RobotProvider.instance.getMotor("intake.ChainRight");
        ((CANSparkMax) intakeChainLeft).setSmartCurrentLimit(10, 80, 200);
        ((CANSparkMax) intakeChainRight).setSmartCurrentLimit(10, 80, 200);

        intakeChainLeft.setSensorPosition(0);
        intakeChainRight.setSensorPosition(0);

    }

    public void setIntake(double leftPower, double rightPower) {
        if (!((leftPower > 0 && intakeChainLeft.getSensorPosition() > INNER_LIMIT) || (leftPower < 0 && intakeChainLeft.getSensorPosition() < OUTER_LIMIT))) {
            intakeChainLeft.set(leftPower);
        } else {
            intakeChainLeft.set(0);
        }
        
        if (!((rightPower > 0 && intakeChainLeft.getSensorPosition() > INNER_LIMIT) || (rightPower < 0 && intakeChainLeft.getSensorPosition() < OUTER_LIMIT))) {
            intakeChainRight.set(rightPower);
        } else {
            intakeChainRight.set(0);
        }
    }

    public void setPowerBoth(double powerBoth) {
        setIntake(powerBoth, powerBoth);
    }
        
}

