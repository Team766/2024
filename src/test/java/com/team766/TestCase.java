package com.team766;

import com.team766.config.ConfigFileReader;
import com.team766.config.ConfigFileTestUtils;
import com.team766.framework.Scheduler;
import com.team766.hal.RobotProvider;
import com.team766.hal.TestClock;
import com.team766.hal.mock.TestRobotProvider;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestCase {

    @BeforeEach
    public void setUp() {
        ConfigFileTestUtils.resetStatics();
        Scheduler.getInstance().reset();

        RobotProvider.instance = new TestRobotProvider(new TestClock());
    }

    protected void loadConfig(String configJson) throws IOException {
        var configFilePath = Files.createTempFile("testConfig", ".txt");
        Files.writeString(configFilePath, configJson);
        ConfigFileReader.instance = new ConfigFileReader(configFilePath.toString());
    }

    protected void step() {
        Scheduler.getInstance().run();
    }
}
