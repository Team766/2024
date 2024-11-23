package com.team766.robot.rookie_bot.mechanisms;

import com.revrobotics.CANSparkMax;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.robot.rookie_bot.Robot;

public class Elevator extends Mechanism {
    private MotorController m_elevator;
    private EncoderReader m_elevatorEncoder;
    double setpoint;
    PIDController controller;

    public Elevator() {
        m_elevator = RobotProvider.instance.getMotor("elevator");
        m_elevatorEncoder = RobotProvider.instance.getEncoder("elevator_encoder");
        ((CANSparkMaxMotorController) m_elevator).setSmartCurrentLimit(10, 80, 200);
        resetEncoder();
        controller = new PIDController(0.03, 0, 0, -1, 1, 1);
        setpoint = getElevatorDistance();
    }

    public void move(double power) {
        m_elevator.set(power);
    }

    public double getElevatorDistance() {
        return m_elevator.getSensorPosition();
    }

    public void resetEncoder() {
        m_elevatorEncoder.reset();
    }

    public void moveElevator(boolean up){
        //BOTTOM: 0 
        //TOP: 219
        setpoint = getElevatorDistance();
        if (up) {
            setpoint+=35;
        } else {
            setpoint= setpoint - 35;
        }

        if (setpoint < 0) setpoint = 0;
        if (setpoint > 219) setpoint = 219;

        controller.setSetpoint(setpoint);
    }

    public void run(){
       
            controller.calculate(
                    getElevatorDistance()); // pass the feedback into the PID
            double motor_effort =
                    controller.getOutput(); // get the PID controller output for this cycle
            Robot.elevator.move(motor_effort); // MOVE THE
            // ELEVATOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
            // (that 1 was a typo)
    }
}
