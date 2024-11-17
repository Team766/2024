package com.team766.robot.rookie_bot.mechanisms;


import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.MotorController;
import com.team766.hal.EncoderReader;

public class Elevator extends Mechanism{
    private MotorController m_elevator;
    private EncoderReader m_elevatorEncoder;

    public Elevator() {
        m_elevator = RobotProvider.instance.getMotor("elevator");

        ((CANSparkMaxMotorController)m_elevator).setSmartCurrentLimit(10, 80, 200);

        m_elevatorEncoder = RobotProvider.instance.getEncoder("elevator_encoder");
        resetEncoder();

    }

    public void move(double power){
        m_elevator.set(power);
    }

    public double getElevatorDistance(){
        return m_elevator.getSensorPosition();
    }

    public void resetEncoder(){
        m_elevatorEncoder.reset();
    }

}

