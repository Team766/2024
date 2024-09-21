package com.team766.robot.reva;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.framework3.RulePersistence.*;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.CANdle;
import com.team766.framework3.Context;
import com.team766.framework3.Rule;
import com.team766.framework3.RuleEngine;
import com.team766.logging.Severity;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class Lights extends RuleEngine {

    CANdle m_candle = new CANdle(58);

    public Lights() {
        final BooleanSupplier isCameraMissing =
                () ->
                        !checkForStatusWith(
                                ForwardApriltagCamera.ApriltagCameraStatus.class,
                                s -> s.isCameraConnected());

        addRule(
                Rule.create("Robot Disabled", () -> DriverStation.isDisabled())
                        .whenTriggering(
                                Rule.create("Camera Missing", isCameraMissing)
                                        .withOnTriggeringProcedure(
                                                ONCE_AND_HOLD,
                                                Set.of(),
                                                () -> signalCameraNotConnected())

                                // Rule.createDefaultProcedure("Alliance Color", Set.of(), () -> {
                                //     switch (DriverStation.getAlliance().orElse(null)) {
                                //         case Blue -> blue();
                                //         case Red -> red();
                                //         case null -> purple();
                                //     }
                                // })
                                )
                        .whenNotTriggering(
                                // for (var s : statuses) {
                                //     if (!s.isFreshOrAgeLessThan(2.0)) {
                                //         continue;
                                //     }
                                //     switch (s.status) {
                                //         case IntakeUntilIn.IntakeUntilInStatus intakeStatus -> {
                                //             if (intakeStatus.noteInIntake()) {
                                //                 signalNoteInIntake();
                                //             } else {
                                //                 signalNoNoteInIntakeYet();
                                //             }
                                //             return;
                                //         }
                                //         case ShootingProcedureStatus shootStatus -> {
                                //             if (isCameraMissing) {
                                //                 signalCameraNotConnected();
                                //             } else {
                                //                 switch (shootStatus.status()) {
                                //                     case RUNNING ->
                                // signalStartingShootingProcedure();
                                //                     case OUT_OF_RANGE ->
                                // signalShooterOutOfRange();
                                //                     case FINISHED ->
                                // signalFinishingShootingProcedure();
                                //                 }
                                //             }
                                //             return;
                                //         }
                                //         default -> {}
                                //     }
                                // }
                                // turnLightsOff();
                                ));
    }

    // Lime green
    public void signalCameraConnected() {
        handleErrorCode(m_candle.setLEDs(92, 250, 40));
    }

    public void signalFinishedShootingProcedure() {
        handleErrorCode(m_candle.setLEDs(0, 150, 0));
    }

    // Purple
    public void signalCameraNotConnected() {
        handleErrorCode(m_candle.setLEDs(100, 0, 100));
    }

    public void signalShooterOutOfRange(Context context) {
        while (true) {
            handleErrorCode(m_candle.setLEDs(150, 0, 0));
            context.waitForSeconds(0.5);
            turnLightsOff();
            context.waitForSeconds(0.5);
        }
    }

    // Coral orange
    public void signalNoteInIntake() {
        handleErrorCode(m_candle.setLEDs(255, 95, 21));
    }

    // Off
    public void turnLightsOff() {
        handleErrorCode(m_candle.setLEDs(0, 0, 0));
    }

    // Blue
    public void signalNoNoteInIntakeYet() {
        handleErrorCode(m_candle.setLEDs(0, 0, 100));
    }

    public void isDoingShootingProcedure() {
        handleErrorCode(m_candle.setLEDs(0, 227, 197));
    }

    public void signalFinishingShootingProcedure() {
        handleErrorCode(m_candle.setLEDs(0, 50, 100));
    }

    public void signalStartingShootingProcedure() {
        handleErrorCode(m_candle.setLEDs(50, 50, 2));
    }

    private void handleErrorCode(ErrorCode e) {
        if (!e.equals(ErrorCode.OK)) {
            log(Severity.ERROR, "CANdle error: " + e.toString());
        }
    }

    public void red() {
        handleErrorCode(m_candle.setLEDs(100, 0, 0));
    }

    public void blue() {
        handleErrorCode(m_candle.setLEDs(0, 0, 100));
    }

    public void purple() {
        handleErrorCode(m_candle.setLEDs(100, 0, 100));
    }
}
