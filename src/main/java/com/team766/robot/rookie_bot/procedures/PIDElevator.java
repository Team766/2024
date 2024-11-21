package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Procedure;
import com.team766.controllers.PIDController;
import com.team766.framework.Context;
import com.team766.robot.rookie_bot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDElevator extends Procedure {
    double setpoint;
    PIDController controller;

    public PIDElevator(boolean top){
        if (top == true ){
        this.setpoint = 170;
        }
        else {
            this.setpoint = 0 ;
        }
    }

    public void run(Context context){
        context.takeOwnership(Robot.elevator);
        Robot.elevator.resetEncoder();

        controller = new PIDController(0.1, 0, 0.0002, -1, 1, 0.01);
        controller.setSetpoint(setpoint);
        SmartDashboard.putNumber("Setpoint", setpoint);

        while (!controller.isDone())
        {
            
            SmartDashboard.putNumber("ElevatorPosition", Robot.elevator.getElevatorDistance());
            controller.calculate(Robot.elevator.getElevatorDistance());
            double motor_effort = controller.getOutput();
            Robot.elevator.move(motor_effort);
            context.yield();
        }
        Robot.elevator.move(0);
    }
}

