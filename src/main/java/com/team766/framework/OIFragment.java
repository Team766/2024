package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Severity;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Fragment of an OI, with facilities to make it easy to set up {@link Condition}s for usage in the fragment's
 * {@link #handleOI} method.
 *
 * The overall OI for a robot will contain a set of fragments, typically one per set of controls (eg, driver, boxop, debug),
 * and it will call {@link #run(Context)} once per its own loop.  During each call to {@link #run}, the fragment
 * will update any {@link Condition}s that were created for this fragment.  This simplifies OI logic that checks if a
 * specific condition is currently triggering (eg pressing or holding down a joystick button) or if a condition that had been triggering
 * in a previous iteration of the OI loop is no longer triggering in this iteration.
 */
public abstract class OIFragment extends LoggingBase {

    private class OIConditionBase implements Condition {
        private boolean valid = false;
        private boolean triggering = false;
        private boolean newlyTriggering = false;
        private boolean finishedTriggering = false;

        protected OIConditionBase() {
            register(this);
        }

        protected void invalidate() {
            valid = false;
        }

        protected boolean isValid() {
            return valid;
        }

        /**
         * Subclasses of OIFragment should call this once per call to {@link OIFragment#handleOI}.
         */
        protected void update(final boolean triggeringNow) {
            if (valid) {
                throw new IllegalStateException(
                        "update() called multiple times on this OIFragment.Condition");
            }

            newlyTriggering = !triggering && triggeringNow;
            finishedTriggering = triggering && !triggeringNow;
            triggering = triggeringNow;
            valid = true;
        }

        public boolean isTriggering() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this OIFragment.Condition before calling isTriggering()");
            }
            return triggering;
        }

        public boolean isNewlyTriggering() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this OIFragment.Condition before calling isNewlyTriggering()");
            }
            return newlyTriggering;
        }

        public boolean isFinishedTriggering() {
            if (!valid) {
                throw new IllegalStateException(
                        "Call update() on this OIFragment.Condition before calling isFinishedTriggering()");
            }
            return finishedTriggering;
        }
    }

    protected class InlineCondition extends OIConditionBase {
        public InlineCondition() {}

        // Method redefined to make it public.
        public void update(boolean triggeringNow) {
            super.update(triggeringNow);
        }
    }

    protected class PredefinedCondition extends OIConditionBase {
        private final BooleanSupplier condition;

        public PredefinedCondition(BooleanSupplier condition) {
            this.condition = condition;
        }

        public void update() {
            super.update(condition.getAsBoolean());
        }
    }

    protected class AutoUpdateCondition extends OIConditionBase {
        private final BooleanSupplier condition;

        public AutoUpdateCondition(BooleanSupplier condition) {
            this.condition = condition;
        }

        @Override
        protected void invalidate() {
            super.invalidate();

            super.update(condition.getAsBoolean());
        }
    }

    private final String name;
    private final List<OIConditionBase> conditions = new LinkedList<>();

    /**
     * Creates a new OIFragment.
     * @param name The name of this part of the OI (eg, "BoxOpOI").  Used for logging.
     */
    public OIFragment(String name) {
        loggerCategory = Category.OPERATOR_INTERFACE;
        this.name = name;
    }

    /**
     * Creates a new OIFragment, using the name of the sub-class.
     */
    public OIFragment() {
        loggerCategory = Category.OPERATOR_INTERFACE;
        this.name = this.getClass().getSimpleName();
    }

    public final String getName() {
        return name;
    }

    private void register(OIConditionBase condition) {
        conditions.add(condition);
    }

    /**
     * OIFragments must override this method to implement their OI logic.  Typically called via the overall
     * OI's loop, once per iteration through the loop.  Can use any {@link Condition}s
     * they have set up to simplify checking if the {@link Condition} is {@link Condition#isTriggering()},
     * or, if it had been triggering in a previous iteration of the loop, if it is now
     * {@link Condition#isFinishedTriggering()}.
     *
     * When implementing handleOI, you must call {@link #evaluateConditions()} in order to use any
     * Conditions; omitting this call will result in calls to {@link #run} throwing an IllegalStateException.
     *
     * @param context The {@link Context} running the OI.
     */
    protected abstract void handleOI(Context context);

    /**
     * Called by a Robot's OI class, once per its loop.
     * @param context The {@link Context} running the OI.
     */
    public final void run(Context context) {
        // reset for the next call
        for (var condition : conditions) {
            condition.invalidate();
        }

        handleOI(context);

        for (var condition : conditions) {
            if (!condition.isValid()) {
                log(Severity.WARNING, "Fragment did not call update() on all Conditions!");
                throw new IllegalStateException(
                        "Fragment did not call update() on all Conditions!");
            }
        }
    }
}
