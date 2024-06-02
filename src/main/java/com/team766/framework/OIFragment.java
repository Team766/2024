package com.team766.framework;

import com.team766.framework.Statuses.StatusSource;
import com.team766.framework.resources.ResourceManager;
import com.team766.framework.resources.ResourcesMixin;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.ReflectionLogging;
import java.util.Optional;

/**
 * Fragment of an OI, with facilities to make it easy to set up {@link OICondition}s for usage in the fragment's
 * {@link #handleOI} method.
 *
 * The overall OI for a robot will contain a set of fragments, typically one per set of controls (eg, driver, boxop, debug),
 * and it will call {@link #runOI(Context)} once per its own loop.  During each call to {@link #runOI}, the fragment
 * will evaluate any {@link OICondition}s that were created for this fragment.  This simplifies OI logic that checks if a
 * specific condition is currently triggering (eg pressing or holding down a joystick button) or if a condition that had been triggering
 * in a previous iteration of the OI loop is no longer triggering in this iteration.
 */
public abstract class OIFragment implements ResourcesMixin, LoggingBase, StatusSource {
    private final String name;
    private final ResourceManager resourceManager;

    protected Category loggerCategory = Category.OPERATOR_INTERFACE;

    /**
     * Creates a new OIFragment.
     * @param name The name of this part of the OI (eg, "BoxOpOI").  Used for logging.
     */
    public OIFragment(OIFragment oi, String name) {
        this.name = name;
        this.resourceManager = oi.resourceManager;
    }

    /**
     * Creates a new OIFragment, using the name of the sub-class.
     */
    public OIFragment(OIFragment oi) {
        this.name = this.getClass().getSimpleName();
        this.resourceManager = oi.resourceManager;
    }

    /**
     * Creates a new OIFragment, using the name of the sub-class.
     */
    OIFragment(ResourceManager resourceManager) {
        this.name = this.getClass().getSimpleName();
        this.resourceManager = resourceManager;
    }

    @Override
    public final ResourceManager getResourceManager() {
        return resourceManager;
    }

    public final String getName() {
        return name;
    }

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    /**
     * OIFragments must override this method to implement their OI logic.
     */
    protected abstract void dispatch();

    protected final void updateStatus(Record status) {
        try {
            ReflectionLogging.recordOutput(
                    status, getName() + "/" + status.getClass().getSimpleName());
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
        Statuses.getInstance().add(status, this);
    }

    @Override
    public final boolean isStatusActive() {
        return true;
    }

    protected final <StatusRecord extends Record> Optional<StatusRecord> getStatus(
            Class<StatusRecord> c) {
        return Statuses.getStatus(c);
    }

    /**
     * Called by a Robot's OI class, once per its loop.
     */
    public void run() {
        dispatch();
    }
}
