package com.team766.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigFileReaderTest {
    @BeforeEach
    public void setup() {
        AbstractConfigValue.resetStatics();
    }

    @Test
    public void isPrefix() {
        assertTrue(ConfigFileReader.isPrefix(new String[] {}, new String[] {}));
        assertTrue(ConfigFileReader.isPrefix(new String[] {}, new String[] {"a"}));
        assertFalse(ConfigFileReader.isPrefix(new String[] {"a"}, new String[] {}));
        assertTrue(ConfigFileReader.isPrefix(new String[] {"a"}, new String[] {"a"}));
        assertTrue(ConfigFileReader.isPrefix(new String[] {"a"}, new String[] {"a", "b"}));
        assertTrue(ConfigFileReader.isPrefix(new String[] {"a", "b"}, new String[] {"a", "b"}));
        assertFalse(ConfigFileReader.isPrefix(new String[] {"a", "b"}, new String[] {"a"}));
    }

    @Test
    public void getJsonStringFromEmptyConfigFile() throws IOException {
        File testConfigFile = File.createTempFile("config_file_test", ".json");
        try (FileWriter fos = new FileWriter(testConfigFile)) {
            fos.append("{}");
        }

        ConfigFileReader.instance = new ConfigFileReader(testConfigFile.getPath());
        ConfigFileReader.getInstance().getString("test.sub.key");
        assertEquals(
                "{\"test\": {\"sub\": {\"key\": null}}}",
                ConfigFileReader.getInstance().getJsonString());
    }

    @Test
    public void getJsonStringFromPartialConfigFile() throws IOException {
        File testConfigFile = File.createTempFile("config_file_test", ".json");
        try (FileWriter fos = new FileWriter(testConfigFile)) {
            fos.append("{\"test\": {\"sub\": {\"key\": \"pi\", \"value\": 3.14159}}}");
        }

        ConfigFileReader.instance = new ConfigFileReader(testConfigFile.getPath());
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

    @Test
    public void observeChangeInConfigFile() throws IOException {
        File testConfigFile = File.createTempFile("config_file_test", ".json");
        try (FileWriter fos = new FileWriter(testConfigFile)) {
            fos.append("{\"test\": {\"sub\": {\"key\": \"pi\", \"value\": 3.14159}}}");
        }

        ConfigFileReader.instance = new ConfigFileReader(testConfigFile.getPath());
        var keyProvider = ConfigFileReader.getInstance().getString("test.sub.key");
        var valueProvider = ConfigFileReader.getInstance().getDouble("test.sub.value");

        final AtomicReference<Optional<String>> lastKeyUpdate = new AtomicReference<>();
        keyProvider.addObserver(lastKeyUpdate::set);
        final AtomicReference<Optional<Double>> lastValueUpdate = new AtomicReference<>();
        valueProvider.addObserver(lastValueUpdate::set);

        assertNull(lastKeyUpdate.get());
        assertNull(lastValueUpdate.get());

        ConfigFileReader.getInstance()
                .reloadFromJson("{\"test\": {\"sub\": {\"key\": \"tau\", \"value\": 6.28319}}}");
        assertEquals("tau", keyProvider.get());
        assertEquals("tau", lastKeyUpdate.get().get());
        assertEquals(6.28319, valueProvider.get().doubleValue(), 1e-6);
        assertEquals(6.28319, lastValueUpdate.get().get().doubleValue(), 1e-6);
    }

    @Test
    public void observeConfigValueMutation() throws IOException {
        File testConfigFile = File.createTempFile("config_file_test", ".json");
        try (FileWriter fos = new FileWriter(testConfigFile)) {
            fos.append("{\"test\": {\"sub\": {\"key\": \"pi\", \"value\": 3.14159}}}");
        }

        ConfigFileReader.instance = new ConfigFileReader(testConfigFile.getPath());

        var providerReader = ConfigFileReader.getInstance().getString("test.sub.key");
        final AtomicReference<Optional<String>> readerLastUpdate = new AtomicReference<>();
        providerReader.addObserver(readerLastUpdate::set);

        var providerWriter = ConfigFileReader.getInstance().getString("test.sub.key");
        final AtomicReference<Optional<String>> writerLastUpdate = new AtomicReference<>();
        providerWriter.addObserver(writerLastUpdate::set);

        providerWriter.set("gamma");
        assertEquals(Optional.of("gamma"), readerLastUpdate.get());
        assertEquals(Optional.of("gamma"), writerLastUpdate.get());

        providerWriter.clear();
        assertEquals(Optional.empty(), readerLastUpdate.get());
        assertEquals(Optional.empty(), writerLastUpdate.get());
    }
}
