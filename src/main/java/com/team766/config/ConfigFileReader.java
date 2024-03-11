package com.team766.config;

import com.team766.library.SettableValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Class for loading in config from the Config file.
 * Constants that need to be tuned / changed
 *
 * Data is read from a file in JSON format
 *
 * @author Brett Levenson
 */
public class ConfigFileReader {
    // this.getClass().getClassLoader().getResource(fileName).getPath()

    public static ConfigFileReader instance;

    private static final String KEY_DELIMITER = ".";

    private String m_fileName;
    private String m_backupFileName; // if set, will also save here
    private JSONObject m_values = new JSONObject();

    public static ConfigFileReader getInstance() {
        return instance;
    }

    public ConfigFileReader(final String fileName, final String backupFileName) {
        m_fileName = fileName;
        m_backupFileName = backupFileName;

        try {
            reloadFromFile();
        } catch (Exception e) {
            System.err.println("Failed to load config file!");
            e.printStackTrace();
            LoggerExceptionUtils.logException(new IOException("Failed to load config file!", e));
        }
    }

    public ConfigFileReader(final String fileName) {
        this(fileName, null /* backup file name */);
    }

    public void reloadFromFile() throws IOException {
        System.out.println("Loading config file: " + m_fileName);
        String jsonString = Files.readString(Paths.get(m_fileName));
        reloadFromJson(jsonString);
    }

    public void reloadFromJson(final String jsonString) {
        JSONObject newValues;
        try (StringReader reader = new StringReader(jsonString)) {
            newValues = new JSONObject(new JSONTokener(reader));
        }
        for (AbstractConfigValue<?> param : AbstractConfigValue.accessedValues()) {
            var rawValue = getRawValue(newValues, param.getKey());
            if (rawValue == null) {
                continue;
            }
            try {
                param.parseJsonValue(rawValue);
            } catch (Exception ex) {
                throw new ConfigValueParseException(
                        "Could not parse config value for " + param.getKey(), ex);
            }
        }
        // All values parsed successfully; now actually apply the new values.
        m_values = newValues;
        for (AbstractConfigValue<?> param : AbstractConfigValue.accessedValues()) {
            param.update();
        }
    }

    public boolean containsKey(final String key) {
        return getRawValue(key) != null;
    }

    public SettableValueProvider<Integer[]> getInts(final String key) {
        return new IntegerConfigMultiValue(key);
    }

    public SettableValueProvider<Integer> getInt(final String key) {
        return new IntegerConfigValue(key);
    }

    public SettableValueProvider<Double[]> getDoubles(final String key) {
        return new DoubleConfigMultiValue(key);
    }

    public SettableValueProvider<Double> getDouble(final String key) {
        return new DoubleConfigValue(key);
    }

    public SettableValueProvider<Boolean> getBoolean(final String key) {
        return new BooleanConfigValue(key);
    }

    public SettableValueProvider<String> getString(final String key) {
        return new StringConfigValue(key);
    }

    public <E extends Enum<E>> SettableValueProvider<E> getEnum(
            final Class<E> enumClass, final String key) {
        return new EnumConfigValue<E>(enumClass, key);
    }

    public <E> void setValue(final String key, final E value) {
        String[] keyParts = splitKey(key);
        JSONObject parentObj = getParent(m_values, keyParts);
        parentObj.putOpt(keyParts[keyParts.length - 1], value == null ? JSONObject.NULL : value);

        for (AbstractConfigValue<?> otherValue : AbstractConfigValue.accessedValues()) {
            String[] otherValueKeyParts = splitKey(otherValue.getKey());
            if (isPrefix(keyParts, otherValueKeyParts) || isPrefix(otherValueKeyParts, keyParts)) {
                otherValue.update();
            }
        }
    }

    Object getRawValue(final String key) {
        return getRawValue(m_values, key);
    }

    private static Object getRawValue(final JSONObject obj, final String key) {
        String[] keyParts = splitKey(key);
        JSONObject parentObj = getParent(obj, keyParts);
        var rawValue = parentObj.opt(keyParts[keyParts.length - 1]);
        if (rawValue instanceof JSONObject) {
            throw new IllegalArgumentException(
                    "The config file cannot store both a single config "
                            + "setting and a group of config settings with the name "
                            + key
                            + " Please pick a different name");
        }
        if (rawValue == null) {
            parentObj.put(keyParts[keyParts.length - 1], JSONObject.NULL);
        }
        if (rawValue == JSONObject.NULL) {
            rawValue = null;
        }
        return rawValue;
    }

    static String[] splitKey(final String key) {
        return key.split(Pattern.quote(KEY_DELIMITER));
    }

    static boolean isPrefix(final String[] a, final String[] b) {
        if (a.length > b.length) {
            return false;
        }
        return Arrays.equals(a, 0, a.length, b, 0, a.length);
    }

    private static JSONObject getParent(JSONObject obj, final String[] keyParts) {
        for (int i = 0; i < keyParts.length - 1; ++i) {
            JSONObject subObj;
            try {
                subObj = (JSONObject) obj.opt(keyParts[i]);
            } catch (ClassCastException ex) {
                throw new IllegalArgumentException(
                        "The config file cannot store both a single config "
                                + "setting and a group of config settings with the name "
                                + String.join(KEY_DELIMITER, keyParts)
                                + " Please pick a different name for one of them.");
            }
            if (subObj == null) {
                subObj = new JSONObject();
                obj.put(keyParts[i], subObj);
            }
            obj = subObj;
        }
        return obj;
    }

    public String getJsonString() {
        return m_values.toString(2);
    }

    private void saveFileToPath(final String jsonString, final String path) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(jsonString);
        }
        Logger.get(Category.CONFIGURATION).logRaw(Severity.INFO, "Config file written to " + path);
    }

    public void saveFile(final String jsonString) throws IOException {
        saveFileToPath(jsonString, m_fileName);

        if (m_backupFileName != null) {
            saveFileToPath(jsonString, m_backupFileName);
        }
    }
}
