package com.team766.robot.candle_bot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.robot.candle_bot.Robot;
import com.team766.robot.candle_bot.Robot;

public class Elevator extends Mechanism{
    private CANSparkMaxMotorController elevatormotor;
    private double targetPos; 

public Elevator(){
    elevatormotor = (CANSparkMaxMotorController) RobotProvider.instance.getMotor("Elevator.elevatormotor");
    elevatormotor.setSmartCurrentLimit(10, 80, 200);
    elevatormotor.setSensorPosition(0);
}
    public void setPosition(double position) {
        checkContextOwnership();
        targetPos = position;
        elevatormotor.set(MotorController.ControlMode.Position, position);
    }
    public void run(){
        if (Math.abs(elevatormotor.getSensorPosition() - targetPos) < 1){
            Robot.candle.LED();
        } else {
            Robot.candle.stop();
        }
        log("elevator encoder: " + elevatormotor.getSensorPosition());
    }
    
}

