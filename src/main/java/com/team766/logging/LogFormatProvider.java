package com.team766.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LogFormatProvider {
	private ArrayList<String> m_formatStrings;

	public LogFormatProvider() {
		m_formatStrings = new ArrayList<String>();
	}

	synchronized String getFormatString(int index) {
		String str;
		try {
			str = m_formatStrings.get(index);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalArgumentException("Unknown format string index: " + index);
		}
		if (str == null) {
			throw new IllegalArgumentException("Unknown format string index: " + index);
		}
		return str;
	}

	public synchronized List<String> listFormatStrings() {
		return Collections.unmodifiableList(m_formatStrings);
	}

	protected synchronized void addFormatString(String format, int index) {
		m_formatStrings.ensureCapacity(index + 1);
		while (m_formatStrings.size() <= index) {
			m_formatStrings.add(null);
		}
		m_formatStrings.set(index, format);
	}
}
