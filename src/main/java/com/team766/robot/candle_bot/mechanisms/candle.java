package com.team766.robot.candle_bot.mechanisms;
import com.ctre.phoenix.led.CANdle;
import com.team766.framework.Mechanism;


public class candle extends Mechanism {
    private CANdle candle;
    public candle() {
        candle = new CANdle(0);

    }
    public void LED(){
        candle.setLEDs(179, 105, 2);
    }

    public void stop(){
        candle.setLEDs(0,0,0);
    }


}