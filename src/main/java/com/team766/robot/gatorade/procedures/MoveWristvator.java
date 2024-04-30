package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class MoveWristvator extends Procedure {
    private final Shoulder.Position shoulderSetpoint;
    private final Elevator.Position elevatorSetpoint;
    private final Wrist.Position wristSetpoint;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;

    public MoveWristvator(
            Shoulder.Position shoulderSetpoint_,
            Elevator.Position elevatorSetpoint_,
            Wrist.Position wristSetpoint_,
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
        wrist.rotate(Wrist.Position.RETRACTED);
        // If raising the shoulder, do that before the elevator (else, lower it after the elevator).
        if (shoulderSetpoint.getAngle() > shoulder.getAngle()) {
            shoulder.rotate(shoulderSetpoint);
            context.waitFor(() -> shoulder.isNearTo(shoulderSetpoint));
        }
        context.waitFor(() -> wrist.isNearTo(Wrist.Position.RETRACTED));

        // Move the elevator. Wait until it gets near the target position.
        elevator.moveTo(elevatorSetpoint);
        context.waitFor(() -> elevator.isNearTo(elevatorSetpoint));

        // If lowering the shoulder, do that after the elevator.
        if (shoulderSetpoint.getAngle() < shoulder.getAngle()) {
            shoulder.rotate(shoulderSetpoint);
        }
        // Lastly, move the wrist.
        wrist.rotate(wristSetpoint);
        context.waitFor(() -> wrist.isNearTo(wristSetpoint));
        context.waitFor(() -> shoulder.isNearTo(shoulderSetpoint));
    }
}
