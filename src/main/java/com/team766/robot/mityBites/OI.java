package com.team766.robot.mityBites;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color8Bit;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;
    
    private Mechanism2d testMechanism;
    private MechanismLigament2d _spinner;
    private MechanismLigament2d _spinner2;

    private float spinnerAngle;
    private float spinnerAngle2;
    private boolean spinleft;
    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        joystick2 = RobotProvider.instance.getJoystick(2);
        spinnerAngle = 0;
        spinleft = false;
        
        
        // original mechanism
        testMechanism = new Mechanism2d(10, 10, new Color8Bit(0,200,0));
        
        // create a root node
        MechanismRoot2d root = testMechanism.getRoot("testSpinner", 5, 0);

        // append a Ligament part to the root node
        _spinner = root.append(new MechanismLigament2d("spinLine", 3, 0, 30, new Color8Bit(200,0,0))); 
        _spinner2 = _spinner.append(new MechanismLigament2d("spinner2", 2, 0, 10, new Color8Bit(0,0,200)));
        //put this on the SmartDashboard
        SmartDashboard.putData("MyMechanism", testMechanism);
    }

    public void run(final Context context) {
        context.takeOwnership(Robot.drive);

        while (true) {
            if(_spinner.getAngle() < 90) spinleft = true;
            if(_spinner.getAngle() > -90) spinleft = false; 
            if(spinleft)
                spinnerAngle += 0.2;
            else   
                spinnerAngle -=0.2;
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();
            _spinner.setAngle(spinnerAngle);
            _spinner2.setAngle(spinnerAngle2);
            // Add driver controls here - make sure to take/release ownership
            Robot.drive.OneJoystickDrive(-joystick0.getAxis(1), joystick0.getAxis(0));
            SmartDashboard.putData("MyMechanism", testMechanism);
        }
    }
}
