package com.team766.controllers;

public class CanSparkMaxSmartMotionLinearPIDController extends ExtendableCanSparkMaxSmartMotionPIDController {
	
	private final double constantAntigravPower;
	
	public CanSparkMaxSmartMotionLinearPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second, final double antigravityPower) {
		super(configName, absEncoderOffset, absEncoderOffsetForZeroEncoderUnits, first, second);
		constantAntigravPower = antigravityPower;
	}

	public void runPIDs(){
		updateAntigrav(constantAntigravPower);
		run();
	}

}
