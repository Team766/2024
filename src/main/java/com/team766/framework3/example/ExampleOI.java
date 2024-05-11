package com.team766.framework3.example;

import com.team766.framework.OIFragment;
import com.team766.framework3.OI;
import com.team766.robot.gatorade.PlacementPosition;

public class ExampleOI extends OI<ExampleOI.State, ExampleOI.LightSignals> {
	public static class State {
		PlacementPosition placementPosition;
	}

	public static record LightSignals() {
	}

	private final State state = new State();

	private final OICondition buttonPushed = new OICondition(() -> joystick1.getButtonPressed(1));

	public ExampleOI() {
	}

	@Override
	protected void dispatch() {
		dispatchDriver();
		dispatchBoxop();
		dispatchDebug();
	}

	// TODO: if we made the condition more declarative (i.e. add rules in the constructor instead of
	// evaluating it procedurally in dispatch()), then we could check that the order of conditions and
	// byDefault is correct with "static analysis".

	public void dispatchDriver() {
		// byDefault(new StopIntake()); // bad! semi-auto aimbot wants to reserve the intake as well

		// manually control drive

		// semi auto aimbot

		byDefault(new StopDrive());

		if (buttonPushed.isNewlyTriggering()) {
			new MyBehavior().schedule();
		} else if (buttonPushed.isFinishedTriggering()) {
			new YourBehavior().schedule();
		}

		checkCondition(buttonPushed,
			() -> new MyBehavior().schedule(),
			() -> {},
			() -> new YourBehavior().schedule()
		);

		// buttonPushed
		// 	.ifNewlyTriggering(() -> new MyBehavior());

		buttonPushed
			.whileTriggering(() -> new MyBehavior());
			// .ifFinishedTriggering(() -> new YourBehavior());
		
		byDefault(new StopIntake());
		byDefault(new RetractWristvator());
	}

	public void dispatchBoxop() {
		
	}

	public void dispatchDebug() {
		
	}

	@Override
	public ExampleOI.State getState() {
		return state;
	}

	@Override
	protected ExampleOI.LightSignals updateLightSignals() {
		return new LightSignals();
	}
	
}
