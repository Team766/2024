package com.team766.robot.reva.procedures;

public record ShootingProcedureStatus(ShootingProcedureStatus.Status status) {
    public enum Status {
        RUNNING,
        OUT_OF_RANGE,
        FINISHED
    }
}
