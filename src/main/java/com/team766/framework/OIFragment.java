package com.team766.framework;

import java.util.LinkedList;
import java.util.List;

public abstract class OIFragment extends LoggingBase {
    private final String name;
    private final List<OICondition> conditions = new LinkedList<OICondition>();

    public OIFragment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /*package */ void register(OICondition condition) {
        conditions.add(condition);
    }

    protected void handlePre() {}

    protected abstract void handleOI(Context context);

    protected void handlePost() {}

    public final void runOI(Context context) {
        handlePre();
        for (OICondition condition : conditions) {
            condition.evaluate();
        }
        handleOI(context);
        handlePost();
    }
}
