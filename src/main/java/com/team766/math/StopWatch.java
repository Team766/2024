package com.team766.math;

import com.team766.hal.Clock;

public class StopWatch {
	private double startTime = 0;
	private double stopTime = 0;
	private boolean running = false;

	private Clock clock;

	public StopWatch(Clock clock) {
		this.clock = clock;
	}

	public void start() {
		startTime = clock.getTime();
		running = true;
	}

	public void startIfNecessary() {
		if (!running) {
			startTime = clock.getTime();
			running = true;
		}
	}

	public void stop() {
		stopTime = clock.getTime();
		running = false;
	}

	public void reset() {
		stopTime = 0;
		if (running) {
			startTime = clock.getTime();
		} else {
			startTime = 0;
		}
	}

	public double elapsedSeconds() {
		if (running) {
			return clock.getTime() - startTime;
		} else {
			return stopTime - startTime;
		}
	}
}
