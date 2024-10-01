package com.team766.robot.reva.procedures;

import com.team766.framework.Status;

public record ShootingProcedureStatus(ShootingProcedureStatus.Status status) implements Status {
    public enum Status {
        RUNNING,
        OUT_OF_RANGE,
        FINISHED
    }
}
