package com.team766.robot.candle_bot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Elevator extends Mechanism{
    private MotorController elevatormotor;

public Elevator(){
    elevatormotor = RobotProvider.instance.getMotor("Elevator.elevatormotor");
    elevatormotor.setSensorPosition(0);
}
    public void setPosition(double position) {
        checkContextOwnership();
        elevatormotor.set(MotorController.ControlMode.Position, position);
    }
    public void run(){
        log("elevator encoder: " + elevatormotor.getSensorPosition());
    }
    
}

