package com.team766.robot.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;

public class Elevator extends Mechanism {
	private MotorController motor;
	private EncoderReader encoder;

	private double setpoint;
	private boolean enabled = false;

	private ValueProvider<Double> gain;
	private ValueProvider<Double> minPosition;
	private ValueProvider<Double> maxPosition;

	public Elevator() {
		motor = RobotProvider.instance.getMotor("elevator.motor");
		encoder = RobotProvider.instance.getEncoder("elevator.encoder");
		gain = ConfigFileReader.instance.getDouble("elevator.control_gain");
		minPosition = ConfigFileReader.instance.getDouble("elevator.min_position");
		maxPosition = ConfigFileReader.instance.getDouble("elevator.max_position");
	}

	public void setSetpoint(double position) {
		checkContextOwnership();

		setpoint = position;
		if (minPosition.hasValue()) {
			setpoint = Math.max(setpoint, minPosition.get());
		}
		if (maxPosition.hasValue()) {
			setpoint = Math.min(setpoint, maxPosition.get());
		}
		enabled = true;
	}

	public double getSetpoint() {
		return setpoint;
	}

	public double getPosition() {
		return encoder.getDistance();
	}

	@Override
	public void run() {
		if (!enabled) {
			return;
		}
		if (!gain.hasValue()) {
			log(Severity.ERROR, "Control gain not specified in config file");
			return;
		}

		double error = setpoint - getPosition();
		double power = gain.get() * error;
		motor.set(power);
	}
}