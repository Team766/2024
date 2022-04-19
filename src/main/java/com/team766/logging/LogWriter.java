package com.team766.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.google.protobuf.CodedOutputStream;
import com.team766.config.ConfigFileReader;
import com.team766.library.LossyPriorityQueue;

public class LogWriter extends LogFormatProvider {
	private static final int QUEUE_SIZE = 50;

	public static String logFilePathBase = null;

	public static LogWriter instance;

	static {
		try {
			ConfigFileReader config_file = ConfigFileReader.getInstance();
			if (config_file != null) {
				logFilePathBase = config_file.getString("logFilePath").get();
				new File(logFilePathBase).mkdirs();
				final String timestamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
				final String logFilePath = new File(logFilePathBase, timestamp).getAbsolutePath();
				instance = new LogWriter(logFilePath);
				Logger.get(Category.CONFIGURATION).logRaw(Severity.INFO, "Logging to " + logFilePath);
			} else {
				instance = new LogWriter(OutputStream.nullOutputStream());
				Logger.get(Category.CONFIGURATION).logRaw(Severity.ERROR, "Config file is not available. Logs will only be in-memory and will be lost when the robot is turned off.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				instance = new LogWriter(OutputStream.nullOutputStream());
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			LoggerExceptionUtils.logException(e);
			Logger.get(Category.CONFIGURATION).logRaw(Severity.ERROR, "Log file is not available. Logs will only be in-memory and will be lost when the robot is turned off.");
		}
	}
	
	private LossyPriorityQueue<LogEntry> m_entriesQueue;
	
	private Thread m_workerThread;
	private boolean m_running = true;
	
	private HashMap<String, Integer> m_formatStringIndices = new HashMap<String, Integer>();
	
	private FileOutputStream m_fileStream = null;
	private OutputStream m_outputStream;
	private CodedOutputStream m_dataStream;
	
	public LogWriter(String filename) throws IOException {
		this(new FileOutputStream(filename));
	}

	public LogWriter(FileOutputStream fileStream) throws IOException {
		this((OutputStream)fileStream);
		m_fileStream = fileStream;
	}

	public LogWriter(OutputStream outputStream) throws IOException {
		m_entriesQueue = new LossyPriorityQueue<LogEntry>(QUEUE_SIZE, new LogEntryComparator());
		m_outputStream = outputStream;
		m_dataStream = CodedOutputStream.newInstance(m_outputStream);
		m_workerThread = new Thread(new Runnable() {
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
						// close() sends this sentinel element when it's time to exit
						return;
					}
					try {
						m_dataStream.writeMessageNoTag(entry);
					} catch (IOException e) {
						e.printStackTrace();
						Logger.get(Category.JAVA_EXCEPTION).logOnlyInMemory(
							Severity.ERROR,
							LoggerExceptionUtils.exceptionToString(e));
					}
				}
			}
		});
		m_workerThread.start();
	}

	public synchronized void close() throws IOException, InterruptedException {
		m_running = false;
		m_entriesQueue.add(LogEntryComparator.TERMINATION_SENTINAL);

		m_entriesQueue.waitForEmpty();
		m_workerThread.join();

		m_dataStream.flush();
		m_outputStream.flush();
		if (m_fileStream != null) {
			m_fileStream.flush();
		}

		if (m_fileStream != null) {
			m_fileStream.getFD().sync();
		}

		m_outputStream.close();
		if (m_fileStream != null) {
			m_fileStream.close();
		}
	}

	public synchronized LogEntry logStoredFormat(LogEntry.Builder entry) {
		final String format = entry.getMessageStr();
		Integer index = m_formatStringIndices.get(format);
		if (index == null) {
			index = m_formatStringIndices.size() + 1;
			m_formatStringIndices.put(format, index);
			if (m_formatStringIndices.size() % 100 == 0) {
				System.out.println("You're logging a lot of unique messages. Please switch to using logRaw()");
			}
			addFormatString(format, index);
		} else {
			entry.clearMessageStr();
		}
		entry.setMessageIndex(index);
		var entryMsg = entry.build();
		if (!m_running) {
			System.out.println("Log message during shutdown: " + LogEntryRenderer.renderLogEntry(entry.build(), null));
		} else {
			m_entriesQueue.add(entryMsg);
		}
		return entryMsg;
	}

	public synchronized LogEntry log(LogEntry entry) {
		if (!m_running) {
			System.out.println("Log message during shutdown: " + LogEntryRenderer.renderLogEntry(entry, null));
		} else {
			m_entriesQueue.add(entry);
		}
		return entry;
	}
}
