package com.team766.robot.rookie_bot.mechanisms;


import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.MotorController;

public class Elevator extends Mechanism{
    private MotorController m_elevator;

    public Elevator() {
        m_elevator = RobotProvider.instance.getMotor("elevator");

        ((CANSparkMaxMotorController) m_elevator).setSmartCurrentLimit(10, 80, 200);
        resetEncoder();
    }

    public void move(double power){
        m_elevator.set(power);
    }

    public double getElevatorDistance(){
        return m_elevator.getSensorPosition();
    }

    public void resetEncoder(){
        m_elevator.setSensorPosition(0);
    }

}

