package com.team766.hal.wpilib;

import com.team766.config.ConfigFileReader;
import com.team766.hal.CanivPoller;
import com.team766.hal.GenericRobotMain;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.RobotBase;
import java.io.File;
// import java.nio.file.Files;
import java.util.function.Supplier;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

public class RobotMain extends LoggedRobot {
    private static final String USB_DRIVE_FILE = "/U";
    private static final String USB_CONFIG_DIR = "/U/config";
    private static final String USB_LOGS_DIR = "/U/logs";
    private static final String INTERNAL_LOGS_DIR = "/home/lvuser";

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

        // UPDATE 1/21/2024: temporarily disable this poller
        // Prior to 2024, Phoenix Tuner only installed the "caniv" binary on the RoboRio when
        // a CANivore was configured.  Now, "caniv" is part of the 2024 RoboRio system image
        // even if a CANivore is never configured or used.
        // Thus, we will need to find a different way to condition when we poll.
        // eg, instead of conditioning on whether or not the caniv binary is present,
        // via the presence of a value in the config file, via an invocation of caniv to
        // see if any CAN buses are present, etc.  Until we update this logic, we'll
        // temporarily disable this altogether with a short-circuit AND.
        if (false && new File(CanivPoller.CANIV_BIN).exists()) {
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

    /**
     * Checks if the provided directory exists.  If not attempts to create it.
     * @param dir The directory.
     * @return True if the directory already existed or was successfully created.  False otherwise.
     */
    private static boolean checkOrCreateDirectory(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                com.team766.logging.Logger.get(Category.CONFIGURATION)
                        .logData(Severity.ERROR, "Unable to create directory %s", dir);
                return false;
            }
        }
        return true;
    }

    private boolean checkForAndBootstrapUsbDrive() {
        boolean useUsbDrive = true;

        File usbDrive = new File(USB_DRIVE_FILE);
        if (usbDrive.exists()) {
            if (!checkOrCreateDirectory(USB_CONFIG_DIR)) {
                useUsbDrive = false;
            }
            if (!checkOrCreateDirectory(USB_LOGS_DIR)) {
                // we could still use the USB drive for config files, but something is wonky
                useUsbDrive = false;
            }
        } else {
            useUsbDrive = false;
        }
        return useUsbDrive;
    }

    @Override
    public void robotInit() {
        try {
            boolean useUsbDrive = checkForAndBootstrapUsbDrive();
            String configFilename;
            String backupConfigFilename;
            String logsDir;

            if (useUsbDrive) {
                configFilename = USB_CONFIG_FILE;
                backupConfigFilename = INTERNAL_CONFIG_FILE;
                logsDir = USB_LOGS_DIR;
            } else {
                configFilename = INTERNAL_CONFIG_FILE;
                backupConfigFilename = null;
                logsDir = INTERNAL_LOGS_DIR;
            }

            ConfigFileReader.instance = new ConfigFileReader(configFilename, backupConfigFilename);
            RobotProvider.instance = new WPIRobotProvider();
            robot = new GenericRobotMain();

            DriverStation.startDataLog(DataLogManager.getLog());

            if (isReal()) {
                // enable dual-logging
                com.team766.logging.Logger.enableLoggingToDataLog(true);

                // set up AdvantageKit logging
                DataLogManager.log("Initializing logging.");
                Logger.addDataReceiver(new WPILOGWriter(logsDir)); // Log to sdcard
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
