package com.team766.robot.rookie_bot.mechanisms;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class  Intake extends Mechanism  {
    public MotorController intakeWheelLeft;
    public MotorController intakeWheelRight;
    double leftPowerIntake= 0.75;
    double rightPowerIntake= -0.75;
public Intake(){
    intakeWheelLeft = RobotProvider.instance.getMotor("intakeWheelLeft");
    intakeWheelRight = RobotProvider.instance.getMotor("intakeWheelRight");
}
public void intake() {
    
        intakeWheelLeft.set(leftPowerIntake);
        intakeWheelRight.set(rightPowerIntake);
    }
    public void inout(boolean in){
        if(in==false){
            intakeWheelLeft.set(leftPowerIntake*-1);
            intakeWheelRight.set(rightPowerIntake*-1);
        }
        else{
            intakeWheelLeft.set(leftPowerIntake);
            intakeWheelRight.set(rightPowerIntake);
        }
        SmartDashboard.putBoolean("Way", in);
        SmartDashboard.putNumber("leftPower", leftPowerIntake);
        SmartDashboard.putNumber("rightPower", rightPowerIntake);
//Make three new methods instead of 1: In, Out and Stop
    }
}