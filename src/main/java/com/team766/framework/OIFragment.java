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

    protected void addRule(
            BooleanSupplier condition, Consumer<Context> then, Consumer<Context> whenDone) {
        rules.add(new Rule(condition, then, whenDone));
    }

    protected void pre(Context context) {}

    protected void post(Context context) {}

    public void handleOI(Context context) {
        pre(context);
        for (Rule rule : rules) {
            if (rule.triggered) {
                if (!rule.condition.getAsBoolean()) {
                    if (rule.whenDone != null) {
                        rule.whenDone.accept(context);
                    }
                }
            } else if (rule.condition.getAsBoolean()) {
                if (rule.then != null) {
                    rule.then.accept(context);
                }
                rule.triggered = true;
            }
        }
        post(context);
    }
}
