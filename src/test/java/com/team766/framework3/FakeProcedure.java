package com.team766.framework3;

import java.util.Set;

class FakeProcedure extends Procedure {
    private final int lifetime;
    private int age = 0;
    private boolean ended = false;

    public FakeProcedure(int lifetime, Set<Mechanism<?>> reservations) {
        this.lifetime = lifetime;

        for (Mechanism<?> m : reservations) {
            reserve(m);
        }
    }

    public int age() {
        return age;
    }

    public boolean isEnded() {
        return ended;
    }

    @Override
    public void run(Context context) {
        try {
            for (age = 0; age < lifetime; ) {
                ++age;
                context.yield();
            }
        } finally {
            ended = true;
        }
    }
}
