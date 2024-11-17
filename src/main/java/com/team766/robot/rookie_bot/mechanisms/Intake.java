package com.team766.robot.rookie_bot.mechanisms;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;


public class  Intake extends Mechanism  {
    public MotorController intakeWheelLeft;
    public MotorController intakeWheelRight;

public Intake(){
    intakeWheelLeft = RobotProvider.instance.getMotor("drive.intakeWheelLeft");
    intakeWheelRight = RobotProvider.instance.getMotor("drive.intakeWheelRight");
}
public void intake(double leftPowerIntake, double rightPowerIntake) {
        intakeWheelLeft.set(leftPowerIntake);
        intakeWheelRight.set(rightPowerIntake);
    }
    
}