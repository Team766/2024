package com.team766.hal;

import com.team766.library.ValueProvider;
import edu.wpi.first.math.Pair;
import java.util.ArrayList;

public class PIDSlotHelper {
    private final ArrayList<ValueProvider<Double>> pGains;
    private final ArrayList<ValueProvider<Double>> iGains;
    private final ArrayList<ValueProvider<Double>> dGains;
    private final ArrayList<ValueProvider<Double>> ffGains;
    private final ArrayList<Pair<ValueProvider<Double>, ValueProvider<Double>>> outputMaxes;

    public PIDSlotHelper(int numSlots) {
        int num = numSlots;
        pGains = new ArrayList<ValueProvider<Double>>(num);
        iGains = new ArrayList<ValueProvider<Double>>(num);
        dGains = new ArrayList<ValueProvider<Double>>(num);
        ffGains = new ArrayList<ValueProvider<Double>>(num);
        outputMaxes = new ArrayList<Pair<ValueProvider<Double>, ValueProvider<Double>>>(num);
    }

    public ValueProvider<Double> getP(int slot) {
        return pGains.get(slot);
    }

    public void setP(ValueProvider<Double> value, int slot) {
        pGains.set(slot, value);
    }

    public ValueProvider<Double> getI(int slot) {
        return iGains.get(slot);
    }

    public void setI(ValueProvider<Double> value, int slot) {
        iGains.set(slot, value);
    }

    public ValueProvider<Double> getD(int slot) {
        return dGains.get(slot);
    }

    public void setD(ValueProvider<Double> value, int slot) {
        dGains.set(slot, value);
    }

    public ValueProvider<Double> getFF(int slot) {
        return ffGains.get(slot);
    }

    public void setFF(ValueProvider<Double> value, int slot) {
        ffGains.set(slot, value);
    }

    public Pair<ValueProvider<Double>, ValueProvider<Double>> getOutputRange(int slot) {
        return outputMaxes.get(slot);
    }

    public void setOutputRange(
            ValueProvider<Double> minValue, ValueProvider<Double> maxValue, int slot) {
        outputMaxes.set(
                slot, new Pair<ValueProvider<Double>, ValueProvider<Double>>(maxValue, maxValue));
    }

    public void refreshPIDForSlot(MotorController motor, int slot) {
        if ((pGains.get(slot) != null) && pGains.get(slot).hasValue())
            motor.setP(pGains.get(slot).get(), slot);
        if ((iGains.get(slot) != null) && iGains.get(slot).hasValue())
            motor.setI(pGains.get(slot).get(), slot);
        if ((dGains.get(slot) != null) && dGains.get(slot).hasValue())
            motor.setD(pGains.get(slot).get(), slot);
        if ((ffGains.get(slot) != null) && ffGains.get(slot).hasValue())
            motor.setFF(pGains.get(slot).get(), slot);
        if (outputMaxes.get(slot) != null
                && (outputMaxes.get(slot).getFirst() != null)
                && (outputMaxes.get(slot).getSecond() != null)
                && outputMaxes.get(slot).getFirst().hasValue()
                && outputMaxes.get(slot).getSecond().hasValue()) {
            motor.setOutputRange(
                    outputMaxes.get(slot).getFirst().get(),
                    outputMaxes.get(slot).getSecond().get(),
                    slot);
        }
    }
}
