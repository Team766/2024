package com.team766.robot.rookie_bot.procedures;
import com.team766.framework.Procedure;
import com.team766.controllers.PIDController;
import com.team766.framework.Context;
import com.team766.robot.rookie_bot.Robot;

    public class PIDElevator extends Procedure {
        double setpoint;
        PIDController controller;

        public PIDElevator(boolean top) {
            if (top) {
                setpoint = 400;
                
            } else {
                setpoint = 0;
            }

        }  

        public void run(Context context) {
            context.takeOwnership(Robot.elevator);
            Robot.elevator.resetEncoder(); //clear encoder
            
            controller = new PIDController(0, 0, 0, 0, 1, 0.01); // values a P I D, min. max. and threshold
            controller.setSetpoint(setpoint); //tell our PID contoller our setpoint
            
            while (!controller.isDone()) { //stop loop when we get to setpoint
                controller.calculate(Robot.elevator.getElevatorDistance()); //pass the feedback into the PID
                double motor_effort = controller.getOutput(); //get the PID controller output for this cycle
                Robot.elevator.move(motor_effort); //MOVE THE ELEVATOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1 (that 1 was a typo)
            }
           
            Robot.elevator.move(0); //Make sure that the dang elevator stopped moving!!!!!!
            context.yield();
        }
} 
