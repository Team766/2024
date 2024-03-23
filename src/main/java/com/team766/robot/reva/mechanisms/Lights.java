package com.team766.robot.reva.mechanisms;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.team766.framework.Mechanism;

public class Lights extends Mechanism {

    private CANdle candle;
    private static final int CANID = 2;
    private double brightness;

    private static final int FRONT_NUM_LEDS = 40;
    private static final int FRONT_OFFSET = 8;
    private static final int FRONT_SLOT = 1;

    private static final int BACK_NUM_LEDS = 39;
    private static final int BACK_OFFSET = 48;
    private static final int BACK_SLOT = 2;

    private static final Animation TOO_HIGH_ANIMATION =
            new StrobeAnimation(255, 0, 0, 0, 0.00001, FRONT_NUM_LEDS, FRONT_OFFSET);
    private static final Animation CAN_SHOOT_ANIMATION =
            new RainbowAnimation(1, 0.1, FRONT_NUM_LEDS, false, FRONT_OFFSET);

    public Lights() {
        this(0.1);
    }

    public Lights(double brightness) {
        candle = new CANdle(CANID);
        setBrightnessNoCheck(brightness);
        clear();
    }

    public void signalTooHigh() {
        animate(TOO_HIGH_ANIMATION, FRONT_SLOT);
    }

    public void signalNoNoteInIntake() {
        setAreaColor(0, 0, 255, FRONT_NUM_LEDS, FRONT_OFFSET, FRONT_SLOT);
    }

    public void signalNoteInIntake() {
        setAreaColor(0, 255, 0, FRONT_NUM_LEDS, FRONT_OFFSET, FRONT_SLOT);
    }

    public void signalCanShoot() {
        animate(CAN_SHOOT_ANIMATION, FRONT_SLOT);
    }

    public void turnOffFront() {
        clearArea(FRONT_NUM_LEDS, FRONT_OFFSET, FRONT_SLOT);
    }

    public void signalPlayersGreen() {
        candle.clearAnimation(BACK_SLOT);
        setAreaColor(0, 255, 0, BACK_NUM_LEDS, BACK_OFFSET, BACK_SLOT);
    }

    public void signalPlayersRed() {
        setAreaColor(255, 0, 0, BACK_NUM_LEDS, BACK_OFFSET, BACK_SLOT);
    }

    public void signalPlayersNothing() {
        setAreaColor(0, 0, 0, BACK_NUM_LEDS, BACK_OFFSET, BACK_SLOT);
    }

    private void animate(Animation animation, int slot) {
        checkContextOwnership();
        candle.animate(animation, slot);
    }

    private void clearArea(int count, int offset, int slot) {
        setAreaColor(0, 0, 0, count, offset, slot);
    }

    private void setAreaColor(int r, int g, int b, int count, int offset, int slot) {
        checkContextOwnership();
        candle.clearAnimation(slot);
        candle.setLEDs(r, g, b, 0, r, count);
    }

    private void setBrightnessNoCheck(double value) {
        brightness = com.team766.math.Math.clamp(value, 0, 1);
        candle.configBrightnessScalar(brightness);
    }

    public void setBrightness(double value) {
        checkContextOwnership();
        setBrightnessNoCheck(value);
    }

    public double getBrightness() {
        return brightness;
    }

    public void changeBrightness(double change) {
        setBrightness(brightness + change);
    }

    public void clear() {
        setColor(0, 0, 0);
    }

    private void setColor(int r, int g, int b) {
        checkContextOwnership();
        candle.clearAnimation(FRONT_SLOT);
        candle.clearAnimation(BACK_SLOT);
        candle.setLEDs(r, g, b);
    }
}
