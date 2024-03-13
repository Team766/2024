package com.team766.robot.reva.mechanisms;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.team766.hal.MotorController;

// throaway class.  this is ugly - quick-and-dirty utility class to help us understand
// current draw by motor.
// delete this if we add this functionality to MotorController in MaroonFramework.
public final class MotorUtil {
    private MotorUtil() {}

    private static double getTalonFXCurrentUsage(TalonFX motor) {
        StatusSignal<Double> current = ((TalonFX) motor).getSupplyCurrent();
        if (current.getStatus().isOK()) {
            return current.getValueAsDouble();
        }
        return -1;
    }

    private static double getSparkMaxCurrentUsage(CANSparkMax motor) {
        return motor.getOutputCurrent();
    }

    public static double getCurrentUsage(MotorController motor) {
        if (motor instanceof TalonFX) {
            return getTalonFXCurrentUsage((TalonFX) motor);
        } else if (motor instanceof CANSparkMax) {
            return getSparkMaxCurrentUsage((CANSparkMax) motor);
        } else {
            return -1;
        }
    }
}
