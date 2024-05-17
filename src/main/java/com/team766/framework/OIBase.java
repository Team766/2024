package com.team766.framework;

import com.team766.framework.conditions.RuleEngine;
import com.team766.logging.LoggerExceptionUtils;

public abstract class OIBase extends OIFragment {
    public OIBase() {
        this(new RuleEngine());
    }

    private OIBase(RuleEngine ruleEngine) {
        super(() -> ruleEngine);
    }

    @Override
    public void run() {
        try {
            // if (!RobotProvider.instance.hasNewDriverStationData()) {
            //     return;
            // }
            // RobotProvider.instance.refreshDriverStationData();

            getRuleEngine().startFrame();
            super.run();
            getRuleEngine().endFrame();
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }
}
