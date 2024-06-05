package com.team766.framework;

import com.team766.framework.annotations.ReservableAnnotation;
import com.team766.framework.resources.Reservable;

@ReservableAnnotation
public abstract class RobotSystem<StatusRecord extends Record, Goal>
        extends RobotSubsystem<StatusRecord, Goal> implements Reservable {}
