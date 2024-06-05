package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/* package */ abstract class ProcedureBase extends Command implements ProcedureInterface {
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

    private ContextImpl m_context = null;

    ProcedureBase(Collection<Subsystem> reservations) {
        m_requirements.addAll(reservations);
    }

    public Set<Subsystem> getReservations() {
        return super.getRequirements();
    }

    public void addReservations(Subsystem... reservations) {
        addRequirements(reservations);
    }

    public void addReservations(Collection<Subsystem> reservations) {
        getReservations().addAll(reservations);
    }

    @Override
    public String toString() {
        return getName();
    }

    private Command command() {
        if (m_context == null) {
            m_context = new ContextImpl(this);
        }
        return m_context;
    }

    @Override
    public final void initialize() {
        command().initialize();
    }

    @Override
    public final void execute() {
        command().execute();
    }

    @Override
    public final void end(boolean interrupted) {
        command().end(interrupted);
    }

    @Override
    public final boolean isFinished() {
        return command().isFinished();
    }

    @Override
    public final boolean runsWhenDisabled() {
        return command().runsWhenDisabled();
    }
}
