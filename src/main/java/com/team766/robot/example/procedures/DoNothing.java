package com.team766.robot.example.procedures;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.example.mechanisms.ExampleMechanism;

public class DoNothing extends Procedure {
    private final ExampleMechanism exampleMechanism;

    public DoNothing(ExampleMechanism exampleMechanism) {
        this.exampleMechanism = reserve(exampleMechanism);
    }

    @Override
    public void run(final Context context) {}
}
