package com.team766.library;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import org.junit.jupiter.api.Test;

public class LossyPriorityQueueTest {
    @Test
    public void test() throws InterruptedException {
        final int N = 100;
        var queue = new LossyPriorityQueue<Integer>(N, Comparator.naturalOrder());
        var producerThread =
                new Thread(
                        () -> {
                            for (Integer i = 0; i < N; ++i) {
                                queue.add(i);
                            }
                        });
        producerThread.start();
        for (Integer i = 0; i < N; ++i) {
            assertEquals(i, queue.poll());
        }
        producerThread.join();
    }
}
