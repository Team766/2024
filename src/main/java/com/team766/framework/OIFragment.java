package com.team766.framework;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class OIFragment extends LoggingBase {
    private static class Rule {
        public BooleanSupplier condition;
        public Consumer<Context> then;
        public Consumer<Context> whenDone;
        public boolean triggered = false;

        public Rule(BooleanSupplier condition, Consumer<Context> then, Consumer<Context> whenDone) {
            this.condition = condition;
            this.then = then;
            this.whenDone = whenDone;
        }

        // TODO: add equals, hashCode, toString
    }

    private final String name;
    private List<Rule> rules = new LinkedList<Rule>();

    public OIFragment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Adds a rule for this OI.
     * As long as the supplied condition is triggered, run the "then" behavior.
     * If the condition gets triggered, once it stops being triggered, run the "whenDone" behavior.
     *
     * @param condition The condition to check.
     * @param then The behavior to run when the condition is triggered.  May be null.
     * @param whenDone The behavior to run after the condition had been triggered and is no longer triggered.  May be null.
     */
    protected void addRule(
            BooleanSupplier condition, Consumer<Context> then, Consumer<Context> whenDone) {
        rules.add(new Rule(condition, then, whenDone));
    }

    /**
     * Any logic or computations to run before checking any rules.
     *
     * @param context The context being used to run this OIFragment.
     */
    protected void pre(Context context) {}

    /**
     * Any logic or computations to run after checking all of the rules.
     *
     * @param context The context being used to run this OIFragment.
     */
    protected void post(Context context) {}

    public void handleOI(Context context) {
        // run any "pre" logic
        pre(context);
        for (Rule rule : rules) {
            // if this rule's condition is true, do the associated "then" action
            if (rule.condition.getAsBoolean()) {
                if (rule.then != null) {
                    rule.then.accept(context);
                }
                // and mark the rule as "triggered"
                rule.triggered = true;
            } else {
                // the rule condition's is false
                // see if the rule had been "triggered".  if so, do the associated "done" action.
                if (rule.triggered) {
                    if (rule.whenDone != null) {
                        rule.whenDone.accept(context);
                    }
                    // and mark the rule as no longer being "triggered"
                    rule.triggered = false;
                }
            }
        }
        // run any "post" logic
        post(context);
    }
}
