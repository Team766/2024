package com.team766.framework;

import static com.team766.framework.RulePersistence.ONCE;
import static com.team766.framework.RulePersistence.ONCE_AND_HOLD;
import static com.team766.framework.RulePersistence.REPEATEDLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team766.TestCase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;

public class RuleEngineTest extends TestCase {

    private static class ScheduledPredicate implements BooleanSupplier {
        private final int start;
        private final int end;

        private int currentCycle = 0;

        public ScheduledPredicate(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public ScheduledPredicate(int start) {
            this(start, start + 1);
        }

        @Override
        public boolean getAsBoolean() {
            boolean value = currentCycle >= start && currentCycle < end;
            ++currentCycle;
            return value;
        }
    }

    private static class PeriodicPredicate implements BooleanSupplier {
        private final int period;
        private int currentCycle = 0;

        public PeriodicPredicate(int period) {
            this.period = period;
        }

        @Override
        public boolean getAsBoolean() {
            boolean value = currentCycle % period == 0;
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
                                Rule.create("fm1_p0", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE, () -> new FakeProcedure(2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE, () -> new FakeProcedure(2, Set.of(fm1))));
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
                                Rule.create("fm1", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE, () -> new FakeProcedure(2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm2", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE, () -> new FakeProcedure(2, Set.of(fm2))));
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
    public void testFinishedProcedureBumpsNewlyProcedureForSameRule() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 1, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p0",
                                                                1,
                                                                Set.of(fm1, fm2))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));

        step(); // 0

        // next iteration - check that the original procedure is new bumped by the finished
        // procedure for the same rule
        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procfin_p0"));

        step(); // 1
    }

    @Test
    public void testRunRulePriorities() {
        // create simple RuleEngine with two rules with conflicting actions
        // we'll check that only the higher priority rule triggers
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p0",
                                                                0,
                                                                Set.of(fm1, fm2))));
                        addRule(
                                Rule.create("fm1_p1", new PeriodicPredicate(2))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p1",
                                                                0,
                                                                Set.of(fm1, fm3))));

                        addRule(
                                Rule.create("fm3_p2", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
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
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p0",
                                                                2,
                                                                Set.of(fm1, fm2))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p1",
                                                                2,
                                                                Set.of(fm1, fm2))));

                        addRule(
                                Rule.create("fm1_p2", new ScheduledPredicate(3))
                                        .withOnTriggeringProcedure(
                                                ONCE,
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
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1proc_p0",
                                                                2,
                                                                Set.of(fm1, fm2))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
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

    @Test
    public void testRuleResetIgnoredLowerPriorityRule() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p1", 1, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p1", 1, Set.of(fm2))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));

        step(); // 0

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd);

        step();
    }

    @Test
    public void testRuleResetIgnoredLowerPriorityRuleHigherPriorityRulePreviouslyScheduled() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p1", 1, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p1", 1, Set.of(fm2))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));

        step(); // 0

        // next iteration - even with the second rule firing, the procedure from the first rule
        // should continue
        // executing, since the first rule is higher priority
        myRules.run();
        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));
        step(); // 1

        // next iteration - the second rule's finished procedure should *not* execute
        myRules.run();
        cmd = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd);
        step(); // 2
    }

    @Test
    public void testRuleResetBumpedLowerPriorityRule() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 2, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(0))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p1", 2, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p1", 2, Set.of(fm2))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p1"));

        step(); // 0

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd);

        step();
    }

    @Test
    public void testLowerPriorityRuleRunsWhenProcedureFromHigherPriorityRuleFinishes() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(0, 4))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 0, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p0", 0, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p1", 0, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p1", 0, Set.of(fm1))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));

        step(); // 0

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p1"));

        step(); // 1

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procfin_p1"));

        step(); // 2

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNull(cmd);

        step(); // 3

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procfin_p0"));

        step(); // 4
    }

    @Test
    public void testRuleCalledAgainAfterBeingReset() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 0, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p0", 0, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(0, 4))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p1", 1, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p1", 1, Set.of(fm1))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p1"));

        step(); // 0

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));

        step(); // 1

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procfin_p0"));

        step(); // 2

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p1"));

        step(); // 3

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procfin_p1"));

        step(); // 4
    }

    @Test
    public void testRuleResetPreventsFinishedForLongTrigger() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("fm1_p0", new ScheduledPredicate(1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p0", 0, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p0", 0, Set.of(fm1))));
                        addRule(
                                Rule.create("fm1_p1", new ScheduledPredicate(0, 3))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procnew_p1", 1, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "fm1procfin_p1", 1, Set.of(fm1))));
                    }
                };

        myRules.run();

        // check that the expected Procedure is scheduled
        Command cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p1"));

        step(); // 0

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procnew_p0"));

        step(); // 1

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd);
        assertTrue(cmd.getName().endsWith("fm1procfin_p0"));

        step(); // 2

        myRules.run();

        cmd = CommandScheduler.getInstance().requiring(fm1);
        assertNull(cmd);

        step(); // 3
    }

    /** Test ONCE RulePersistence policy */
    @Test
    public void testOncePersistence() {
        AtomicReference<FakeProcedure> predicateEndsFirstProc = new AtomicReference<>();
        AtomicReference<FakeProcedure> actionEndsFirstProc = new AtomicReference<>();
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("predicate_ends_first", new ScheduledPredicate(0, 1))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () -> {
                                                    var proc =
                                                            new FakeProcedure(
                                                                    "predicate_ends_first_proc",
                                                                    10,
                                                                    Set.of(fm1));
                                                    predicateEndsFirstProc.set(proc);
                                                    return proc;
                                                }));
                        addRule(
                                Rule.create("action_ends_first", new ScheduledPredicate(0, 10))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () -> {
                                                    var proc =
                                                            new FakeProcedure(
                                                                    "action_ends_first_proc",
                                                                    1,
                                                                    Set.of(fm2));
                                                    actionEndsFirstProc.set(proc);
                                                    return proc;
                                                }));
                    }
                };

        myRules.run();

        // check that both action Procedures are scheduled
        Command cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("predicate_ends_first_proc"));
        Command cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("action_ends_first_proc"));

        step();
        myRules.run();
        step();
        myRules.run();

        // ONCE actions should be allowed to run after the rule has stopped triggering.
        assertEquals(2, predicateEndsFirstProc.get().age());
        assertFalse(predicateEndsFirstProc.get().isEnded());
        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("predicate_ends_first_proc"));

        // If a ONCE action completes, it should end and mechanism reservations released.
        assertTrue(actionEndsFirstProc.get().isEnded());
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd2);
    }

    /** Test ONCE_AND_HOLD RulePersistence policy */
    @Test
    public void testOnceAndHoldPersistence() {
        AtomicReference<FakeProcedure> predicateEndsFirstProc = new AtomicReference<>();
        AtomicReference<FakeProcedure> actionEndsFirstProc = new AtomicReference<>();
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("predicate_ends_first", new ScheduledPredicate(0, 1))
                                        .withOnTriggeringProcedure(
                                                ONCE_AND_HOLD,
                                                () -> {
                                                    var proc =
                                                            new FakeProcedure(
                                                                    "predicate_ends_first_proc",
                                                                    10,
                                                                    Set.of(fm1));
                                                    predicateEndsFirstProc.set(proc);
                                                    return proc;
                                                }));
                        addRule(
                                Rule.create("action_ends_first", new ScheduledPredicate(0, 10))
                                        .withOnTriggeringProcedure(
                                                ONCE_AND_HOLD,
                                                () -> {
                                                    var proc =
                                                            new FakeProcedure(
                                                                    "action_ends_first_proc",
                                                                    1,
                                                                    Set.of(fm2));
                                                    actionEndsFirstProc.set(proc);
                                                    return proc;
                                                }));
                    }
                };

        myRules.run();

        // check that both action Procedures are scheduled
        Command cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("predicate_ends_first_proc"));
        Command cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("action_ends_first_proc"));

        step();
        myRules.run();
        step();
        myRules.run();

        // ONCE_AND_HOLD actions should be cancelled after the rule has stopped triggering.
        assertTrue(predicateEndsFirstProc.get().isEnded());
        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNull(cmd1);

        // If a ONCE_AND_HOLD action completes, it should end but mechanism reservations are
        // retained.
        assertTrue(actionEndsFirstProc.get().isEnded());
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("action_ends_first_proc"));
    }

    /** Test REPEATEDLY RulePersistence policy */
    @Test
    public void testRepeatedlyPersistence() {
        AtomicReference<FakeProcedure> predicateEndsFirstProc = new AtomicReference<>();
        AtomicReference<FakeProcedure> actionEndsFirstProc = new AtomicReference<>();
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("predicate_ends_first", new ScheduledPredicate(0, 1))
                                        .withOnTriggeringProcedure(
                                                REPEATEDLY,
                                                () -> {
                                                    var proc =
                                                            new FakeProcedure(
                                                                    "predicate_ends_first_proc",
                                                                    10,
                                                                    Set.of(fm1));
                                                    predicateEndsFirstProc.set(proc);
                                                    return proc;
                                                }));
                        addRule(
                                Rule.create("action_ends_first", new ScheduledPredicate(0, 10))
                                        .withOnTriggeringProcedure(
                                                REPEATEDLY,
                                                () -> {
                                                    var proc =
                                                            new FakeProcedure(
                                                                    "action_ends_first_proc",
                                                                    1,
                                                                    Set.of(fm2));
                                                    actionEndsFirstProc.set(proc);
                                                    return proc;
                                                }));
                    }
                };

        myRules.run();

        // check that both action Procedures are scheduled
        Command cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("predicate_ends_first_proc"));
        Command cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("action_ends_first_proc"));

        step();
        myRules.run();
        step();
        myRules.run();
        step();

        // REPEATEDLY actions should be cancelled after the rule has stopped triggering.
        assertTrue(predicateEndsFirstProc.get().isEnded());
        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNull(cmd1);

        // If a REPEATEDLY action completes, another instance should be started.
        assertFalse(actionEndsFirstProc.get().isEnded());
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("action_ends_first_proc"));

        final FakeProcedure previousActionInstance = actionEndsFirstProc.get();

        myRules.run();
        step();
        myRules.run();
        step();

        assertTrue(previousActionInstance.isEnded());
        assertFalse(actionEndsFirstProc.get().isEnded());
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("action_ends_first_proc"));
    }

    /** Test hierarchical Rules triggering */
    @Test
    public void testRuleHierarchy() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("root", new ScheduledPredicate(0, 2))
                                        .withOnTriggeringProcedure(
                                                ONCE_AND_HOLD,
                                                () ->
                                                        new FakeProcedure(
                                                                "root_proc", 10, Set.of(fm1)))
                                        .whenTriggering(
                                                Rule.create(
                                                                "positive_combinator",
                                                                new ScheduledPredicate(1, 3))
                                                        .withOnTriggeringProcedure(
                                                                ONCE_AND_HOLD,
                                                                () ->
                                                                        new FakeProcedure(
                                                                                "positive_combinator_proc",
                                                                                10,
                                                                                Set.of(fm2))))
                                        .whenNotTriggering(
                                                Rule.create(
                                                                "negative_combinator",
                                                                // Note: This predicate is only
                                                                // evaluated when the `root` rule is
                                                                // not triggering, so this triggers
                                                                // on frame 2, even though its
                                                                // start/end arguments say it
                                                                // triggers on frame 0.
                                                                new ScheduledPredicate(0, 1))
                                                        .withOnTriggeringProcedure(
                                                                ONCE_AND_HOLD,
                                                                () ->
                                                                        new FakeProcedure(
                                                                                "negative_combinator_proc",
                                                                                10,
                                                                                Set.of(fm3)))));
                    }
                };

        myRules.run();

        Command cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("root_proc"));
        Command cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd2);
        Command cmd3 = CommandScheduler.getInstance().requiring(fm3);
        assertNull(cmd3);

        step();
        myRules.run();

        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("root_proc"));
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNotNull(cmd2);
        assertTrue(cmd2.getName().endsWith("positive_combinator_proc"));
        cmd3 = CommandScheduler.getInstance().requiring(fm3);
        assertNull(cmd3);

        step();
        myRules.run();

        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNull(cmd1);
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd2);
        cmd3 = CommandScheduler.getInstance().requiring(fm3);
        assertNotNull(cmd3);
        assertTrue(cmd3.getName().endsWith("negative_combinator_proc"));

        step();
        myRules.run();

        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNull(cmd1);
        cmd2 = CommandScheduler.getInstance().requiring(fm2);
        assertNull(cmd2);
        cmd3 = CommandScheduler.getInstance().requiring(fm3);
        assertNull(cmd3);
    }

    /** Test that the root Rule takes precedence over child rules triggering */
    @Test
    public void testRuleHierarchyPriorities() {
        RuleEngine myRules =
                new RuleEngine() {
                    {
                        addRule(
                                Rule.create("root", new ScheduledPredicate(0, 2))
                                        .withOnTriggeringProcedure(
                                                ONCE,
                                                () ->
                                                        new FakeProcedure(
                                                                "root_newly_proc", 0, Set.of(fm1)))
                                        .withFinishedTriggeringProcedure(
                                                () ->
                                                        new FakeProcedure(
                                                                "root_finished_proc",
                                                                0,
                                                                Set.of(fm1)))
                                        .whenTriggering(
                                                Rule.create(
                                                                "positive_combinator",
                                                                new ScheduledPredicate(0, 2))
                                                        .withOnTriggeringProcedure(
                                                                ONCE_AND_HOLD,
                                                                () ->
                                                                        new FakeProcedure(
                                                                                "positive_combinator_proc",
                                                                                10,
                                                                                Set.of(fm1))))
                                        .whenNotTriggering(
                                                Rule.create(
                                                                "negative_combinator",
                                                                // Note: This predicate is only
                                                                // evaluated when the `root` rule is
                                                                // not triggering, so this triggers
                                                                // on frames 2-3, even though its
                                                                // start/end arguments say it
                                                                // triggers on frame 0-1.
                                                                new ScheduledPredicate(0, 2))
                                                        .withOnTriggeringProcedure(
                                                                ONCE_AND_HOLD,
                                                                () ->
                                                                        new FakeProcedure(
                                                                                "negative_combinator_proc",
                                                                                10,
                                                                                Set.of(fm1)))));
                    }
                };

        myRules.run();

        Command cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("root_newly_proc"));

        step();
        myRules.run();

        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("positive_combinator_proc"));

        step();
        myRules.run();

        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("root_finished_proc"));

        step();
        myRules.run();

        cmd1 = CommandScheduler.getInstance().requiring(fm1);
        assertNotNull(cmd1);
        assertTrue(cmd1.getName().endsWith("negative_combinator_proc"));
    }
}
