package com.team766.robot.swerveandshoot;

import com.team766.controllers.PIDController;
import com.team766.framework.Procedure;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class VisionPIDProcedure extends Procedure {
    public VisionPIDProcedure(Collection<Subsystem> reservations) {
        super(reservations);
    }

    protected PIDController xPID = new PIDController(0.4, 0, 0, 0, -0.75, 0.75, 0.02);
    protected PIDController yPID = new PIDController(0.18, 0, 0, 0, -0.75, 0.75, 0.02);

    protected PIDController yawPID = new PIDController(0.02, 0.001, 0, 0, -0.25, 0.25, 3);

    // static PIDController makeYawPIDController(){
    // 	return new PIDController(0.02, 0.001, 0, 0, -0.25, 0.25, 3);
    // }

}
