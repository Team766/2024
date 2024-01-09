package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class MoveWristvator extends Procedure {
    private final Shoulder.Position shoulderSetpoint;
    private final Elevator.Position elevatorSetpoint;
    private final Wrist.Position wristSetpoint;

    public MoveWristvator(
            Shoulder.Position shoulderSetpoint_,
            Elevator.Position elevatorSetpoint_,
            Wrist.Position wristSetpoint_) {
        this.shoulderSetpoint = shoulderSetpoint_;
        this.elevatorSetpoint = elevatorSetpoint_;
        this.wristSetpoint = wristSetpoint_;
    }

    @Override
    public final void run(Context context) {
        context.takeOwnership(Robot.wrist);
        context.takeOwnership(Robot.elevator);
        context.takeOwnership(Robot.shoulder);

        // Always retract the wrist before moving the elevator.
        // It might already be retracted, so it's possible that this step finishes instantaneously.
        Robot.wrist.rotate(Wrist.Position.RETRACTED);
        // If raising the shoulder, do that before the elevator (else, lower it after the elevator).
        if (shoulderSetpoint.getAngle() > Robot.shoulder.getAngle()) {
            Robot.shoulder.rotate(shoulderSetpoint);
            context.waitFor(() -> Robot.shoulder.isNearTo(shoulderSetpoint));
        }
        context.waitFor(() -> Robot.wrist.isNearTo(Wrist.Position.RETRACTED));

        // Move the elevator. Wait until it gets near the target position.
        Robot.elevator.moveTo(elevatorSetpoint);
        context.waitFor(() -> Robot.elevator.isNearTo(elevatorSetpoint));

        // If lowering the shoulder, do that after the elevator.
        if (shoulderSetpoint.getAngle() < Robot.shoulder.getAngle()) {
            Robot.shoulder.rotate(shoulderSetpoint);
        }
        // Lastly, move the wrist.
        Robot.wrist.rotate(wristSetpoint);
        context.waitFor(() -> Robot.wrist.isNearTo(wristSetpoint));
        context.waitFor(() -> Robot.shoulder.isNearTo(shoulderSetpoint));
    }
}
