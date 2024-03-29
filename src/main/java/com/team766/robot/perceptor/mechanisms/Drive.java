package com.team766.robot.perceptor.mechanisms;

import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive extends Mechanism {
    private final MotorController leftMotor;
    private final MotorController rightMotor;

    public Drive() {
        leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
    }

    @Override
    public void run() {
        CANSparkMax leftSpark = (CANSparkMax) leftMotor;
        CANSparkMax rightSpark = (CANSparkMax) rightMotor;
        SmartDashboard.putString("Event", DriverStation.getEventName());
        SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());
        SmartDashboard.putString("Match Type", DriverStation.getMatchType().toString());
        SmartDashboard.putNumber("Match Number", DriverStation.getMatchNumber());
        SmartDashboard.putNumber("Left Motor Temp", leftSpark.getMotorTemperature());
        SmartDashboard.putNumber("Right Motor Temp", rightSpark.getMotorTemperature());
    }
}
