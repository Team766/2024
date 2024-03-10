package com.team766.robot.smores.mechanisms;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.SingleFadeAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.team766.framework.Mechanism;

public class Lights extends Mechanism {

    private static final int intakeLEDs = 50;
    private static final int intakeOffset = 8;
    public enum Intake { // which animation to use for the note
        NOT_VISIBLE (),
        VISIBLE (),
        STUCK (new StrobeAnimation(25, 0, 0, 0, 0.)),
        READY ();

        Intake(Animation animation) {

        }
    } 

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

