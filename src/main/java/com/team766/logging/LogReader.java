package com.team766.logging;

import java.io.FileInputStream;
import java.io.IOException;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

public class LogReader extends LogFormatProvider {

	private FileInputStream m_fileStream;
	private CodedInputStream m_dataStream;
	private LogEntry.Builder m_entryBuilder;

	public LogReader(String filename) throws IOException {
		m_fileStream = new FileInputStream(filename);
		m_dataStream = CodedInputStream.newInstance(m_fileStream);
		m_entryBuilder = LogEntry.newBuilder();
	}
	
	public LogEntry readNext() throws IOException {
		m_entryBuilder.clear();
		m_dataStream.readMessage(m_entryBuilder, ExtensionRegistryLite.getEmptyRegistry());
		LogEntry entry = m_entryBuilder.build();
		if (entry.hasMessageIndex() && entry.hasMessageStr()) {
			final int index = entry.getMessageIndex();
			final String format = entry.getMessageStr();
			addFormatString(format, index);
		}
		return entry;
	}
}
