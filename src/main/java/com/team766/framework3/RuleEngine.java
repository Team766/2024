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

/**
 * {@link RuleEngine}s manage and process a set of {@link Rule}s.  Subclasses should add rules via
 * {@link #addRule(com.team766.framework3.Rule.Builder)}.  {@link Rule}s have an implicit priority based on insertion order - the first {@link Rule} to be added is highest priority, etc.
 *
 * Callers should then call {@link #run} once per iteration.  Each call to {@link #run} evaluates each of the contained {@link Rule}s, firing the associated {@link Procedure}
 * {@link Supplier}s when {@link Rule}s are NEWLY triggering, are CONTINUING to trigger, or are FINISHED triggering.
 *
 * The {@link RuleEngine} also pays attention to the {@link Mechanism}s that these {@link Procedure}s reserve.
 * For a {@link Rule} to trigger, its predicate must be satisfied -- and, the {@link Mechanism}s the corresponding {@link Procedure} would reserve
 * must not be in use or about to be in use from a higher priority {@link Rule}.
 */
public class RuleEngine implements LoggingBase {

    private static record RuleAction(Rule rule, Rule.TriggerType triggerType) {}

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

        // TODO(MF3): when creating a Procedure, check that the reservations are the same as
        // what the Rule pre-computed.

        // evaluate each rule
        ruleLoop:
        for (Rule rule : rules) {
            try {
                rule.evaluate();

                // see if the rule is triggering
                final Rule.TriggerType triggerType = rule.getCurrentTriggerType();
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
                            rule.reset();
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
                                                    + rule.getName()
                                                    + "; mechanism "
                                                    + mechanism.getName()
                                                    + " already being used in CommandScheduler by higher priority rule.");
                                    rule.reset();
                                    continue ruleLoop;
                                } else if (rule != existingRule) {
                                    // new rule takes priority
                                    // reset existing rule
                                    log(
                                            Severity.INFO,
                                            "Pre-empting rule: "
                                                    + existingRule.getName()
                                                    + "; mechanism "
                                                    + mechanism.getName()
                                                    + " will now be reserved by higher priority rule "
                                                    + rule.getName());
                                    existingRule.reset();
                                }
                            }
                        }
                    }

                    // we're good to proceed

                    if (triggerType == Rule.TriggerType.FINISHED
                            && rule.getCancellationOnFinish()
                                    == Rule.Cancellation.CANCEL_NEWLY_ACTION) {
                        var newlyCommand =
                                ruleMap.inverse().get(new RuleAction(rule, Rule.TriggerType.NEWLY));
                        if (newlyCommand != null) {
                            newlyCommand.cancel();
                        }
                    }

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

                    // TODO(MF3): check that the reservations have not changed
                    Command command = procedure.createCommandToRunProcedure();
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
