package com.team766.framework;

import com.team766.framework.conditions.RuleEngine;
import com.team766.framework.resources.ResourceManager;
import com.team766.logging.LoggerExceptionUtils;

public abstract class OIBase extends OIFragment {
    public OIBase() {
        super(new ResourceManager(), new RuleEngine());
    }

    @Override
    public void run() {
        try {
            // if (!RobotProvider.instance.hasNewDriverStationData()) {
            //     return;
            // }
            // RobotProvider.instance.refreshDriverStationData();

            getResourceManager().startFrame();
            getRuleEngine().startFrame();
            super.run();
            getRuleEngine().endFrame();
            getResourceManager().endFrame();
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }
}
