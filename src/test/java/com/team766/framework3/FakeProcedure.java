package com.team766.framework3;

import java.util.Set;

class FakeProcedure extends Procedure {
    private final int lifetime;
    private int age = 0;
    private boolean ended = false;

    public FakeProcedure(int lifetime, Set<Mechanism<?>> reservations) {
        super(reservations);
        this.lifetime = lifetime;
    }

    public FakeProcedure(String name, int lifetime, Set<Mechanism<?>> reservations) {
        super(name, reservations);
        this.lifetime = lifetime;
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
