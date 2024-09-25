package com.team766;

import com.team766.config.ConfigFileReader;
import com.team766.config.ConfigFileTestUtils;
import com.team766.framework3.SchedulerUtils;
import com.team766.framework3.TestLoggerExtension;
import com.team766.hal.RobotProvider;
import com.team766.hal.mock.TestRobotProvider;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestLoggerExtension.class)
public abstract class TestCase3 {

    @BeforeEach
    public void setUp() {
        assert HAL.initialize(500, 0);

        ConfigFileTestUtils.resetStatics();
        SchedulerUtils.reset();

        RobotProvider.instance = new TestRobotProvider();
    }

    protected void loadConfig(String configJson) throws IOException {
        var configFilePath = Files.createTempFile("testConfig", ".txt");
        Files.writeString(configFilePath, configJson);
        ConfigFileReader.instance = new ConfigFileReader(configFilePath.toString());
    }

    protected void step() {
        CommandScheduler.getInstance().run();
    }
}
