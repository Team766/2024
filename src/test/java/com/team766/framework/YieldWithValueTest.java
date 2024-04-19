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
                    "lastYieldedValue should be null before the procedure yields a value",
                    generator.lastYieldedValue());

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
        @Override
        public void run(ContextWithValue<Integer> context) {
            for (int i = 0; i <= 10; ++i) {
                context.yield(i);
            }
        }
    }

    @Test
    public void testYieldWithValue() {
        var consumer = new ValueConsumer();
        Scheduler.getInstance().startAsync(consumer);

        for (int i = 0; i < 50; ++i) {
            step();
        }

        assertEquals(consumer.values, List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    }
}
