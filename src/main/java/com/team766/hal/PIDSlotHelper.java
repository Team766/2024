package com.team766.hal;

import com.team766.library.ObserveValue;
import com.team766.library.ValueProvider;
import edu.wpi.first.math.Pair;

public class PIDSlotHelper {
    private class Slot {
        private final int slot;
        final ObserveValue<Double> pGain;
        final ObserveValue<Double> iGain;
        final ObserveValue<Double> dGain;
        final ObserveValue<Double> ffGain;
        final ObserveValue<Double> outputMin;
        final ObserveValue<Double> outputMax;

        private Slot(int slot) {
            this.slot = slot;
            this.pGain =
                    new ObserveValue<Double>(
                            ObserveValue.whenPresent((p) -> motor.setP_Impl(p, this.slot)));
            this.iGain =
                    new ObserveValue<Double>(
                            ObserveValue.whenPresent((i) -> motor.setI_Impl(i, this.slot)));
            this.dGain =
                    new ObserveValue<Double>(
                            ObserveValue.whenPresent((d) -> motor.setD_Impl(d, this.slot)));
            this.ffGain =
                    new ObserveValue<Double>(
                            ObserveValue.whenPresent((ff) -> motor.setFF_Impl(ff, this.slot)));
            this.outputMin =
                    new ObserveValue<Double>(ObserveValue.whenPresent((__) -> updateOutputRange()));
            this.outputMax =
                    new ObserveValue<Double>(ObserveValue.whenPresent((__) -> updateOutputRange()));
        }

        private void updateOutputRange() {
            motor.setOutputRange_Impl(
                    outputMin.getValueProvider().valueOr(-1.0),
                    outputMax.getValueProvider().valueOr(1.0),
                    slot);
        }
    }

    /**
     * The methods of this interface match the semantics of the similarly-named methods in
     * {@link MotorController}, but it's important that these set_Impl methods remain separate from
     * the MotorController methods, otherwise PIDSlotHelper will not work properly.
     * All of the MotorController methods should call PIDSlotHelper (rather than updating the motor
     * device object directly) otherwise PIDSlotHelper's ObserveValues will not be updated, and they
     * will stay subscribed to the old ValueProvider.
     */
    public static interface MotorCallbacks {
        int numPIDSlots();

        void setP_Impl(double value, int slot);

        void setI_Impl(double value, int slot);

        void setD_Impl(double value, int slot);

        void setFF_Impl(double value, int slot);

        void setOutputRange_Impl(double minOutput, double maxOutput, int slot);
    }

    private final MotorCallbacks motor;
    private final Slot[] slots;

    public PIDSlotHelper(MotorCallbacks motor) {
        this.motor = motor;
        final int size = motor.numPIDSlots();
        this.slots = new Slot[size];
        for (int i = 0; i < size; ++i) {
            this.slots[i] = new Slot(i);
        }
    }

    public ValueProvider<Double> getP(int slot) {
        return slots[slot].pGain.getValueProvider();
    }

    public void setP(ValueProvider<Double> value, int slot) {
        slots[slot].pGain.setValueProvider(value);
    }

    public ValueProvider<Double> getI(int slot) {
        return slots[slot].iGain.getValueProvider();
    }

    public void setI(ValueProvider<Double> value, int slot) {
        slots[slot].iGain.setValueProvider(value);
    }

    public ValueProvider<Double> getD(int slot) {
        return slots[slot].dGain.getValueProvider();
    }

    public void setD(ValueProvider<Double> value, int slot) {
        slots[slot].dGain.setValueProvider(value);
    }

    public ValueProvider<Double> getFF(int slot) {
        return slots[slot].ffGain.getValueProvider();
    }

    public void setFF(ValueProvider<Double> value, int slot) {
        slots[slot].ffGain.setValueProvider(value);
    }

    public Pair<ValueProvider<Double>, ValueProvider<Double>> getOutputRange(int slot) {
        return new Pair<ValueProvider<Double>, ValueProvider<Double>>(
                slots[slot].outputMin.getValueProvider(), slots[slot].outputMax.getValueProvider());
    }

    public void setOutputRange(
            ValueProvider<Double> minValue, ValueProvider<Double> maxValue, int slot) {
        slots[slot].outputMin.setValueProvider(minValue);
        slots[slot].outputMax.setValueProvider(maxValue);
    }
}
