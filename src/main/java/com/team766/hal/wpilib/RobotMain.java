package com.team766.hal.wpilib;

import com.team766.config.ConfigFileReader;
import com.team766.hal.CanivPoller;
import com.team766.hal.GenericRobotMain;
import com.team766.hal.RobotProvider;
import com.team766.logging.LoggerExceptionUtils;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.RobotBase;
import java.io.File;
// import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

public class RobotMain extends LoggedRobot {
    private static final String USB_CONFIG_FILE = "/U/config/robotConfig.txt";
    private static final String INTERNAL_CONFIG_FILE = "/home/lvuser/robotConfig.txt";

    private GenericRobotMain robot;

    public static void main(final String... args) {
        Supplier<RobotMain> supplier =
                new Supplier<RobotMain>() {
                    RobotMain instance;

                    @Override
                    public RobotMain get() {
                        if (instance == null) {
                            instance = new RobotMain();
                        }
                        return instance;
                    }
                };

        // periodically poll "caniv" in the background, if present
        CanivPoller canivPoller = null;
        if (new File(CanivPoller.CANIV_BIN).exists()) {
            canivPoller = new CanivPoller(10 * 1000 /* millis */);
            new Thread(canivPoller, "caniv poller").start();
        }

        try {
            RobotBase.startRobot(supplier);
        } catch (Throwable ex) {
            ex.printStackTrace();
            LoggerExceptionUtils.logException(ex);
        }

        if (canivPoller != null) {
            canivPoller.setDone(true);
        }
    }

    public RobotMain() {
        super(0.005);
    }

    private static String checkForAndReturnPathToConfigFile(final String file) {
        Path configPath = Filesystem.getDeployDirectory().toPath().resolve(file);
        File configFile = configPath.toFile();
        if (configFile.exists()) {
            return configFile.getPath();
        }
        return null;
    }

    @Override
    public void robotInit() {
        try {
            boolean configFromUSB = true;
            String filename = null;
            filename = checkForAndReturnPathToConfigFile(USB_CONFIG_FILE);

            if (filename == null) {
                filename = INTERNAL_CONFIG_FILE;
                configFromUSB = false;
            }

            ConfigFileReader.instance =
                    new ConfigFileReader(filename, configFromUSB ? INTERNAL_CONFIG_FILE : null);
            RobotProvider.instance = new WPIRobotProvider();
            robot = new GenericRobotMain();

            DriverStation.startDataLog(DataLogManager.getLog());

            if (isReal()) {
                // enable dual-logging
                com.team766.logging.Logger.enableLoggingToDataLog(true);

                // set up AdvantageKit logging
                DataLogManager.log("Initializing logging.");
                Logger.addDataReceiver(new WPILOGWriter("/U/logs")); // Log to sdcard
                Logger.addDataReceiver(new NT4Publisher()); // Publish data to NetworkTables
                new PowerDistribution(1, ModuleType.kRev); // Enables power distribution logging

            } else {
                // TODO: add support for simulation logging/replay
            }

            Logger.start();

            robot.robotInit();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    public void disabledInit() {
        try {
            robot.disabledInit();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    public void autonomousInit() {
        try {
            robot.autonomousInit();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    public void teleopInit() {
        try {
            robot.teleopInit();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    public void disabledPeriodic() {
        try {
            robot.disabledPeriodic();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    public void autonomousPeriodic() {
        try {
            robot.autonomousPeriodic();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    public void teleopPeriodic() {
        try {
            robot.teleopPeriodic();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }
    }
}
