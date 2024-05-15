package com.team766.framework;

import com.team766.framework.conditions.RuleEngine;

public abstract class OIBase extends OIFragment {
    public OIBase() {
        this(new RuleEngine());
    }

    private OIBase(RuleEngine ruleEngine) {
        super(() -> ruleEngine);
    }

    @Override
    public void run() {
        // if (!RobotProvider.instance.hasNewDriverStationData()) {
        //     return;
        // }
        // RobotProvider.instance.refreshDriverStationData();

        super.run();
    }
}
