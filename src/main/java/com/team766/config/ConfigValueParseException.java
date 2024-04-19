package com.team766.config;

public class ConfigValueParseException extends RuntimeException {
    private static final long serialVersionUID = -3235627203813966130L;

    public ConfigValueParseException(final String message) {
        super(message);
    }

    public ConfigValueParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
