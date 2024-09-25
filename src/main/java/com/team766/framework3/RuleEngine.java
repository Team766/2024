package com.team766.framework3;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RuleEngine implements LoggingBase {

    private static record RuleAction(Rule rule, Rule.TriggerType triggerType) {}

    // TODO: should we check for, require uniqueness in rule names?  possibly store rules by name?
    private final List<Rule> rules = new LinkedList<>();
    private final Map<Rule, Integer> rulePriorities = new HashMap<>();
    private BiMap<Command, RuleAction> ruleMap = HashBiMap.create();

    protected RuleEngine() {}

    @Override
    public Category getLoggerCategory() {
        return Category.RULES;
    }

    protected void addRule(Rule.Builder builder) {
        Rule rule = builder.build();
        rules.add(rule);
        int priority = rulePriorities.size();
        rulePriorities.put(rule, priority);
    }

    @VisibleForTesting
    /* package */ Map<String, Rule> getRuleNameMap() {
        Map<String, Rule> namedRules = new HashMap<>();
        for (Rule rule : rules) {
            namedRules.put(rule.getName(), rule);
        }
        return namedRules;
    }

    @VisibleForTesting
    /* package */ int getPriorityForRule(Rule rule) {
        if (rulePriorities.containsKey(rule)) {
            return rulePriorities.get(rule);
        }
        log(
                Severity.WARNING,
                "Could not find priority for rule " + rule.getName() + ".  Should not happen.");
        return Integer.MAX_VALUE;
    }

    protected Rule getRuleForTriggeredProcedure(Command command) {
        RuleAction ruleAction = ruleMap.get(command);
        return (ruleAction == null) ? null : ruleAction.rule;
    }

    public final void run() {
        Set<Mechanism<?>> mechanismsToUse = new HashSet<>();

        // TODO: when creating a Procedure, check that the reservations are the same as
        // what the Rule pre-computed.

        // evaluate each rule
        ruleLoop:
        for (Rule rule : rules) {
            try {
                rule.evaluate();

                // see if the rule is triggering
                Rule.TriggerType triggerType = rule.getCurrentTriggerType();
                if (triggerType != Rule.TriggerType.NONE) {
                    log(Severity.INFO, "Rule " + rule.getName() + " triggering: " + triggerType);

                    int priority = getPriorityForRule(rule);

                    // see if there are mechanisms a potential procedure would want to reserve
                    Set<Mechanism<?>> reservations = rule.getMechanismsToReserve();
                    log(Severity.INFO, "Rule " + rule.getName() + " would reserve " + reservations);
                    for (Mechanism<?> mechanism : reservations) {
                        // see if any of the mechanisms higher priority rules will use would also be
                        // used by this lower priority rule's procedure.
                        if (mechanismsToUse.contains(mechanism)) {
                            log(
                                    Severity.INFO,
                                    "RULE CONFLICT!  Ignoring rule: "
                                            + rule.getName()
                                            + "; mechanism "
                                            + mechanism.getName()
                                            + " already reserved by higher priority rule.");
                            continue ruleLoop;
                        }
                        // see if a previously triggered rule is still using the mechanism
                        Command existingCommand =
                                CommandScheduler.getInstance().requiring(mechanism);
                        if (existingCommand != null) {
                            // look up the rule
                            Rule existingRule = getRuleForTriggeredProcedure(existingCommand);
                            if (existingRule != null) {
                                // look up the priority
                                int existingPriority = getPriorityForRule(existingRule);
                                if (existingPriority < priority /* less is more */) {
                                    // existing rule takes priority.
                                    // don't proceed with this new rule.
                                    log(
                                            Severity.INFO,
                                            "RULE CONFLICT!  Ignoring rule: "
                                                    + rule
                                                    + "; mechanism "
                                                    + mechanism.getName()
                                                    + " already being used in CommandScheduler by higher priority rule.");
                                    continue ruleLoop;
                                }
                            }
                        }
                    }

                    // we're good to proceed
                    Procedure procedure = rule.getProcedureToRun();
                    if (procedure == null) {
                        continue;
                    }
                    log(
                            Severity.INFO,
                            "Running Procedure "
                                    + procedure.getName()
                                    + " with reservations "
                                    + reservations);

                    // TODO: check that the reservations have not changed
                    Command command = procedure.createCommand();
                    mechanismsToUse.addAll(reservations);
                    ruleMap.forcePut(command, new RuleAction(rule, triggerType));
                    command.schedule();
                }
            } catch (Exception ex) {
                log(
                        Severity.ERROR,
                        "Exception caught while trying to run(): "
                                + LoggerExceptionUtils.exceptionToString(ex));
            }
        }
    }
}
