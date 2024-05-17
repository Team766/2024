package com.team766.framework.conditions;

import edu.wpi.first.wpilibj2.command.Command;

@FunctionalInterface
public interface CommandSupplier {
    Command get() throws ResourceUnavailableException;
}
