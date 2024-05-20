package com.team766.framework;

import com.team766.framework.Statuses.StatusSource;
import com.team766.framework.conditions.RuleEngineProvider;
import com.team766.framework.conditions.RulesMixin;
import com.team766.logging.Category;
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
public abstract class OIFragment extends RulesMixin implements LoggingBase, StatusSource {
    private final String name;

    protected Category loggerCategory = Category.OPERATOR_INTERFACE;

    /**
     * Creates a new OIFragment.
     * @param name The name of this part of the OI (eg, "BoxOpOI").  Used for logging.
     */
    public OIFragment(RuleEngineProvider oi, String name) {
        super(oi);
        this.name = name;
    }

    /**
     * Creates a new OIFragment, using the name of the sub-class.
     */
    public OIFragment(RuleEngineProvider oi) {
        super(oi);
        this.name = this.getClass().getSimpleName();
    }

    public final String getName() {
        return name;
    }

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    /**
     * OIFragments must override this method to implement their OI logic.  Typically called via the overall
     * OI's loop, once per iteration through the loop.  Can use any {@link OICondition}s
     * they have set up to simplify checking if the {@link OICondition} is {@link OICondition#isTriggering()},
     * or, if it had been triggering in a previous iteration of the loop, if it is now
     * {@link OICondition#isFinishedTriggering()}.
     */
    protected abstract void dispatch();

    protected final void updateStatus(Record status) {
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
     * Calls {@link #handlePre()}, evaluates all conditions once per call, and calls {@link #handlePost()}.
     * @param context The {@link Context} running the OI.
     */
    public void run() {
        dispatch();
    }
}
