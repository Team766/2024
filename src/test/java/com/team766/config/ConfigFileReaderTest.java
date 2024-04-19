package com.team766.config;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class ConfigFileReaderTest extends TestCase {
    @Test
    public void getJsonStringFromEmptyConfigFile() throws IOException {
        loadConfig("{}");

        ConfigFileReader.getInstance().getString("test.sub.key");
        assertEquals(
                "{\"test\": {\"sub\": {\"key\": null}}}",
                ConfigFileReader.getInstance().getJsonString());
    }

    @Test
    public void getJsonStringFromPartialConfigFile() throws IOException {
        loadConfig("{\"test\": {\"sub\": {\"key\": \"pi\", \"value\": 3.14159}}}");

        assertEquals("pi", ConfigFileReader.getInstance().getString("test.sub.key").get());
        assertEquals(
                3.14159,
                ConfigFileReader.getInstance().getDouble("test.sub.value").get().doubleValue(),
                1e-6);
        assertFalse(ConfigFileReader.getInstance().getInts("test.other.value").hasValue());
        assertEquals(
                "{\"test\": {\n  \"sub\": {\n    \"value\": 3.14159,\n    \"key\": \"pi\"\n  },\n  \"other\": {\"value\": null}\n}}",
                ConfigFileReader.getInstance().getJsonString());
    }
}
