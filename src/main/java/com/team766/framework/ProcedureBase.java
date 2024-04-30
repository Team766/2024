package com.team766.framework;

import com.team766.logging.Category;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/* package */ abstract class ProcedureBase extends Command implements LoggingBase {
    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected final int m_id;
    protected Category loggerCategory = Category.PROCEDURES;

    protected static final List<Subsystem> NO_RESERVATIONS = List.of();

    protected static Collection<Subsystem> reservations(Collection<Subsystem> reqs) {
        return reqs;
    }

    protected static List<Subsystem> reservations(Subsystem[] reqs) {
        return Arrays.asList(reqs);
    }

    protected static List<Subsystem> reservations(Subsystem req, Subsystem... reqs) {
        var list = new ArrayList<Subsystem>(1 + reqs.length);
        list.add(req);
        for (var r : reqs) {
            list.add(r);
        }
        return list;
    }

    ProcedureBase(Collection<Subsystem> reservations) {
        m_id = createNewId();
        setName(this.getClass().getName() + "/" + m_id);
        m_requirements.addAll(reservations);
    }

    public Set<Subsystem> getReservations() {
        return super.getRequirements();
    }

    public void addReservations(Subsystem... reqs) {
        addRequirements(reqs);
    }

    @Override
    public final Category getLoggerCategory() {
        return loggerCategory;
    }

    @Override
    public String toString() {
        return getName();
    }
}
