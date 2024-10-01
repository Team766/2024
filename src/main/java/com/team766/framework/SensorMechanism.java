package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.NoSuchElementException;

public abstract class SensorMechanism<S extends Record & Status> extends SubsystemBase
        implements LoggingBase {
    private S status = null;

    @Override
    public Category getLoggerCategory() {
        return Category.MECHANISMS;
    }

    public S getStatus() {
        if (status == null) {
            throw new NoSuchElementException(getName() + " has not published a status yet");
        }
        return status;
    }

    @Override
    public final void periodic() {
        super.periodic();

        try {
            status = run();
            StatusBus.publishStatus(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerExceptionUtils.logException(ex);
        }
    }

    protected abstract S run();
}
