package com.team766.framework;

/**
 * Policies for how to handle a Rule's action when the action completes or the Rule stops triggering.
 */
public enum RulePersistence {
    /**
     * When the action completes, don't do anything. Any Mechanism reservations that the action held
     * are released. Also, the action may continue running after the Rule stops triggering.
     */
    ONCE,
    /**
     * When the action completes, don't do anything but retain the Mechanism reservations that the
     * action held until the Rule stops triggering. If the Rule stops triggering before the action
     * has completed, then the action will be terminated.
     */
    ONCE_AND_HOLD,
    /**
     * When the action completes, start executing the action again, until the Rule stops triggering.
     * The action will be terminated when the Rule stops triggering.
     */
    REPEATEDLY,
}
