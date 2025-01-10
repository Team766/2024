package com.team766.framework3;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.Supplier;

public class AutonomousModeStateMachine {
    private enum AutonomousState {
        /**
         * m_autonomous has not been started yet.
         * It can be scheduled the next time autonomousInit is called.
         */
        NEW,
        /**
         * m_autonomous is currently running.
         */
        SCHEDULED,
        /**
         * m_autonomous has finished, has been canceled, or is null.
         * A new instance of the autonomous Command needs to be created before
         * autonomous mode can be enabled.
         */
        INVALID,
    }

    private final Supplier<AutonomousMode> selector;
    private AutonomousMode m_autonMode = null;
    private Command m_autonomous = null;
    private AutonomousState m_autonState = AutonomousState.INVALID;

    public AutonomousModeStateMachine(Supplier<AutonomousMode> selector) {
        this.selector = selector;
    }

    public void stopAutonomousMode(final String reason) {
        if (m_autonState == AutonomousState.SCHEDULED) {
            m_autonomous.cancel();
            m_autonState = AutonomousState.INVALID;
            Logger.get(Category.AUTONOMOUS)
                    .logRaw(Severity.INFO, "Resetting autonomus procedure from " + reason);
        }
    }

    private void refreshAutonomousMode() {
        final AutonomousMode autonomousMode = selector.get();
        if (m_autonMode != autonomousMode) {
            stopAutonomousMode("selection of new autonomous mode " + autonomousMode);
            m_autonState = AutonomousState.INVALID;
        }
        if (m_autonState == AutonomousState.INVALID && autonomousMode != null) {
            m_autonomous = autonomousMode.instantiate();
            m_autonMode = autonomousMode;
            m_autonState = AutonomousState.NEW;
            Logger.get(Category.AUTONOMOUS)
                    .logRaw(
                            Severity.INFO,
                            "Initialized new autonomus procedure " + m_autonomous.getName());
        }
    }

    public void startAutonomousMode() {
        refreshAutonomousMode();
        switch (m_autonState) {
            case INVALID -> {
                Logger.get(Category.AUTONOMOUS)
                        .logRaw(Severity.WARNING, "No autonomous mode selected");
            }
            case SCHEDULED -> {
                Logger.get(Category.AUTONOMOUS)
                        .logRaw(
                                Severity.INFO,
                                "Continuing previous autonomus procedure "
                                        + m_autonomous.getName());
            }
            case NEW -> {
                m_autonomous.schedule();
                m_autonState = AutonomousState.SCHEDULED;
                Logger.get(Category.AUTONOMOUS)
                        .logRaw(
                                Severity.INFO,
                                "Starting new autonomus procedure " + m_autonomous.getName());
            }
        }
    }

    public void reinitializeAutonomousMode(final String reason) {
        stopAutonomousMode(reason);
        refreshAutonomousMode();
    }
}
