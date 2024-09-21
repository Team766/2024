package com.team766.robot.gatorade;

import static com.team766.framework3.Conditions.checkForStatus;
import static com.team766.framework3.Conditions.checkForStatusEntryWith;
import static com.team766.framework3.RulePersistence.*;
import static com.team766.framework3.StatusBus.getStatusOrThrow;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.team766.framework3.Rule;
import com.team766.framework3.RuleEngine;
import com.team766.logging.Severity;
import com.team766.robot.gatorade.constants.SwerveDriveConstants;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.Set;

public class Lights extends RuleEngine {
    private static final int CANID = 5;
    private static final int LED_COUNT = 90;
    private static final Animation rainbowAnimation = new RainbowAnimation(1, 1.5, LED_COUNT);
    private final CANdle candle = new CANdle(CANID, SwerveDriveConstants.SWERVE_CANBUS);

    public Lights() {
        addRule(
                Rule.create(
                                "OI State Updated",
                                () ->
                                        checkForStatusEntryWith(
                                                OI.OIStatus.class, s -> s.age() < 1.3))
                        .withOnTriggeringProcedure(
                                REPEATEDLY,
                                Set.of(),
                                () -> {
                                    final OI.OIStatus status = getStatusOrThrow(OI.OIStatus.class);
                                    setLightsForGamePiece(status.gamePieceType());
                                    setLightsForPlacement(
                                            status.placementPosition(), status.gamePieceType());
                                }));

        addRule(
                Rule.create(
                                "Endgame",
                                () ->
                                        DriverStation.getMatchTime() > 0
                                                && DriverStation.getMatchTime() < 17)
                        .withOnTriggeringProcedure(ONCE_AND_HOLD, Set.of(), () -> rainbow()));

        addRule(
                Rule.create("Default display", () -> checkForStatus(OI.OIStatus.class))
                        .withOnTriggeringProcedure(
                                REPEATEDLY,
                                Set.of(),
                                () -> {
                                    final OI.OIStatus status = getStatusOrThrow(OI.OIStatus.class);
                                    setLightsForGamePiece(status.gamePieceType());
                                    setLightsForPlacement(
                                            status.placementPosition(), status.gamePieceType());
                                }));
    }

    private void setLightsForPlacement(
            PlacementPosition placementPosition, GamePieceType gamePieceType) {
        switch (placementPosition) {
            case NONE -> white();
            case LOW_NODE -> green();
            case MID_NODE -> red();
            case HIGH_NODE -> orange();
            case HUMAN_PLAYER -> setLightsForGamePiece(gamePieceType);
            default ->
            // warn, ignore
            log(Severity.WARNING, "Unexpected placement position: " + placementPosition.toString());
        }
    }

    private void setLightsForGamePiece(GamePieceType gamePieceType) {
        switch (gamePieceType) {
            case CUBE -> purple();
            case CONE -> yellow();
        }
    }

    public void purple() {
        candle.setLEDs(128, 0, 128);
    }

    public void white() {
        // NOTE: 255, 255, 255 trips the breaker. lol
        candle.setLEDs(128, 128, 128);
    }

    public void yellow() {
        candle.setLEDs(255, 150, 0);
    }

    public void red() {
        candle.setLEDs(255, 0, 0);
    }

    public void green() {
        candle.setLEDs(0, 255, 0);
    }

    public void orange() {
        candle.setLEDs(255, 64, 0);
    }

    public void rainbow() {
        candle.animate(rainbowAnimation);
    }
}
