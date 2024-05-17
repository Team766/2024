package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class MoveWristvator extends Procedure {
    private final Shoulder.RotateToPosition shoulderSetpoint;
    private final Elevator.MoveToPosition elevatorSetpoint;
    private final Wrist.RotateToPosition wristSetpoint;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;

    public MoveWristvator(
            Shoulder.RotateToPosition shoulderSetpoint_,
            Elevator.MoveToPosition elevatorSetpoint_,
            Wrist.RotateToPosition wristSetpoint_,
            Shoulder shoulder,
            Elevator elevator,
            Wrist wrist) {
        super(reservations(shoulder, elevator, wrist));
        this.shoulderSetpoint = shoulderSetpoint_;
        this.elevatorSetpoint = elevatorSetpoint_;
        this.wristSetpoint = wristSetpoint_;
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
    }

    @Override
    public final void run(Context context) {
        // Always retract the wrist before moving the elevator.
        // It might already be retracted, so it's possible that this step finishes instantaneously.
        wrist.setGoal(Wrist.RotateToPosition.RETRACTED);
        // If raising the shoulder, do that before the elevator (else, lower it after the elevator).
        if (shoulderSetpoint.angle() > shoulder.getStatus().angle()) {
            shoulder.setGoal(shoulderSetpoint);
            context.waitFor(() -> shoulder.getStatus().isNearTo(shoulderSetpoint));
        }
        context.waitFor(() -> wrist.getStatus().isNearTo(Wrist.RotateToPosition.RETRACTED));

        // Move the elevator. Wait until it gets near the target position.
        elevator.setGoal(elevatorSetpoint);
        context.waitFor(() -> elevator.getStatus().isNearTo(elevatorSetpoint));

        // If lowering the shoulder, do that after the elevator.
        if (shoulderSetpoint.angle() < shoulder.getStatus().angle()) {
            shoulder.setGoal(shoulderSetpoint);
        }
        // Lastly, move the wrist.
        wrist.setGoal(wristSetpoint);
        context.waitFor(() -> wrist.getStatus().isNearTo(wristSetpoint));
        context.waitFor(() -> shoulder.getStatus().isNearTo(shoulderSetpoint));
    }
}
