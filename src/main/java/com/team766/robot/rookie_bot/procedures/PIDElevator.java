package com.team766.robot.rookie_bot.procedures;
import com.team766.framework.Procedure;
import com.team766.controllers.PIDController;
import com.team766.framework.Context;
import com.team766.robot.rookie_bot.Robot;

    public class PIDElevator extends Procedure{
        double setpoint;
        PIDController controller;

        public PIDElevator(){
            this.setpoint = 1;
     }  

     Public void run (Context context){
        context.

