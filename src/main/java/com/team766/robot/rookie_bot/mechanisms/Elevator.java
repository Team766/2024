package com.team766.robot.rookie_bot.mechanisms;

import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;

public class Elevator extends Mechanism{
    private MotorController m_elevator;
    private EncoderReader m_elevatorEncoder;
    private final double TOP_LIMIT = -130;
    private final double BOTTOM_LIMIT = 0;


    public Elevator() {
        m_elevator = RobotProvider.instance.getMotor("elevator");
        m_elevatorEncoder = RobotProvider.instance.getEncoder("elevator_encoder");
        ((CANSparkMaxMotorController) m_elevator).setSmartCurrentLimit(10, 80, 200);
        resetEncoder();
    }

    public void move(double power) {
        if (!((power < 0 && (getElevatorDistance() < TOP_LIMIT)) || (power > 0 && (getElevatorDistance() > BOTTOM_LIMIT)))) {
            m_elevator.set(-power);
        } else {
            m_elevator.set(0);
        }
        
    }

    public double getElevatorDistance(){
        return m_elevator.getSensorPosition();
    }

    public void resetEncoder(){
        m_elevator.setSensorPosition(0);
    }

    public void run() {
        log("elevator: " + getElevatorDistance());
    }
}

