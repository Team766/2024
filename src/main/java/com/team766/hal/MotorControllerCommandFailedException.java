package com.team766.hal;

public class MotorControllerCommandFailedException extends RuntimeException {

    public MotorControllerCommandFailedException(final String message) {
        super(message);
    }

    public MotorControllerCommandFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
