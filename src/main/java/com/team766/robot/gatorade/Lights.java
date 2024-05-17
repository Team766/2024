package com.team766.robot.gatorade;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.team766.framework.LightsBase;
import com.team766.framework.Statuses;
import com.team766.logging.Severity;
import com.team766.robot.gatorade.constants.SwerveDriveConstants;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.wpilibj.DriverStation;

public class Lights extends LightsBase {
    private final CANdle candle;
    private static final int CANID = 5;
    private static final int LED_COUNT = 90;
    private static final Animation rainbowAnimation = new RainbowAnimation(1, 1.5, LED_COUNT);

    public Lights() {
        candle = new CANdle(CANID, SwerveDriveConstants.SWERVE_CANBUS);
    }

    @Override
    protected void dispatch(Statuses statuses) {
        var status = statuses.get(OI.Status.class);
        if ((!status.isPresent() || status.get().age() > 1.3)
                && DriverStation.getMatchTime() > 0
                && DriverStation.getMatchTime() < 17) {
            rainbow();
        } else if (status.isPresent()) {
            var oiStatus = status.get().status;
            setLightsForGamePiece(oiStatus.gamePieceType());
            setLightsForPlacement(oiStatus.placementPosition(), oiStatus.gamePieceType());
        }
    }

    private void setLightsForPlacement(
            PlacementPosition placementPosition, GamePieceType gamePieceType) {
        switch (placementPosition) {
            case NONE:
                white();
                break;
            case LOW_NODE:
                green();
                break;
            case MID_NODE:
                red();
                break;
            case HIGH_NODE:
                orange();
                break;
            case HUMAN_PLAYER:
                setLightsForGamePiece(gamePieceType);
                break;
            default:
                // warn, ignore
                log(
                        Severity.WARNING,
                        "Unexpected placement position: " + placementPosition.toString());
                break;
        }
    }

    private void setLightsForGamePiece(GamePieceType gamePieceType) {
        if (gamePieceType == GamePieceType.CUBE) {
            purple();
        } else {
            yellow();
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
