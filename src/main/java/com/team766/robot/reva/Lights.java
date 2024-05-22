package com.team766.robot.reva;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.CANdle;
import com.team766.framework.Context;
import com.team766.framework.LightsBase;
import com.team766.framework.Statuses;
import com.team766.logging.Severity;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.procedures.IntakeUntilIn;
import com.team766.robot.reva.procedures.ShootingProcedureStatus;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class Lights extends LightsBase {

    CANdle m_candle = new CANdle(58);

    protected void dispatch(Statuses statuses) {
        final boolean isCameraPresent = statuses.has(
                ForwardApriltagCamera.Status.class,
                s -> s.status.isCameraConnected() && s.status.tagId().isPresent());
        if (DriverStation.isDisabled()) {
            if (!isCameraPresent) {
                signalCameraNotConnected();
            } else {
                Optional<Alliance> alliance = DriverStation.getAlliance();
                if (alliance.isPresent()) {
                    if (alliance.get().equals(Alliance.Blue)) {
                        // Blue
                        m_candle.setLEDs(0, 0, 100);
                    } else {
                        // Red
                        m_candle.setLEDs(100, 0, 0);
                    }
                } else {
                    // Purple
                    m_candle.setLEDs(100, 0, 100);
                }
            }
        } else {
            for (var s : statuses) {
                if (!s.isFreshOrAgeLessThan(2.0)) {
                    continue;
                }
                switch (s.status) {
                    case IntakeUntilIn.Status intakeStatus -> {
                        if (intakeStatus.noteInIntake()) {
                            signalNoteInIntake();
                        } else {
                            signalNoNoteInIntakeYet();
                        }
                        return;
                    }
                    case ShootingProcedureStatus shootStatus -> {
                        if (!isCameraPresent) {
                            signalCameraNotConnected();
                        } else {
                            switch (shootStatus.status()) {
                                case RUNNING -> signalStartingShootingProcedure();
                                case OUT_OF_RANGE -> signalShooterOutOfRange();
                                case FINISHED -> signalFinishingShootingProcedure();
                            }
                        }
                        return;
                    }
                    default -> {}
                }
            }
            turnLightsOff();
        }
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

    public void signalShooterOutOfRange() {
        runAnimation((Context context) -> {
            while (true) {
                handleErrorCode(m_candle.setLEDs(150, 0, 0));
                context.waitForSeconds(0.5);
                turnLightsOff();
                context.waitForSeconds(0.5);
            }
        });
    }

    // Coral orange
    public void signalNoteInIntake() {
        handleErrorCode(m_candle.setLEDs(255, 127, 80));
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
}
