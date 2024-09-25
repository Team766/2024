package com.team766.logging;

import com.team766.config.ConfigFileReader;
import com.team766.library.CircularBuffer;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicReference;

public final class Logger {

    private static AtomicReference<StringLogEntry> wpiLogEntryReference =
            new AtomicReference<StringLogEntry>();

    private static class LogUncaughtException implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(final Thread t, final Throwable e) {
            e.printStackTrace();

            LoggerExceptionUtils.logException(e);

            if (m_logWriter != null) {
                try {
                    m_logWriter.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            System.exit(1);
        }
    }

    private static final int MAX_NUM_RECENT_ENTRIES = 100;
    private static final String LOG_FILE_PATH_KEY = "logFilePath";

    private static EnumMap<Category, Logger> m_loggers =
            new EnumMap<Category, Logger>(Category.class);
    private static LogWriter m_logWriter = null;
    private CircularBuffer<LogEntry> m_recentEntries =
            new CircularBuffer<LogEntry>(MAX_NUM_RECENT_ENTRIES);
    private static Object s_lastWriteTimeSync = new Object();
    private static long s_lastWriteTime = 0L;

    public static String logFilePathBase = null;

    static {
        for (Category category : Category.values()) {
            m_loggers.put(category, new Logger(category));
        }
        try {
            ConfigFileReader config_file = ConfigFileReader.getInstance();
            if (config_file != null && config_file.containsKey(LOG_FILE_PATH_KEY)) {
                logFilePathBase = config_file.getString(LOG_FILE_PATH_KEY).get();
                new File(logFilePathBase).mkdirs();
                final String timestamp =
                        new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
                final String logFilePath = new File(logFilePathBase, timestamp).getAbsolutePath();
                m_logWriter = new LogWriter(logFilePath);
                get(Category.CONFIGURATION).logRaw(Severity.INFO, "Logging to " + logFilePath);
            } else {
                get(Category.CONFIGURATION)
                        .logRaw(
                                Severity.ERROR,
                                "Config file does not specify "
                                        + LOG_FILE_PATH_KEY
                                        + ". Logs will only be in-memory and will be lost when the robot is turned off.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerExceptionUtils.logException(e);
        }

        Thread.setDefaultUncaughtExceptionHandler(new LogUncaughtException());
    }

    public static void enableLoggingToDataLog(boolean enabled) {
        if (enabled) {
            wpiLogEntryReference.compareAndSet(
                    null, new StringLogEntry(DataLogManager.getLog(), "/maroon/logs"));
        } else {
            wpiLogEntryReference.set(null);
        }
    }

    public static Logger get(final Category category) {
        return m_loggers.get(category);
    }

    static long getTime() {
        long nowNanosec = new Date().getTime() * 1000000;
        synchronized (s_lastWriteTimeSync) {
            // Ensure that log entries' timestamps are unique. This is important
            // because the log viewer uses an entry's timestamp as a unique ID,
            // and we don't want two different log entries to accidentally
            // compare as equal.
            nowNanosec = s_lastWriteTime = Math.max(nowNanosec, s_lastWriteTime);
        }
        return nowNanosec;
    }

    private final Category m_category;

    private Logger(final Category category) {
        m_category = category;
    }

    public static boolean isLoggingToDataLog() {
        return wpiLogEntryReference.get() != null;
    }

    public Collection<LogEntry> recentEntries() {
        return Collections.unmodifiableCollection(m_recentEntries);
    }

    public void logData(final Severity severity, final String format, final Object... args) {
        var entry =
                LogEntry.newBuilder()
                        .setTime(getTime())
                        .setSeverity(severity)
                        .setCategory(m_category);
        String message = String.format(format, args);
        entry.setMessageStr(message);
        m_recentEntries.add(entry.build());
        entry.setMessageStr(format);

        for (Object arg : args) {
            var logValue = LogValue.newBuilder();
            SerializationUtils.valueToProto(arg, logValue);
            entry.addArg(logValue.build());
        }
        if (m_logWriter != null) {
            m_logWriter.logStoredFormat(entry);
        }
        StringLogEntry stringLogEntry = wpiLogEntryReference.get();
        if (stringLogEntry != null && (severity.compareTo(Severity.INFO) >= 0)) {
            stringLogEntry.append(message);
        }
        System.out.println(message);
    }

    public void logRaw(final Severity severity, final String message) {
        var entry =
                LogEntry.newBuilder()
                        .setTime(getTime())
                        .setSeverity(severity)
                        .setCategory(m_category)
                        .setMessageStr(message)
                        .build();
        m_recentEntries.add(entry);
        if (m_logWriter != null) {
            m_logWriter.log(entry);
        }
        StringLogEntry stringLogEntry = wpiLogEntryReference.get();
        if (stringLogEntry != null && (severity.compareTo(Severity.INFO) >= 0)) {
            stringLogEntry.append(message);
        }
        System.out.println(message);
    }

    void logOnlyInMemory(final Severity severity, final String message) {
        var entry =
                LogEntry.newBuilder()
                        .setTime(getTime())
                        .setSeverity(severity)
                        .setCategory(m_category)
                        .setMessageStr(message)
                        .build();
        m_recentEntries.add(entry);
    }
}
