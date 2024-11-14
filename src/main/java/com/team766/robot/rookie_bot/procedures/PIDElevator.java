package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Procedure;
import com.team766.controllers.PIDController;
import com.team766.framework.Context;
import com.team766.robot.rookie_bot.Robot;

public class PIDElevator extends Procedure {
    double setpoint;
    PIDController controller;

    public PIDElevator(boolean top){
        if (top == true ){
        this.setpoint = 400;
        }
        else {
            this.setpoint = 1 ;
        }
    }

    public void run(Context context){
        context.takeOwnership(Robot.elevator);
        Robot.elevator.resetEncoder();

        controller = new PIDController(0.001, 0, 0.0001, 0, 1, 0.01);
        controller.setSetpoint(setpoint);
        while (!controller.isDone())
        {
            controller.calculate(Robot.elevator.getElevatorDistance());
            double motor_effort = controller.getOutput();
            Robot.elevator.move(motor_effort);
        }
        Robot.elevator.move(0);
        context.yield();
    }
}

