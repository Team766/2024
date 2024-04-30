package com.team766.robot.gatorade.procedures;

import com.team766.framework.InstantProcedure;
import com.team766.robot.common.mechanisms.Drive;

public class SetCross extends InstantProcedure {
    private final Drive drive;

    public SetCross(Drive drive) {
        super(reservations(drive));
        this.drive = drive;
    }

    public void run() {
        drive.stopDrive();
        drive.setCross();
    }
}
