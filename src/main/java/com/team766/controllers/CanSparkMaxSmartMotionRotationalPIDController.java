package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.hal.RobotProvider;
import com.team766.library.SetValueProvider;
import com.team766.library.SettableValueProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import edu.wpi.first.math.MathUtil;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import java.io.IOError;
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;
import com.team766.controllers.pidstate.*;



public class CanSparkMaxSmartMotionRotationalPIDController {

	//Motorcontroller for the motor
	private MotorController mc;

	//Variables that we need to be able to do this type of PID
	private CANSparkMax csm;
	private SparkMaxPIDController pid;
	private SparkMaxAbsoluteEncoder abs;

	//PID Related Variables
	private static double deadzone = 0;
	private static double setPointPosition = 0;
	private static double comboOfTimesInsideDeadzone = 0;
	private static double minPos = 0;
	private static double maxPos = 0;

	//Reset encoder variables
	private double degreesToEncoderUnitsRatio = 0;
	private double encoderUnitsPerOneAbsolute = 0;

	//antigrav coefficient
	private static double antiGravK;
	//enum for which state the PID is in


	//the state of the PID
	private PIDSTATE theState = PIDSTATE.OFF;

	//constructor for the class not using an absolute encoder for kDutyCycle
	public CanSparkMaxSmartMotionRotationalPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second, final double ratio) {

		try {
			mc = RobotProvider.instance.getMotor(configName);
			csm = (CANSparkMax) mc;
			pid = csm.getPIDController();
			abs = csm.getAbsoluteEncoder(Type.kDutyCycle);
			abs.setZeroOffset(absEncoderOffset);
			degreesToEncoderUnitsRatio = ratio;

			double absoluteDifference = second.getAbsoluteValue() - first.getAbsoluteValue();
			double motorEncoderDiference = second.getMotorEncoderValue() - first.getMotorEncoderValue();

			encoderUnitsPerOneAbsolute = motorEncoderDiference / absoluteDifference;

			double relEncoder = absToEu(abs.getPosition() - absEncoderOffsetForZeroEncoderUnits);

			mc.setSensorPosition(relEncoder);

		} catch (IllegalArgumentException ill) {
			throw new RuntimeException("Error instantiating the PID controller: " + ill);
		}

	}

	private double absToEu(final double abs) {
		return encoderUnitsPerOneAbsolute * abs;
	}

	private double euToDegrees(final double eu) {
		return eu * degreesToEncoderUnitsRatio;
	}

	private double degreesToEu(final double degrees) {
		return degrees / degreesToEncoderUnitsRatio;
	}

	//changing all PID values at once
	public void setPIDF(final double p, final double i, final double d, final double ff) {
		pid.setP(p);
		pid.setI(i);
		pid.setD(d);
		pid.setFF(ff);
	}

	//changing the P value
	public void setP(final double p) {
		pid.setP(p);
	}

	//changing the I value
	public void setI(final double i) {
		pid.setI(i);
	}

	//changing the D value
	public void setD(final double d) {
		pid.setD(d);
	}

	//changing the FF value
	public void setFf(final double ff) {
		pid.setFF(ff);
	}

	/*
	 * Here we set the antigrav constant
	 * The mechanism is rotational, so this is the amount we multiply the Sine of the sensor position with
	 * @param k the value to set for the antigrav constant
	 */

	public void setAntigravConstant(final double k) {
		antiGravK = k;
	}

	private void antigrav() {
		mc.set(antiGravK * Math.sin(euToDegrees(mc.getSensorPosition())));
	}

	//changing the deadzone
	public void setDeadzone(final double dz) {
		deadzone = dz;
	}

	//changing the output range of the speed of the motors
	public void setOutputRange(final double min, final double max) {
		pid.setOutputRange(min, max);
	}

	//changing the neutral mode of the motor (brake/coast)
	public void setMotorMode(final NeutralMode mode) {
		mc.setNeutralMode(mode);
	}

	//setting the maximum and minimum locations that the motor can go to
	public void setMinMaxLocation(final double min, final double max) {
		maxPos = max;
		minPos = min;
	}

	//setting the maximum velocity of the motor
	public void setMaxVel(final double max) {
		pid.setSmartMotionMaxVelocity(max, 0);
		pid.setSmartMotionMinOutputVelocity(0, 0);
	}

	//setting the maximum acceleration of the motor
	public void setMaxAccel(final double max) {
		pid.setSmartMotionMaxAccel(max, 0);
	}

	//1st step to go to a position using the normal PID, setting what you want the position to be
	public void setSetpoint(final double positionInDegrees) {
		setPointPosition = MathUtil.clamp(degreesToEu(positionInDegrees), minPos, maxPos);
		theState = PIDSTATE.PID;
	}

	//Failsafe
	public void stop() {
		setPointPosition = mc.getSensorPosition();
		theState = PIDSTATE.OFF;
	}

	//run loop that actually runs the PID 
	//You need to call this function repedatly in the mechanism's run function as often as possible to get the best results
	public void run() {
		switch (theState) {
			case OFF:
				mc.set(0);
				break;
			case ANTIGRAV:
				if (setPointPosition <= (deadzone + mc.getSensorPosition()) && setPointPosition >= (mc.getSensorPosition() - deadzone)) {
					antigrav();
				} else {
					theState = PIDSTATE.PID;
				}
			case PID:
				if (setPointPosition <= (deadzone + mc.getSensorPosition()) && setPointPosition >= (mc.getSensorPosition() - deadzone)) {
					comboOfTimesInsideDeadzone++;
				} else {
					comboOfTimesInsideDeadzone = 0;
					pid.setReference(setPointPosition, ControlType.kSmartMotion);
				}

				if (comboOfTimesInsideDeadzone >= 6) {
					theState = PIDSTATE.ANTIGRAV;
				}
				break;
			default:
				LoggerExceptionUtils.logException(new IllegalArgumentException("Unknown state. Provided value: " + theState));
				break;
		}
	}
}