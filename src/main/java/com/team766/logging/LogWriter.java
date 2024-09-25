package com.team766.logging;

import com.google.protobuf.CodedOutputStream;
import com.team766.library.LossyPriorityQueue;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class LogWriter {
    private static final int QUEUE_SIZE = 50;

    private LossyPriorityQueue<LogEntry> m_entriesQueue;

    private Thread m_workerThread;
    private boolean m_running = true;

    private HashMap<String, Integer> m_formatStringIndices = new HashMap<String, Integer>();

    private OutputStream m_outputStream;
    private CodedOutputStream m_dataStream;

    private Severity m_minSeverity = Severity.INFO;

    public LogWriter(OutputStream out) throws IOException {
        m_entriesQueue = new LossyPriorityQueue<LogEntry>(QUEUE_SIZE, new LogEntryComparator());
        m_outputStream = out;
        m_dataStream = CodedOutputStream.newInstance(m_outputStream);
        m_workerThread =
                new Thread(
                        new Runnable() {
                            public void run() {
                                while (true) {
                                    LogEntry entry;
                                    try {
                                        entry = m_entriesQueue.poll();
                                    } catch (InterruptedException e) {
                                        System.out.println("Logger thread received interruption");
                                        continue;
                                    }
                                    if (entry == LogEntryComparator.TERMINATION_SENTINAL) {
                                        // close() sends this sentinel element when it's time to
                                        // exit
                                        return;
                                    }
                                    try {
                                        m_dataStream.writeMessageNoTag(entry);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Logger.get(Category.JAVA_EXCEPTION)
                                                .logOnlyInMemory(
                                                        Severity.ERROR,
                                                        LoggerExceptionUtils.exceptionToString(e));
                                    }
                                }
                            }
                        });
        m_workerThread.start();
    }

    public LogWriter(final String filename) throws IOException {
        this(new FileOutputStream(filename));
    }

    public void close() throws IOException, InterruptedException {
        m_running = false;
        m_entriesQueue.add(LogEntryComparator.TERMINATION_SENTINAL);

        m_entriesQueue.waitForEmpty();
        m_workerThread.join();

        m_dataStream.flush();
        m_outputStream.flush();

        if (m_outputStream instanceof FileOutputStream) {
            FileOutputStream fos = (FileOutputStream) m_outputStream;
            fos.getFD().sync();
        }

        m_outputStream.close();
    }

    public void setSeverityFilter(final Severity threshold) {
        m_minSeverity = threshold;
    }

    public void logStoredFormat(final LogEntry.Builder entry) {
        if (entry.getSeverity().compareTo(m_minSeverity) < 0) {
            return;
        }
        if (!m_running) {
            System.out.println(
                    "Log message during shutdown: "
                            + LogEntryRenderer.renderLogEntry(entry.build(), null));
            return;
        }
        final String format = entry.getMessageStr();
        Integer index = m_formatStringIndices.get(format);
        if (index == null) {
            index = m_formatStringIndices.size() + 1;
            m_formatStringIndices.put(format, index);
            if (m_formatStringIndices.size() % 100 == 0) {
                System.out.println(
                        "You're logging a lot of unique messages. Please switch to using logRaw()");
            }
        } else {
            entry.clearMessageStr();
        }
        entry.setMessageIndex(index);
        m_entriesQueue.add(entry.build());
    }

    public void log(final LogEntry entry) {
        if (entry.getSeverity().compareTo(m_minSeverity) < 0) {
            return;
        }
        if (!m_running) {
            System.out.println(
                    "Log message during shutdown: " + LogEntryRenderer.renderLogEntry(entry, null));
            return;
        }
        m_entriesQueue.add(entry);
    }
}
