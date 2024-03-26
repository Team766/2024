package com.team766.robot.reva.mechanisms;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.team766.hal.MotorController;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

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

    public static double getStatorCurrentUsage(MotorController motor) {
        if (motor instanceof TalonFX) {
            StatusSignal<Double> current = ((TalonFX) motor).getStatorCurrent();
            if (current.getStatus().isOK()) {
                return current.getValueAsDouble();
            }
        }
        return -1;
    }

    public static void setTalonFXStatorCurrentLimit(MotorController motor, double ampsLimit) {
        if (motor instanceof TalonFX) {
            TalonFX talonFX = (TalonFX) motor;
            CurrentLimitsConfigs config = new CurrentLimitsConfigs();
            StatusCode status = talonFX.getConfigurator().refresh(config);
            if (!status.isOK()) {
                Logger.get(Category.MECHANISMS)
                        .logRaw(
                                Severity.WARNING,
                                "Unable to get current limit configs: " + status.toString());
                ;
            }
            config.withStatorCurrentLimit(ampsLimit).withSupplyCurrentLimitEnable(true);
            status = talonFX.getConfigurator().apply(config);
            if (!status.isOK()) {
                Logger.get(Category.MECHANISMS)
                        .logRaw(
                                Severity.WARNING,
                                "Unable to set stator current limit: " + status.toString());
                ;
            }
        }
    }

    private static SoftwareLimitSwitchConfigs getLimitSwitchConfigs(TalonFX talonFX) {
        SoftwareLimitSwitchConfigs limitSwitchConfigs = new SoftwareLimitSwitchConfigs();
        StatusCode status = talonFX.getConfigurator().refresh(limitSwitchConfigs);
        if (!status.isOK()) {
            Logger.get(Category.MECHANISMS)
                    .logRaw(
                            Severity.WARNING,
                            "Unable to get limit switch configs: " + status.toString());
            ;
        }
        return limitSwitchConfigs;
    }

    private static void applyLimitSwitchConfigs(
            TalonFX talonFX, SoftwareLimitSwitchConfigs limitSwitchConfigs) {
        StatusCode status = talonFX.getConfigurator().apply(limitSwitchConfigs);
        if (!status.isOK()) {
            Logger.get(Category.MECHANISMS)
                    .logRaw(
                            Severity.WARNING,
                            "Unable to set limit switch configs: " + status.toString());
        }
    }

    // TODO: add the appropriate support for SparkMax as well.
    public static void setSoftLimits(
            MotorController motor, double forwardLimit, double reverseLimit) {
        if (!(motor instanceof TalonFX)) {
            return;
        }
        TalonFX talonFX = (TalonFX) motor;

        SoftwareLimitSwitchConfigs limitSwitchConfigs = getLimitSwitchConfigs(talonFX);
        limitSwitchConfigs.ForwardSoftLimitThreshold = forwardLimit;
        limitSwitchConfigs.ReverseSoftLimitThreshold = reverseLimit;
        limitSwitchConfigs.ForwardSoftLimitEnable = true;
        limitSwitchConfigs.ReverseSoftLimitEnable = true;
        applyLimitSwitchConfigs(talonFX, limitSwitchConfigs);
    }

    public static void enableSoftLimits(MotorController motor, boolean enable) {
        if (!(motor instanceof TalonFX)) {
            return;
        }
        TalonFX talonFX = (TalonFX) motor;

        SoftwareLimitSwitchConfigs limitSwitchConfigs = getLimitSwitchConfigs(talonFX);
        limitSwitchConfigs.ForwardSoftLimitEnable = enable;
        limitSwitchConfigs.ReverseSoftLimitEnable = enable;
        applyLimitSwitchConfigs(talonFX, limitSwitchConfigs);
    }

    public static boolean checkMotor(String label, MotorController motor) {
        if (motor instanceof TalonFX) {
            TalonFX talonFX = (TalonFX) motor;
            boolean check = talonFX.isAlive();
            Logger.get(Category.MECHANISMS).logData(Severity.INFO, "{0}: {1}", label, check);
            // TODO: also check for faults, sticky faults?
            return check;
        }

        if (motor instanceof CANSparkMax) {
            CANSparkMax sparkMax = (CANSparkMax) motor;
            // CANSparkMax doesn't have an "isAlive()" method.  Use something else that depends on
            // being able to communicate over the CAN bus.
            boolean check = sparkMax.getMotorTemperature() > 0;
            Logger.get(Category.MECHANISMS).logData(Severity.INFO, "{0}: {1}", label, check);
            // TODO: also check for faults?
            return check;
        }
        Logger.get(Category.MECHANISMS).logData(Severity.INFO, "{0}: unknown", label);
        return false;
    }
}
