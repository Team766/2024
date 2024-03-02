package com.team766.robot.reva.mechanisms;

import com.playingwithfusion.TimeOfFlight;
import com.team766.framework.Mechanism;

public class RightProximitySensor extends Mechanism {
	
	private TimeOfFlight sensor;

	//This should be the value returned by getRange() 
	private static double noNoteRange;

	//This should be the amount that getRange() should return less than noNoteRange for a note to be classified as in
	private static double threshold = null;

	public RightProximitySensor(){
		sensor = new TimeOfFlight(null);

		//Notes should not start in the intake for this logic to work
		noNoteRange = sensor.getRange();
	}

	public boolean isNoteReady(){
		return (noNoteRange - threshold) > sensor.getRange() && sensor.isRangeValid();
	}

	
}
