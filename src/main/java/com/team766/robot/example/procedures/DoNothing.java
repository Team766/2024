package com.team766.robot.example.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class DoNothing extends Procedure {
    public DoNothing() {
        super(NO_RESERVATIONS);
    }

    @Override
    public void run(final Context context) {}
}
