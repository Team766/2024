package com.team766;

import com.team766.config.ConfigFileReader;
import com.team766.config.ConfigFileTestUtils;
import com.team766.framework3.SchedulerUtils;
import com.team766.framework3.TestLoggerExtension;
import com.team766.hal.RobotProvider;
import com.team766.hal.mock.TestRobotProvider;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.littletonrobotics.conduit.ConduitApi;
import org.littletonrobotics.junction.inputs.LoggedDriverStation;
import org.littletonrobotics.junction.inputs.LoggedSystemStats;

@ExtendWith(TestLoggerExtension.class)
public abstract class TestCase3 {

    @BeforeEach
    public final void setUpFramework() {
        assert HAL.initialize(500, 0);

        setRobotEnabled(true);

        ConfigFileTestUtils.resetStatics();
        SchedulerUtils.reset();

        RobotProvider.instance = new TestRobotProvider();
    }

    protected void setRobotEnabled(boolean enabled) {
        DriverStationSim.setDsAttached(true);
        DriverStationSim.setEnabled(enabled);
        updateDriverStationData();
    }

    protected void setRobotAutonomous(boolean autonomous) {
        DriverStationSim.setAutonomous(autonomous);
        updateDriverStationData();
    }

    protected void updateDriverStationData() {
        // Flush data that was set using DriverStationSim
        DriverStationSim.notifyNewData();
        // AdvantageKit inserts itself into DriverStation, so we also need to flush AdvantageKit
        // data in order for DriverStation to get the updated data.
        // https://github.com/Mechanical-Advantage/AdvantageKit/blob/e236e4bf57188addc3befd2d827660b470d9af89/docs/COMMON-ISSUES.md#unit-testing
        ConduitApi.getInstance().captureData();
        LoggedDriverStation.periodic();
        LoggedSystemStats.periodic();
    }

    protected void loadConfig(String configJson) throws IOException {
        var configFilePath = Files.createTempFile("testConfig", ".txt");
        Files.writeString(configFilePath, configJson);
        ConfigFileReader.instance = new ConfigFileReader(configFilePath.toString());
    }

    protected void step() {
        updateDriverStationData();
        CommandScheduler.getInstance().run();
    }
}
