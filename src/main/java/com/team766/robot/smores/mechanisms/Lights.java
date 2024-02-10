package com.team766.robot.smores.mechanisms;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.FireAnimation;
import com.team766.framework.Mechanism;

public class Lights extends Mechanism {

    private CANdle candle;
    private static final int CANID = 5;
    private static final int LED_COUNT = 8;

    private double brightness;
    private Animation rainbowAnimation;
    private Animation fireAnimation;

    public Lights(){
        this(0.1);
    }

    public Lights(double brightness) {
        candle = new CANdle(CANID);
        setBrightness(brightness);
    }

    public void setBrightness(double brightness){
        brightness = Math.max(Math.min(brightness, 1), 0);
        this.brightness = brightness;
        rainbowAnimation = new RainbowAnimation(brightness, 0.75, LED_COUNT);
        fireAnimation = new FireAnimation(brightness, .1, LED_COUNT, 0.1, 0.1);
    }

    public double getBrightness() {
        return brightness;
    }

    public void changeBrightness(double change) {
        setBrightness(brightness + change);
    }

    private int applyBrightness(int color) {
        return (int)(brightness * color);
    }

    private int randInt(int max) {
        return (int)(Math.random() * max);
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
    }

    public void rainbow() {
        checkContextOwnership();
        candle.animate(rainbowAnimation);
    }

    public void fire() {
        checkContextOwnership();
        candle.animate(fireAnimation);
    }
}
