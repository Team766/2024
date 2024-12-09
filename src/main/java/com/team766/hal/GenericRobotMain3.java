package com.team766.hal;

import com.team766.framework3.AutonomousMode;
import com.team766.framework3.RuleEngine;
import com.team766.framework3.SchedulerMonitor;
import com.team766.framework3.SchedulerUtils;
import com.team766.library.RateLimiter;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.web.AutonomousSelector;
import com.team766.web.ConfigUI;
import com.team766.web.Dashboard;
import com.team766.web.DriverInterface;
import com.team766.web.LogViewer;
import com.team766.web.ReadLogs;
import com.team766.web.WebServer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

// Team 766 - Robot Interface Base class

public final class GenericRobotMain3 implements GenericRobotMainBase {
    private RobotConfigurator3 configurator;
    private RuleEngine m_oi;
    private RuleEngine m_lights;

    private WebServer m_webServer;
    private AutonomousSelector<AutonomousMode> m_autonSelector;
    private AutonomousMode m_autonMode = null;
    private Command m_autonomous = null;

    // Reset the autonomous routine if the robot is disabled for more than this
    // number of seconds.
    private static final double RESET_IN_DISABLED_PERIOD = 10.0;
    private double m_disabledModeStartTime;

    private RateLimiter m_lightUpdateLimiter = new RateLimiter(0.05);

    private boolean faultInRobotInit = false;
    private boolean faultInAutoInit = false;
    private boolean faultInTeleopInit = false;

    public GenericRobotMain3(RobotConfigurator3 configurator) {
        SchedulerUtils.reset();
        SchedulerMonitor.start();

        this.configurator = configurator;
        m_autonSelector = new AutonomousSelector<>(configurator.getAutonomousModes());
        m_webServer = new WebServer();
        m_webServer.addHandler(new Dashboard());
        m_webServer.addHandler(new DriverInterface(m_autonSelector));
        m_webServer.addHandler(new ConfigUI());
        m_webServer.addHandler(new LogViewer());
        m_webServer.addHandler(new ReadLogs());
        m_webServer.addHandler(m_autonSelector);
        m_webServer.start();
    }

    public void robotInit() {
        try {
            configurator.initializeMechanisms();

            m_oi = configurator.createOI();
            m_lights = configurator.createLights();
        } catch (Throwable ex) {
            faultInRobotInit = true;
            throw ex;
        }
        faultInRobotInit = false;
    }

    public void disabledInit() {
        m_disabledModeStartTime = RobotProvider.instance.getClock().getTime();
    }

    public void disabledPeriodic() {
        if (faultInRobotInit) return;

        // The robot can enter disabled mode for two reasons:
        // - The field control system set the robots to disabled.
        // - The robot loses communication with the driver station.
        // In the former case, we want to reset the autonomous routine, as there
        // may have been a field fault, which would mean the match is going to
        // be replayed (and thus we would want to run the autonomous routine
        // from the beginning). In the latter case, we don't want to reset the
        // autonomous routine because the communication drop was likely caused
        // by some short-lived (less than a second long, or so) interference;
        // when the communications are restored, we want to continue executing
        // the routine that was interrupted, since it has knowledge of where the
        // robot is on the field, the state of the robot's mechanisms, etc.
        // Thus, we set a threshold on the amount of time spent in autonomous of
        // 10 seconds. It is almost certain that it will take longer than 10
        // seconds to reset the field if a match is to be replayed, but it is
        // also almost certain that a communication drop will be much shorter
        // than 10 seconds.
        double timeInState = RobotProvider.instance.getClock().getTime() - m_disabledModeStartTime;
        if (timeInState > RESET_IN_DISABLED_PERIOD) {
            resetAutonomousMode("time in disabled mode");
        }
        CommandScheduler.getInstance().run();
        if (m_lights != null && m_lightUpdateLimiter.next()) {
            m_lights.run();
        }
    }

    public void resetAutonomousMode(final String reason) {
        if (m_autonomous != null) {
            m_autonomous.cancel();
            m_autonomous = null;
            m_autonMode = null;
            Logger.get(Category.AUTONOMOUS)
                    .logRaw(Severity.INFO, "Resetting autonomus procedure from " + reason);
        }
    }

    public void autonomousInit() {
        faultInAutoInit = true;

        if (m_autonomous != null) {
            Logger.get(Category.AUTONOMOUS)
                    .logRaw(
                            Severity.INFO,
                            "Continuing previous autonomus procedure " + m_autonomous.getName());
        } else if (m_autonSelector.getSelectedAutonMode() == null) {
            Logger.get(Category.AUTONOMOUS).logRaw(Severity.WARNING, "No autonomous mode selected");
        }
        faultInAutoInit = false;
    }

    public void autonomousPeriodic() {
        if (faultInRobotInit || faultInAutoInit) return;

        final AutonomousMode autonomousMode = m_autonSelector.getSelectedAutonMode();
        if (autonomousMode != null && m_autonMode != autonomousMode) {
            m_autonomous = autonomousMode.instantiate();
            m_autonomous.schedule();
            m_autonMode = autonomousMode;
            Logger.get(Category.AUTONOMOUS)
                    .logRaw(
                            Severity.INFO,
                            "Starting new autonomus procedure " + m_autonomous.getName());
        }
        CommandScheduler.getInstance().run();
        if (m_lights != null && m_lightUpdateLimiter.next()) {
            m_lights.run();
        }
    }

    public void teleopInit() {
        faultInTeleopInit = true;

        if (m_autonomous != null) {
            m_autonomous.cancel();
            m_autonomous = null;
            m_autonMode = null;
        }

        faultInTeleopInit = false;
    }

    public void teleopPeriodic() {
        if (faultInRobotInit || faultInTeleopInit) return;

        CommandScheduler.getInstance().run();
        if (m_oi != null && RobotProvider.instance.hasNewDriverStationData()) {
            RobotProvider.instance.refreshDriverStationData();
            m_oi.run();
        }
        if (m_lights != null && m_lightUpdateLimiter.next()) {
            m_lights.run();
        }
    }
}
