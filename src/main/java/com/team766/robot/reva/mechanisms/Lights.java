package com.team766.robot.reva.mechanisms;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.CANdle;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class Lights extends Mechanism {

    CANdle m_candle = new CANdle(58);

    public Lights() {
        // Show that robot lights mechanism is ready
        // NOTE: this will always display purple unless we reboot in the middle of a match.
        // the alliance won't be available when Lights is typically created.
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
        ErrorCode e = m_candle.setLEDs(255, 95, 21);
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

    public boolean signalHasTag() {
        ErrorCode e = m_candle.setLEDs(255, 215, 0);
        return handleErrorCode(e);
    }

    private boolean handleErrorCode(ErrorCode e) {
        if (e.equals(ErrorCode.OK)) {
            return true;
        }

        return false;
    }
}
