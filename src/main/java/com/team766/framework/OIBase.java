package com.team766.framework;

import com.team766.framework.resources.ResourceManager;
import com.team766.logging.LoggerExceptionUtils;

public abstract class OIBase extends OIFragment {
    public OIBase() {
        super(new ResourceManager());
    }

    @Override
    public void run() {
        try {
            // if (!RobotProvider.instance.hasNewDriverStationData()) {
            //     return;
            // }
            // RobotProvider.instance.refreshDriverStationData();

            getResourceManager().startFrame();
            super.run();
            getResourceManager().endFrame();
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }
}
