package com.team766.framework3;

public class AdvancedUtils {
    /**
     * Start running a new Context so the given procedure can run in parallel.
     * The given procedure must only reserve Mechanisms that aren't reserved by the
     * calling Procedure.
     * In most cases, you want to use Context.runParallel instead of startAsync.
     */
    public static LaunchedContext startAsync(
            final Context callingContext, final Procedure procedure) {
        final ContextImpl callingContextImpl = (ContextImpl) callingContext;
        callingContextImpl.checkProcedureReservationsDisjoint(procedure);
        var context = new ContextImpl(procedure);
        context.schedule();
        return context;
    }

    private AdvancedUtils() {}
}
