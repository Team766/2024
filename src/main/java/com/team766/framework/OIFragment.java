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
 * and it will call {@link #run(Context)} once per its own loop.  During each call to {@link #runOI}, the fragment
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
    private boolean conditionsEvaluated = false;

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

    private void register(Condition condition) {
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
     * Subclasses should call this once per call to {@link #handleOI}.  Evaluates each of the
     * conditions for this fragment.
     */
    protected void evaluateConditions() {
        if (conditionsEvaluated) {
            log(Severity.WARNING, "evaluateConditions() called multiple times in this loop!");
            throw new IllegalStateException(
                    "evaluateConditions() called multiple times in this loop!");
        }
        for (Condition condition : conditions) {
            condition.evaluate();
        }
        conditionsEvaluated = true;
    }

    /**
     * Called by a Robot's OI class, once per its loop.
     * @param context The {@link Context} running the OI.
     */
    public final void run(Context context) {
        // reset for the next call
        conditionsEvaluated = false;

        handleOI(context);
        if (conditions.size() > 0 && !conditionsEvaluated) {
            log(Severity.WARNING, "Fragment did not call evaluateCondition()!");
            throw new IllegalStateException("Fragment did not call evaluateCondition!");
        }
    }
}
