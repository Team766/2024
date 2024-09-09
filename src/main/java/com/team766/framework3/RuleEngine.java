package com.team766.framework3;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RuleEngine implements LoggingBase {

    private static record RuleAction(Rule rule, Rule.TriggerType triggerType) {}

    private final List<Rule> rules = new LinkedList<>();
    private final Map<Rule, Integer> rulePriorities = new HashMap<>();
    private BiMap<Command, RuleAction> ruleMap = HashBiMap.create();

    protected RuleEngine() {}

    @Override
    public Category getLoggerCategory() {
        // TODO: Is this the right default for RuleEngine?
        return Category.OPERATOR_INTERFACE;
    }

    protected void addRule(Rule.Builder builder) {
        Rule rule = builder.build();
        rules.add(rule);
        int priority = rulePriorities.size();
        rulePriorities.put(rule, priority);
    }

    protected Rule getRuleForTriggeredRunnable(Command runnable) {
        RuleAction ruleAction = ruleMap.get(runnable);
        return (ruleAction == null) ? null : ruleAction.rule;
    }

    public final void run() {
        Set<Subsystem> mechanismsToUse = new HashSet<>();

        // TODO: when creating a Command, check that the reservations are the same as
        // what the Rule pre-computed.

        // evaluate each rule
        for (Rule rule : rules) {
            try {
                rule.evaluate();

                // see if the rule is triggering/just finished triggering
                Rule.TriggerType triggerType = rule.getCurrentTriggerType();
                if (triggerType != Rule.TriggerType.NONE) {
                    log("Rule " + rule.getName() + " triggering: " + triggerType);

                    int priority = rulePriorities.get(rule);

                    // see if there are mechanisms a potential runnable would want to reserve
                    Set<Subsystem> reservations = rule.getMechanismsToReserve();
                    for (Subsystem mechanism : reservations) {
                        // see if any of the mechanisms higher priority rules will use would also be
                        // used by this lower priority rule's runnable.
                        if (mechanismsToUse.contains(mechanism)) {
                            log(
                                    "RULE CONFLICT!  Ignoring rule: "
                                            + rule.getName()
                                            + "; mechanism "
                                            + mechanism.getName()
                                            + " already reserved by higher priority rule.");
                            continue;
                        }
                        // see if a previously triggered rule is still using the mechanism
                        Command existingRunnable =
                                CommandScheduler.getInstance().requiring(mechanism);
                        if (existingRunnable != null) {
                            // look up the rule
                            Rule existingRule = getRuleForTriggeredRunnable(existingRunnable);
                            if (existingRule != null) {
                                // look up the priority
                                int existingPriority = rulePriorities.get(existingRule);
                                if (existingPriority < priority /* less is more */) {
                                    // existing rule takes priority.
                                    // don't proceed with this new rule.
                                    log(
                                            "RULE CONFLICT!  Ignoring rule: "
                                                    + rule
                                                    + "; mechanism "
                                                    + mechanism.getName()
                                                    + " already reserved by higher priority rule.");
                                    continue;
                                }
                            }
                        }
                    }

                    // we're good to proceed
                    Command runnable = rule.getRunnableToRun();
                    if (runnable == null) {
                        continue;
                    }
                    mechanismsToUse.addAll(reservations);
                    ruleMap.put(runnable, new RuleAction(rule, triggerType));
                    runnable.schedule();
                }
            } catch (Exception ex) {
                log(Severity.ERROR, LoggerExceptionUtils.exceptionToString(ex));
            }
        }
    }
}
