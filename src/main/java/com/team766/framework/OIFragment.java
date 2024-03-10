package com.team766.framework;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Fragment of an OI, with facilities to make it easy to set up {@link Condition}s for usage in the fragment's
 * {@link #handleOI} method.
 *
 * The overall OI for a robot will contain a set of fragments, typically one per set of controls (eg, driver, boxop, debug),
 * and it will call {@link #runOI(Context)} once per its own loop.  During each call to {@link #runOI}, the fragment
 * will evaluate any {@link Condition}s that were created for this fragment.  This simplifies OI logic that checks if a
 * specific condition is currently triggering (eg pressing or holding down a joystick button) or if a condition that had been triggering
 * in a previous iteration of the OI loop is no longer triggering in this iteration.
 */
public abstract class OIFragment extends LoggingBase {

    protected class Condition {
        private final BooleanSupplier condition;
        private boolean triggering = false;
        private boolean newlyTriggering = false;
        private boolean finishedTriggering = false;

        public Condition(BooleanSupplier condition) {
            this.condition = condition;
            register(this);
        }

        private void evaluate() {
            boolean triggeringNow = condition.getAsBoolean();
            if (triggeringNow) {
                newlyTriggering = !triggering;
                finishedTriggering = false;
            } else {
                finishedTriggering = triggering;
                newlyTriggering = false;
            }
            triggering = triggeringNow;
        }

        public boolean isTriggering() {
            return triggering;
        }

        public boolean isNewlyTriggering() {
            return newlyTriggering;
        }

        public boolean isFinishedTriggering() {
            return finishedTriggering;
        }
    }

    private final String name;
    private final List<Condition> conditions = new LinkedList<Condition>();

    /**
     * Creates a new OIFragment.
     * @param name The name of this part of the OI (eg, "BoxOpOI").  Used for logging.
     */
    public OIFragment(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    private void register(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Called at the beginning of {@link #runOI(Context)}, before evaluating any of the registered conditions
     * and before calling {@link #handleOI(Context)}.  Subclasses should override this if needed.
     */
    protected void handlePre() {}

    /**
     * OIFragments must override this method to implement their OI logic.  Typically called via the overall
     * OI's loop, once per iteration through the loop.  Can use any {@link Condition}s
     * they have set up to simplify checking if the {@link Condition} is {@link Condition#isTriggering()},
     * or, if it had been triggering in a previous iteration of the loop, if it is now
     * {@link Condition#isFinishedTriggering()}.
     *
     * @param context The {@link Context} running the OI.
     */
    protected abstract void handleOI(Context context);

    /**
     * Called after {@link #handleOI}, at the end of {@link #runOI}.  Subclasses should override this if needed.
     */
    protected void handlePost() {}

    /**
     * Called by a Robot's OI class, once per its loop.
     * Calls {@link #handlePre()}, evaluates all conditions once per call, and calls {@link #handlePost()}.
     * @param context The {@link Context} running the OI.
     */
    public final void runOI(Context context) {
        handlePre();
        for (Condition condition : conditions) {
            condition.evaluate();
        }
        handleOI(context);
        handlePost();
    }
}
