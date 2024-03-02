package com.team766.robot.reva.mechanisms;

import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.library.ValueProvider;

public class RightProximitySensor extends Mechanism {

    private TimeOfFlight sensor;

    // This should be the amount that getRange() should return less than for a note to be classified
    // as in
    private static ValueProvider<Double> threshold =
            ConfigFileReader.getInstance()
                    .getDouble("RightProximitySensor.threshold"); // needs calibration

    public RightProximitySensor() {
        sensor = new TimeOfFlight(0); // needs calibration

        sensor.setRangingMode(RangingMode.Short, 80);
    }

    public boolean isNoteReady() {
        return (threshold.get()) > sensor.getRange() && sensor.isRangeValid();
    }

	public void run(){
		log("Sensor thingy: " + sensor.getRange());
	}
}
