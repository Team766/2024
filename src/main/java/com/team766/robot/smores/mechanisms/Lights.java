package com.team766.robot.smores.mechanisms;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.SingleFadeAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.team766.framework.Mechanism;

public class Lights extends Mechanism {

    private static final int INTAKE_NUM_LEDS = 40;
    private static final int INTAKE_OFFSET = 8;
    private static final int INTAKE_SLOT = 1;

    public enum IntakeAnimation { // which animation to use for the note
        NOT_VISIBLE(0, 0, 0),
        VISIBLE(0, 0, 25),
        STUCK(new StrobeAnimation(25, 0, 0, 0, 0.00001, INTAKE_NUM_LEDS, INTAKE_OFFSET)),
        READY(0, 25, 0);

        private Animation animation;
        private int r;
        private int g;
        private int b;
        private boolean isColor;

        IntakeAnimation(Animation animation) {
            this.animation = animation;
            isColor = false;
        }

        IntakeAnimation(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            isColor = true;
        }

        private void playAnimation(CANdle candle) {
            if (isColor) {
                candle.clearAnimation(INTAKE_SLOT);
                candle.setLEDs(r, g, b, 0, INTAKE_OFFSET, INTAKE_NUM_LEDS);
            } else {
                candle.animate(animation, INTAKE_SLOT);
            }
        }
    }

    private IntakeAnimation currentIntakeAnimation = IntakeAnimation.NOT_VISIBLE;
    private CANdle candle;
    private static final int CANID = 5;
    private static final int LED_COUNT = 87;

    private double brightness;
    private Animation rainbowAnimation;
    private Animation fireAnimation;
    private Runnable lastRun;

    public Lights() {
        this(0.1);
    }

    public Lights(double brightness) {
        candle = new CANdle(CANID);
        this.clear();
        setBrightness(brightness);
        currentIntakeAnimation.playAnimation(candle);
        setCanShoot(false);
    }

    public void setIntakeAnimation(IntakeAnimation animation) {
        if (animation == currentIntakeAnimation) {
            return;
        }
        currentIntakeAnimation = animation;
        animation.playAnimation(candle);
    }

    public IntakeAnimation getIntakeAnimation() {
        return currentIntakeAnimation;
    }

    private static final int SHOOTER_NUM_LEDS = 39;
    private static final int SHOOTER_OFFSET = 48;
    private static final int SHOOTER_SLOT = 2;
    private static final Animation canShootAnimation =
            new RainbowAnimation(0.1, 0.1, SHOOTER_NUM_LEDS, false, SHOOTER_OFFSET);

    public void setCanShoot(boolean canShoot) {
        if (canShoot) {
            candle.animate(canShootAnimation, SHOOTER_SLOT);
        } else {
            candle.clearAnimation(SHOOTER_SLOT);
            candle.setLEDs(0, 0, 0, 0, SHOOTER_OFFSET, SHOOTER_NUM_LEDS);
        }
    }

    public void setBrightness(double value) {
        brightness = Math.max(Math.min(value, 1), 0);
        rainbowAnimation = new RainbowAnimation(brightness, 0.75, LED_COUNT);
        fireAnimation = new FireAnimation(brightness, .1, LED_COUNT, 0.1, 0.1);
        lastRun.run();
    }

    public double getBrightness() {
        return brightness;
    }

    public void changeBrightness(double change) {
        setBrightness(brightness + change);
    }

    private int applyBrightness(int color) {
        return (int) (brightness * color);
    }

    private int randInt(int max) {
        return (int) (Math.random() * max);
    }

    private int randInt() {
        return randInt(256);
    }

    public void randColor() {
        setColor(randInt(), randInt(), randInt());
    }

    public void clear() {
        setColor(0, 0, 0);
    }

    public void setColor(int r, int g, int b) {
        checkContextOwnership();
        candle.clearAnimation(0);
        candle.setLEDs(applyBrightness(r), applyBrightness(g), applyBrightness(b));
        lastRun = () -> setColor(r, g, b);
    }

    public void animate(Animation animation) {
        checkContextOwnership();
        candle.animate(animation);
    }

    public void rainbow() {
        lastRun = () -> animate(rainbowAnimation);
        lastRun.run();
    }

    public void fire() {
        lastRun = () -> animate(fireAnimation);
        lastRun.run();
    }

    public void strobe(int r, int g, int b) {
        animate(
                new StrobeAnimation(
                        applyBrightness(r),
                        applyBrightness(g),
                        applyBrightness(b),
                        // white does nothing for our lights
                        0,
                        // 0 slow -> 1 fast
                        0.00001,
                        LED_COUNT));
        lastRun = () -> strobe(r, g, b);
    }

    public void fade(int r, int g, int b) {
        animate(
                new SingleFadeAnimation(
                        applyBrightness(r),
                        applyBrightness(g),
                        applyBrightness(b),
                        0,
                        0.75,
                        LED_COUNT));
        lastRun = () -> fade(r, g, b);
    }
}
