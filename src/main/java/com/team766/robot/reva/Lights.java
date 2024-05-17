package com.team766.robot.reva;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.CANdle;
import com.team766.framework.LightsBase;
import com.team766.framework.Statuses;
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
    public boolean signalCameraConnected() {
        ErrorCode e = m_candle.setLEDs(92, 250, 40);
        return handleErrorCode(e);
    }

    public boolean signalFinishedShootingProcedure() {
        ErrorCode e = m_candle.setLEDs(0, 150, 0);
        return handleErrorCode(e);
    }

    // Purple
    public boolean signalCameraNotConnected() {
        ErrorCode e = m_candle.setLEDs(100, 0, 100);
        return handleErrorCode(e);
    }

    public boolean signalShooterOutOfRange() {
        ErrorCode e = m_candle.setLEDs(150, 0, 0);
        return handleErrorCode(e);
    }

    // Coral orange
    public boolean signalNoteInIntake() {
        ErrorCode e = m_candle.setLEDs(255, 127, 80);
        return handleErrorCode(e);
    }

    // Off
    public boolean turnLightsOff() {
        ErrorCode e = m_candle.setLEDs(0, 0, 0);
        return handleErrorCode(e);
    }

    // Blue
    public boolean signalNoNoteInIntakeYet() {
        ErrorCode e = m_candle.setLEDs(0, 0, 100);
        return handleErrorCode(e);
    }

    public boolean isDoingShootingProcedure() {
        ErrorCode e = m_candle.setLEDs(0, 227, 197);
        return handleErrorCode(e);
    }

    public boolean signalFinishingShootingProcedure() {
        ErrorCode e = m_candle.setLEDs(0, 50, 100);
        return handleErrorCode(e);
    }

    public boolean signalStartingShootingProcedure() {
        ErrorCode e = m_candle.setLEDs(50, 50, 2);
        return handleErrorCode(e);
    }

    private boolean handleErrorCode(ErrorCode e) {
        if (e.equals(ErrorCode.OK)) {
            return true;
        }

        return false;
    }
}
