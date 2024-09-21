package com.team766.hal.wpilib;

import com.team766.hal.EncoderReader;

public class Encoder extends edu.wpi.first.wpilibj.Encoder implements EncoderReader {
    public Encoder(final int channelA, final int channelB) {
        super(channelA, channelB);
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
