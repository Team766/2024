package com.team766.framework3;

import com.team766.hal.JoystickReader;
import java.util.function.BooleanSupplier;

public class Conditions {

    public static class And implements BooleanSupplier {
        private final BooleanSupplier[] preds;

        public And(BooleanSupplier... preds) {
            this.preds = preds;
        }

        @Override
        public boolean getAsBoolean() {
            boolean returnValue = true;
            for (BooleanSupplier pred : preds) {
                returnValue &= pred.getAsBoolean();
                if (!returnValue) {
                    break;
                }
            }
            return returnValue;
        }
    }

    public static class Or implements BooleanSupplier {
        private final BooleanSupplier[] preds;

        public Or(BooleanSupplier... preds) {
            this.preds = preds;
        }

        @Override
        public boolean getAsBoolean() {
            boolean returnValue = false;
            for (BooleanSupplier pred : preds) {
                returnValue |= pred.getAsBoolean();
                if (returnValue) {
                    break;
                }
            }
            return returnValue;
        }
    }

    public static class StatusCheck<S extends Status> implements BooleanSupplier {
        public interface Checker<S> {
            boolean check(S status);
        }

        private final Class<S> clazz;
        private final Checker<S> checker;

        public StatusCheck(Class<S> clazz, Checker<S> checker) {
            this.clazz = clazz;
            this.checker = checker;
        }

        public boolean getAsBoolean() {
            S status = StatusBus.getInstance().getStatus(clazz);
            return checker.check(status);
        }
    }

    public static class AwaitRequest<S extends Status> extends StatusCheck<S> {

        public AwaitRequest(Class<S> clazz, Request<S> request) {
            super(clazz, request::isDone);
        }
    }

    // TODO: move this to a more suitable location
    public static class JoystickAxisWithDeadzone {
        private final JoystickReader joystick;
        private final int axis;
        private final double deadzone;

        public JoystickAxisWithDeadzone(JoystickReader joystick, int axis, double deadzone) {
            this.joystick = joystick;
            this.axis = axis;
            this.deadzone = deadzone;
        }

        public double getAxis() {
            double rawValue = joystick.getAxis(axis);
            return (rawValue > deadzone) ? rawValue : 0.0;
        }
    }

    public static class JoystickMoved implements BooleanSupplier {
        private JoystickAxisWithDeadzone axis;

        public JoystickMoved(JoystickAxisWithDeadzone axis) {
            this.axis = axis;
        }

        @Override
        public boolean getAsBoolean() {
            return axis.getAxis() > 0.0;
        }
    }

    private Conditions() {}
}
