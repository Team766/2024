package com.team766.rookie_bot.mechanisms;

import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;
import com.team766.hal.EncoderReader;

public class Elevator extends Mechanism{
    private SpeedController m_elevator;
    private EncoderReader m_elevatorEncoder;

    public Elevator() {
        m_elevator = RobotProvider.instance.getMotor("elevator");
        m_elevatorEncoder = RobotProvider.instance.getEncoder("elevator_encoder");
        resetEncoder();
    }

    public void move(double power){
        m_elevator.set(power);
    }

    public double getElevatorDistance(){
        return m_elevatorEncoder.getDistance();
    }

    public void resetEncoder(){
        m_elevatorEncoder.reset();
    }
}
