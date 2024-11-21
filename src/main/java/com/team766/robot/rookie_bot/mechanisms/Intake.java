package com.team766.robot.rookie_bot.mechanisms;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class  Intake extends Mechanism  {

    public enum State {
        IN,
        OUT,
        STOP
    }

    public MotorController intakeWheelLeft;
    public MotorController intakeWheelRight;
    double Power= -0.35;
public Intake(){
    intakeWheelLeft = RobotProvider.instance.getMotor("intakeWheelLeft");
    intakeWheelRight = RobotProvider.instance.getMotor("intakeWheelRight");
}
public void intake() {
    
        intakeWheelLeft.set(Power);
        intakeWheelRight.set(Power);
    }
    public void inout(State state){

        double normal=0;;
        switch (state) {
            case IN:
                
                normal=Power;
            break;
            case OUT:
                normal=Power*-1;
            break;
            case STOP:
                normal=0;
            default: // drop down
            break;
        }
       intakeWheelLeft.set(normal);
       intakeWheelRight.set(normal);
       
        SmartDashboard.putString("State", state.toString());
        SmartDashboard.putNumber("leftPower", normal);
        SmartDashboard.putNumber("rightPower", normal*-1 );// inversion happens in config
//Make three new methods instead of 1: In, Out and Stop
    }
}