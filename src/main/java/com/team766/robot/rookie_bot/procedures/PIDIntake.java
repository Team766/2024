package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Procedure;
import com.team766.controllers.PIDController;
import com.team766.framework.Context;
import com.team766.robot.rookie_bot.Robot;

public class PIDIntake {
    double speedpointL;
    double speedpointR;
    public PIDIntake(boolean in){
        if (in == true ){
        this.speedpointL = 0.75;
        this.speedpointR = -0.75;
        }
        else {
            this.speedpointL = -0.75 ;
            this.speedpointR = 0.75 ;
        }
    }
}
