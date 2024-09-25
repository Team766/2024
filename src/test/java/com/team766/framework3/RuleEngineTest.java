package com.team766.framework3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team766.TestCase3;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;

public class RuleEngineTest extends TestCase3 {

    private static class PeriodicPredicate implements BooleanSupplier {
        private final int period;
        private final int delay;
        private int currentCycle = 0;

        public PeriodicPredicate(int period, int delay) {
            this.period = period;
            this.delay = delay;
        }

        public PeriodicPredicate(int period) {
            this(period, 0);
        }

        @Override
        public boolean getAsBoolean() {
            boolean value = currentCycle % period == delay;
            ++currentCycle;
            return value;
        }
    }

    private final FakeMechanism1 fm1 = new FakeMechanism1();
    private final FakeMechanism2 fm2 = new FakeMechanism2();
    private final FakeMechanism3 fm3 = new FakeMechanism3();

    @Test
    public void testAddRuleAndGetPriority() {
        // simply test that rules we add are added - and at the expected priority

        // create simple RuleEngine with two rules
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new PeriodicPredicate(2))
                                        .withNewlyTriggeringProcedure(
                                                () -> new FakeProcedure(2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new PeriodicPredicate(2))
                                        .withNewlyTriggeringProcedure(
                                                () -> new FakeProcedure(2, Set.of(fm1))));
                    }
                };

        Map<String, Rule> namedRules = myRules.getRuleNameMap();
        // make sure we have 2 rules
        assertEquals(2, namedRules.size());
        // with priorities based on insertion order, starting at 0
        assertEquals(0, myRules.getPriorityForRule(namedRules.get("fm1_p0")));
        assertEquals(1, myRules.getPriorityForRule(namedRules.get("fm1_p1")));
    }

    @Test
    public void testRunNonConflictingRules() {
        // test that two non-conflicting rules (ones that would reserve the same resources)
        // are triggered in parallel and are visible to the CommandScheduler for the expected
        // lifetime.

        // create a simple RuleEngine with two non-conflicting rules
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1", new PeriodicPredicate(4))
                                        .withNewlyTriggeringProcedure(
                                                () -> new FakeProcedure(2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm2", new PeriodicPredicate(4))
                                        .withNewlyTriggeringProcedure(
                                                () -> new FakeProcedure(2, Set.of(fm2))));
                    }
                };

        // run the RuleEngine once to trigger these rules.
        myRules.run();

        // make sure the procedures (commands, as seen by the CommandScheduler) are running
        Command fm1cmd = CommandScheduler.getInstance().requiring(fm1);
        Command fm2cmd = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(fm1cmd);
        assertNotNull(fm2cmd);

        step(); // 0

        myRules.run();

        // make sure the same Commands are still running
        assertEquals(fm1cmd, CommandScheduler.getInstance().requiring(fm1));
        assertEquals(fm2cmd, CommandScheduler.getInstance().requiring(fm2));

        step(); // 1

        myRules.run();

        // make sure the same Commands are still running
        assertEquals(fm1cmd, CommandScheduler.getInstance().requiring(fm1));
        assertEquals(fm2cmd, CommandScheduler.getInstance().requiring(fm2));

        step(); // 2

        myRules.run();
        // the Commands should no longer be running
        assertNull(CommandScheduler.getInstance().requiring(fm1));
        assertNull(CommandScheduler.getInstance().requiring(fm2));
        step(); // 3
    }

    @Test
    public void testRunRulePriorities() {
        // create simple RuleEngine with two rules with conflicting actions
        // we'll check that only the higher priority rule triggers
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new PeriodicPredicate(4))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p0",
                                                                0,
                                                                Set.of(fm1, fm2))));
                        addRule(
                                Rule.create("fm1_p1", new PeriodicPredicate(2))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p1",
                                                                0,
                                                                Set.of(fm1, fm3))));

                        addRule(
                                Rule.create("fm3_p2", new PeriodicPredicate(4))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm3proc_p2", 0, Set.of(fm3))));
                    }
                };

        // run the RuleEngine once to trigger these rules
        myRules.run();

        // fm1proc_p0 should run, not fm1proc_p1, since the former is higher priority
        Command fm1cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(fm1cmd);
        assertTrue(fm1cmd.getName().endsWith("fm1proc_p0"));

        // fm3proc_p2 should also run
        Command fm3cmd = CommandScheduler.getInstance().requiring(fm3);
        assertNotNull(fm3cmd);
        assertTrue(fm3cmd.getName().endsWith("fm3proc_p2"));

        step(); // 0

        myRules.run();

        fm1cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNull(fm1cmd);

        step(); // 1

        myRules.run();

        fm1cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(fm1cmd);
        assertTrue(fm1cmd.getName().endsWith("fm1proc_p1"));

        step(); // 2
    }

    @Test
    public void testRunHigherPriorityRuleStillBeingRun() {
        // create a simple RuleEngine with two non-conflicting rules
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new PeriodicPredicate(4))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p0",
                                                                2,
                                                                Set.of(fm1, fm2))));
                        addRule(
                                Rule.create("fm1_p1", new PeriodicPredicate(4, 1))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p1",
                                                                2,
                                                                Set.of(fm1, fm2))));

                        addRule(
                                Rule.create("fm1_p2", new PeriodicPredicate(4, 3))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p2",
                                                                2,
                                                                Set.of(fm1, fm2))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1proc_p0"));

        step(); // 0

        myRules.run();

        // another rule will trigger but should be ignored, as it is trying to reserve an in-use
        // Mechanism
        // but is lower priority than the original procedure
        // the original Procedure should still be running
        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1proc_p0"));

        step(); // 1

        myRules.run();

        // original Procedure still running
        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1proc_p0"));

        step(); // 2

        myRules.run();
        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1proc_p2"));

        step(); // 3
    }

    @Test
    public void testRunLowerPriorityRuleBumped() {
        // create a simple RuleEngine with two non-conflicting rules
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new PeriodicPredicate(4, 1))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p0",
                                                                2,
                                                                Set.of(fm1, fm2))));
                        addRule(
                                Rule.create("fm1_p1", new PeriodicPredicate(4))
                                        .withNewlyTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p1",
                                                                4,
                                                                Set.of(fm1, fm2))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1proc_p1"));

        step(); // 0

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1proc_p0"));

        step(); // 1
    }
}
