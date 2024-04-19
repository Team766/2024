package com.team766.hal;

public interface BeaconReader {
    class BeaconPose {
        public double x;
        public double y;
        public double z;
        public double yaw;
        public double pitch;
        public double roll;
    }

    BeaconPose[] getBeacons();
}
