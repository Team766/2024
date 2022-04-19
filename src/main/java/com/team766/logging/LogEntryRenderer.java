package com.team766.logging;

public class LogEntryRenderer {
	public static String renderLogEntry(LogEntry entry, LogFormatProvider formatProvider) {
		String message = entry.hasMessageStr() ? entry.getMessageStr()
				: formatProvider.getFormatString(entry.getMessageIndex());
		final int argCount = entry.getArgCount();
		if (argCount == 0) {
			return message;
		} else {
			final Object[] args =
				entry.getArgList().stream()
					.map(SerializationUtils::protoToValue).toArray();
			return String.format(message, args);
		}
	}
}