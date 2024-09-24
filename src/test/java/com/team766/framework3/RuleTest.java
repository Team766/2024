package com.team766.framework3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.team766.framework3.Rule.TriggerType;
import java.util.Collections;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RuleTest {

    // boolean sequence: true true false false true true false false ...
    // allows for testing sequence of trigger states: NONE->NEWLY->CONTINUING->FINISHED->NONE ...
    private static class DuckDuckGooseGoosePredicate implements BooleanSupplier {
        private int count = 0;

        @Override
        public boolean getAsBoolean() {
            int mod = count % 4;
            boolean value = (mod == 0 || mod == 1);
            ++count;
            return value;
        }
    }

    private static class TrivialProcedure extends Procedure {
        public TrivialProcedure(String name) {
            super(name, Collections.emptySet());
        }

        @Override
        public void run(Context context) {}
    }

    @BeforeEach
    protected void setUp() {}

    @Test
    public void testCreate() {
        Rule alwaysTrue =
                Rule.create("always true", () -> true)
                        .withNewlyTriggeringProcedure(() -> Procedure.NO_OP)
                        .build();
        assertNotNull(alwaysTrue);
        assertEquals("always true", alwaysTrue.getName());
    }

    @Test
    public void testEvaluate() {
        // start with simple test of a NONE->NEWLY->CONTINUING->CONTINUING sequence
        Rule alwaysTrue =
                Rule.create("always true", () -> true)
                        .withNewlyTriggeringProcedure(() -> Procedure.NO_OP)
                        .build();
        assertEquals(Rule.TriggerType.NONE, alwaysTrue.getCurrentTriggerType());
        alwaysTrue.evaluate();
        assertEquals(TriggerType.NEWLY, alwaysTrue.getCurrentTriggerType());
        alwaysTrue.evaluate();
        assertEquals(TriggerType.CONTINUING, alwaysTrue.getCurrentTriggerType());
        alwaysTrue.evaluate();
        assertEquals(TriggerType.CONTINUING, alwaysTrue.getCurrentTriggerType());

        // test a full cycle: NONE->NEWLY->CONTINUING->FINISHED->NONE->NEWLY->...
        Rule duckDuckGooseGoose =
                Rule.create("duck duck goose goose", new DuckDuckGooseGoosePredicate())
                        .withNewlyTriggeringProcedure(() -> Procedure.NO_OP)
                        .build();
        assertEquals(Rule.TriggerType.NONE, duckDuckGooseGoose.getCurrentTriggerType());
        duckDuckGooseGoose.evaluate();
        assertEquals(TriggerType.NEWLY, duckDuckGooseGoose.getCurrentTriggerType());
        duckDuckGooseGoose.evaluate();
        assertEquals(TriggerType.CONTINUING, duckDuckGooseGoose.getCurrentTriggerType());
        duckDuckGooseGoose.evaluate();
        assertEquals(TriggerType.FINISHED, duckDuckGooseGoose.getCurrentTriggerType());
        duckDuckGooseGoose.evaluate();
        assertEquals(TriggerType.NONE, duckDuckGooseGoose.getCurrentTriggerType());
        duckDuckGooseGoose.evaluate();
        assertEquals(TriggerType.NEWLY, duckDuckGooseGoose.getCurrentTriggerType());
    }

    @Test
    public void testGetMechanismsToReserve() {
        final Set<Mechanism<?>> newlyMechanisms =
                Set.of(new FakeMechanism1(), new FakeMechanism2());
        final Set<Mechanism<?>> continuingMechanisms = Set.of(new FakeMechanism3());
        final Set<Mechanism<?>> finishedMechanisms = Set.of(new FakeMechanism());

        Rule duckDuckGooseGoose =
                Rule.create("duck duck goose goose", new DuckDuckGooseGoosePredicate())
                        .withNewlyTriggeringProcedure(newlyMechanisms, () -> {})
                        .withContinuingTriggeringProcedure(continuingMechanisms, () -> {})
                        .withFinishedTriggeringProcedure(finishedMechanisms, () -> {})
                        .build();

        // NONE
        assertEquals(Collections.emptySet(), duckDuckGooseGoose.getMechanismsToReserve());

        // NEWLY
        duckDuckGooseGoose.evaluate();
        assertEquals(newlyMechanisms, duckDuckGooseGoose.getMechanismsToReserve());

        // CONTINUING
        duckDuckGooseGoose.evaluate();
        assertEquals(continuingMechanisms, duckDuckGooseGoose.getMechanismsToReserve());

        // FINISHED
        duckDuckGooseGoose.evaluate();
        assertEquals(finishedMechanisms, duckDuckGooseGoose.getMechanismsToReserve());

        // check NONE again
        duckDuckGooseGoose.evaluate();
        assertEquals(Collections.emptySet(), duckDuckGooseGoose.getMechanismsToReserve());

        // check newly again
        duckDuckGooseGoose.evaluate();
        assertEquals(newlyMechanisms, duckDuckGooseGoose.getMechanismsToReserve());
    }

    @Test
    public void testGetProcedureToRun() {
        Rule duckDuckGooseGoose =
                Rule.create("duck duck goose goose", new DuckDuckGooseGoosePredicate())
                        .withNewlyTriggeringProcedure(() -> new TrivialProcedure("newly"))
                        .withContinuingTriggeringProcedure(() -> new TrivialProcedure("continuing"))
                        .withFinishedTriggeringProcedure(() -> new TrivialProcedure("finished"))
                        .build();

        // NONE
        assertNull(duckDuckGooseGoose.getProcedureToRun());

        // NEWLY
        duckDuckGooseGoose.evaluate();
        assertEquals("newly", duckDuckGooseGoose.getProcedureToRun().getName());

        // CONTINUING
        duckDuckGooseGoose.evaluate();
        assertEquals("continuing", duckDuckGooseGoose.getProcedureToRun().getName());

        // FINISHED
        duckDuckGooseGoose.evaluate();
        assertEquals("finished", duckDuckGooseGoose.getProcedureToRun().getName());

        // check NONE again
        duckDuckGooseGoose.evaluate();
        assertNull(duckDuckGooseGoose.getProcedureToRun());

        // check newly again
        duckDuckGooseGoose.evaluate();
        assertEquals("newly", duckDuckGooseGoose.getProcedureToRun().getName());
    }
}
