package com.team766.hal;

public interface EncoderReader extends ControlInputReader {

    enum Type {
        CANcoder,
        REVThroughBoreDutyCycle
    };

    /**
     * Reset the Encoder distance to zero. Resets the current count to zero on the encoder.
     */
    void reset();

    /**
     * Return true iff the encoder's readings are up-to-date.
     */
    boolean isConnected();

    /**
     * Get the distance the robot has driven since the last reset.
     *
     * @return The distance driven since the last reset as scaled by the value from
     *         setDistancePerPulse().
     */
    double getDistance();

    /**
     * Get the current rate of the encoder. Units are distance per second as scaled by the value
     * from setDistancePerPulse().
     *
     * @return The current rate of the encoder.
     */
    double getRate();

    /**
     * Set the distance per pulse for this encoder. This sets the multiplier used to determine the
     * distance driven based on the count value from the encoder. Do not include the decoding type
     * in this scale. The library already compensates for the decoding type. Set this value based on
     * the encoder's rated Pulses per Revolution and factor in gearing reductions following the
     * encoder shaft. This distance can be in any units you like, linear or angular.
     *
     * @param distancePerPulse The scale factor that will be used to convert pulses to useful units.
     */
    void setDistancePerPulse(double distancePerPulse);

    // Implementation for ControlInputReader interface
    @Override
    default double getPosition() {
        return getDistance();
    }
}
