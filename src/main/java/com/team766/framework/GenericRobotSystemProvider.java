package com.team766.framework;

import com.team766.framework.resources.Reservable;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface GenericRobotSystemProvider {
    <T extends Subsystem & Reservable> T getRobotSystem(Class<T> clazz);
}
