package com.team766.framework3;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

public abstract class OI<StateRecord, LightSignals> {
    private LightSignals currentLightSignals;

	private Set<Subsystem> reservedSubsystems = new HashSet<>();

	protected class OICondition {
		public boolean isNewlyTriggering() {

		}

		public void ifNewlyTriggering(Supplier<Command> behaviorProvider) {
			if (isNewlyTriggering()) {
				final var behavior = behaviorProvider.get();
				if (reservedSubsystems.intersection(behavior.getRequirements()).isEmpty()) {
					reservedSubsystems.addAll(behavior.getRequirements());
					behavior.schedule(); // TODO: don't schedule if already scheduled
				}
			}
		}
	}

	protected void byDefault(Command behavior)
		if (reservedSubsystems.intersection(behavior.getRequirements()).isEmpty()) {
			reservedSubsystems.addAll(behavior.getRequirements());
			behavior.schedule(); // TODO: don't schedule if already scheduled
		}
	}

    /**
     * 
     */
    protected abstract void dispatch();

    /**
     *
     */
    protected abstract LightSignals updateLightSignals();

    /* package */ final void run() {
		for (var c : conditions) {
			evaluateCondition(c);
		}
		dispatch();
        currentLightSignals = updateLightSignals();
    }

    public abstract StateRecord getState();

    public final LightSignals getLightSignals() {
        return currentLightSignals;
    }
}
