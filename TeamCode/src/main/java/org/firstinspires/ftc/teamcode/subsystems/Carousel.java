package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

public class Carousel implements Subsystem {
    public static final Carousel INSTANCE = new Carousel();
    private final double COUNTS_PER_360_DEGREES = 2786.2;
    private final double TICKS_PER_DEGREE = COUNTS_PER_360_DEGREES / 360.0;
    public final int POSITION_LEFT = 0;
    public final int POSITION_LEFT_MIDDLE = 60;
    public final int POSITION_MIDDLE = 120;
    public final int POSITION_RIGHT_MIDDLE = 180;
    public final int POSITION_RIGHT = 240;
    public final int POSITION_LEFT_RIGHT = 300;

    private Carousel() {}
    private final MotorEx motor = new MotorEx("carousel");
    private final ControlSystem controlSystem = ControlSystem.builder()
            .angular(AngleType.DEGREES,
                    feedback -> feedback.posPid(0.04, 0, 0.001))
            .basicFF(0.05, 0, 0)
            .build();

    // Enum to keep track of the current logical state
    public enum CarouselState {
        LEFT, LEFT_MIDDLE, MIDDLE, RIGHT_MIDDLE, RIGHT, LEFT_RIGHT
    }

    // Track the current desired state
    public CarouselState currentState = CarouselState.LEFT;

    @Override
    public void initialize() {
        currentState = CarouselState.LEFT;
        new InstantCommand(() -> motor.setPower(0.0)).schedule();
        controlSystem.setGoal(new KineticState(POSITION_LEFT));
    }

    @Override
    public void periodic() {
        KineticState motorState = motor.getState();
        double currentAngleDegrees = (motorState.getPosition() / TICKS_PER_DEGREE) % 360.0;
        if (currentAngleDegrees < 0) {
            currentAngleDegrees += 360.0;
        }
        KineticState wrappedState = new KineticState(currentAngleDegrees, motorState.getVelocity() / TICKS_PER_DEGREE);

        motor.setPower(controlSystem.calculate(wrappedState));
    }

    public Command launchMoveToRight() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.RIGHT;
                    break;
                case LEFT_RIGHT:
                    currentState = CarouselState.RIGHT;
                    break;
                case RIGHT:
                    currentState = CarouselState.MIDDLE;
                    break;
                case RIGHT_MIDDLE:
                    currentState = CarouselState.MIDDLE;
                    break;
                case MIDDLE:
                    currentState = CarouselState.LEFT;
                    break;
                case LEFT_MIDDLE:
                    currentState = CarouselState.LEFT;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }

    public Command launchMoveToLeft() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.MIDDLE;
                    break;
                case LEFT_RIGHT:
                    currentState = CarouselState.LEFT;
                    break;
                case RIGHT:
                    currentState = CarouselState.LEFT;
                    break;
                case RIGHT_MIDDLE:
                    currentState = CarouselState.RIGHT;
                    break;
                case MIDDLE:
                    currentState = CarouselState.RIGHT;
                    break;
                case LEFT_MIDDLE:
                    currentState = CarouselState.MIDDLE;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }
    public Command intakeMoveToLeft() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case LEFT_RIGHT:
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case RIGHT:
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
                case RIGHT_MIDDLE:
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
                case MIDDLE:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    break;
                case LEFT_MIDDLE:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }
    public Command intakeMoveToRight() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case LEFT_RIGHT:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    break;
                case RIGHT:
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
                case RIGHT_MIDDLE:
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case MIDDLE:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    break;
                case LEFT_MIDDLE:
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }

    private void setGoalForCurrentState() {
        int targetPos;
        switch (currentState) {
            case LEFT:
                targetPos = POSITION_LEFT;
                break;
            case LEFT_RIGHT:
                targetPos = POSITION_LEFT_RIGHT;
                break;
            case RIGHT:
                targetPos = POSITION_RIGHT;
                break;
            case RIGHT_MIDDLE:
                targetPos = POSITION_RIGHT_MIDDLE;
                break;
            case MIDDLE:
                targetPos = POSITION_MIDDLE;
                break;
            case LEFT_MIDDLE:
                targetPos = POSITION_LEFT_MIDDLE;
                break;
            default:
                targetPos = POSITION_LEFT;
        }
        KineticState newGoal = new KineticState(targetPos);
        controlSystem.setGoal(newGoal);
    }
}