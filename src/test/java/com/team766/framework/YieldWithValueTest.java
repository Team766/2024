package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class YieldWithValueTest extends TestCase {
    private static class ValueConsumer extends Procedure {
        public final ArrayList<Integer> values = new ArrayList<>();

        @Override
        public void run(Context context) {
            var generator = context.startAsync(new ValueGenerator());

            assertNull(
                    generator.lastYieldedValue(),
                    "lastYieldedValue should be null before the procedure yields a value");

            while (generator.lastYieldedValue() == null || generator.lastYieldedValue() < 10) {
                var value = generator.lastYieldedValue();
                if (value != null) {
                    values.add(value);
                }
                context.yield();
            }
        }
    }

    private static class ValueGenerator extends ProcedureWithValue<Integer> {
        public int nextToYield = 0;

        @Override
        public void run(ContextWithValue<Integer> context) {
            for (; nextToYield <= 10; ++nextToYield) {
                context.yield(nextToYield);
            }
        }
    }

    @Test
    public void testYieldWithValue() {
        var consumer = new ValueConsumer();
        SchedulerUtils.startAsync(consumer);

        for (int i = 0; i < 50; ++i) {
            step();
        }

        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), consumer.values);
    }

    private static class DiscardingCaller extends Procedure {
        private ValueGenerator generator = new ValueGenerator();

        public int stepCount() {
            return generator.nextToYield;
        }

        @Override
        public void run(Context context) {
            context.runSync(generator);
        }
    }

    @Test
    public void testDiscardYieldedValues() {
        var caller = new DiscardingCaller();
        SchedulerUtils.startAsync(caller);

        for (int i = 0; i < 50; ++i) {
            step();
        }

        assertEquals(11, caller.stepCount());
    }

    private static class CollectingCaller extends Procedure {
        public List<Integer> values;

        @Override
        public void run(Context context) {
            values = context.runSyncAndCollectValues(new ValueGenerator());
        }
    }

    @Test
    public void testCollectYieldedValues() {
        var consumer = new CollectingCaller();
        SchedulerUtils.startAsync(consumer);

        for (int i = 0; i < 50; ++i) {
            step();
        }

        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), consumer.values);
    }
}
