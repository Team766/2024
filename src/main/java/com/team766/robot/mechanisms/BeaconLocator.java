package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.BeaconReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;

public class BeaconLocator extends Mechanism {
	private BeaconReader beaconSensor = RobotProvider.instance.getBeaconSensor();

	public BeaconLocator() {
		loggerCategory = Category.CAMERA;
	}

	@Override
	public void run() {
		super.run();

		var beacons = beaconSensor.getBeacons();
		String beaconInfo = "Beacons:\n";
		for (var beacon : beacons) {
			beaconInfo += String.format("  %f %f %f %f %f %f\n", beacon.x, beacon.y, beacon.z, beacon.yaw, beacon.pitch, beacon.roll);
		}
		log(beaconInfo);
	}
}
