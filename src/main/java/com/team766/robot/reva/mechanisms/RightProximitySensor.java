package com.team766.robot.reva.mechanisms;

import com.playingwithfusion.TimeOfFlight.*;
import com.team766.framework.Mechanism;

public class RightProximitySensor extends Mechanism {
	
	private TimeOfFlight sensor;


	//This should be the amount that getRange() should return less than for a note to be classified as in
	private static double threshold = 0; // needs calibration

	public RightProximitySensor(){
		sensor = new TimeOfFlight(0); // needs calibration

		sensor.setRangingMode(RangingMode.Short);
	}

	public boolean isNoteReady(){
		return (threshold) > sensor.getRange() && sensor.isRangeValid();
	}

	
}