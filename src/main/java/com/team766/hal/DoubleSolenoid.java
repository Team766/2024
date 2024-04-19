package com.team766.hal;

public class DoubleSolenoid implements SolenoidController {

    private SolenoidController forward;
    private SolenoidController back;
    private boolean boolState;

    public enum State {
        Forward,
        Neutral,
        Backward
    }

    public DoubleSolenoid(final SolenoidController forward_, final SolenoidController back_) {
        this.forward = forward_;
        this.back = back_;

        set(State.Neutral);
    }

    @Override
    public boolean get() {
        return boolState;
    }

    public void set(final State state) {
        switch (state) {
            case Forward:
                boolState = true;
                if (forward != null) {
                    forward.set(true);
                }
                if (back != null) {
                    back.set(false);
                }
                break;
            case Backward:
                boolState = false;
                if (forward != null) {
                    forward.set(false);
                }
                if (back != null) {
                    back.set(true);
                }
                break;
            case Neutral:
                boolState = false;
                if (forward != null) {
                    forward.set(false);
                }
                if (back != null) {
                    back.set(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void set(final boolean on) {
        set(on ? State.Forward : State.Backward);
    }
}
