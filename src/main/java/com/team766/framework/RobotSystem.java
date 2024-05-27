package com.team766.framework;

import com.team766.framework.resources.Reservable;

public abstract class RobotSystem<StatusRecord extends Record, Goal>
        extends RobotSubsystem<StatusRecord, Goal> implements Reservable {}
