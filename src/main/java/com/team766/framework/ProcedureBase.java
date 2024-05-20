package com.team766.framework;

import com.team766.logging.Category;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/* package */ abstract class ProcedureBase extends Command
        implements LoggingBase, Statuses.StatusSource {
    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected final int m_id;
    protected Category loggerCategory = Category.PROCEDURES;

    public static final List<Subsystem> NO_RESERVATIONS = List.of();

    public static Collection<Subsystem> reservations(Collection<Subsystem> reqs) {
        return reqs;
    }

    public static List<Subsystem> reservations(Subsystem[] reqs) {
        return Arrays.asList(reqs);
    }

    public static List<Subsystem> reservations(Subsystem req, Subsystem... reqs) {
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

    protected final void updateStatus(Record status) {
        Statuses.getInstance().add(status, this);
    }

    protected final <StatusRecord extends Record> Optional<StatusRecord> getStatus(
            Class<StatusRecord> c) {
        return Statuses.getStatus(c);
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
