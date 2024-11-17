package com.team766.robot.rookie_bot;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.example.procedures.*;
import com.team766.robot.rookie_bot.procedures.PIDElevator;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        joystick2 = RobotProvider.instance.getJoystick(2);
    }



    public void run(final Context context) {
        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            Robot.drive.setArcadeDrivePower(joystick0.getAxis(1), joystick0.getAxis(3));

            if ( joystick0.getButtonPressed(5) ){
                context.startAsync(new PIDElevator(false));
            }
            if ( joystick0.getButtonPressed(6) ){
                context.startAsync(new PIDElevator(true));
            }
        

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.
        }
    }
}
