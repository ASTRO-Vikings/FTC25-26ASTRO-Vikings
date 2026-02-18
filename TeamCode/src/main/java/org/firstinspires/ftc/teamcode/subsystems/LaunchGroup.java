package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.NullCommand;
import dev.nextftc.core.subsystems.SubsystemGroup;

public class LaunchGroup extends SubsystemGroup {
    public static final LaunchGroup INSTANCE = new LaunchGroup();
    double delayAfterRotate = 0.5;
    double delayBeforeLaunch = 1;
    double delayBeforeDown = 0.5;


    private LaunchGroup() {
        super(
                Elevator.INSTANCE,
                Flywheel.INSTANCE,
                Carousel.INSTANCE
        );
    }

    public Command launchAll(boolean shortLaunch) {
        return new SequentialGroup(
                Flywheel.INSTANCE.on(shortLaunch),
                new Delay(1.75),
                Elevator.INSTANCE.toHigh(),
                new Delay(1),
                Elevator.INSTANCE.toLow(),
                new Delay(0.4),
                Carousel.INSTANCE.launchMoveToRight(),
                new Delay(0.5),
                Elevator.INSTANCE.toHigh(),
                new Delay(1),
                Elevator.INSTANCE.toLow(),
                new Delay(0.4),
                Carousel.INSTANCE.launchMoveToRight(),
                new Delay(1),
                Elevator.INSTANCE.toHigh(),
                new Delay(0.5),
                Flywheel.INSTANCE.off(),
                Elevator.INSTANCE.toLow()
        );
    }

    private Command launchCurrent(boolean shortLaunch) {
        return new SequentialGroup(
                Carousel.INSTANCE.launchMoveToRight(true),
                Carousel.INSTANCE.launchMoveToLeft(true),
                new InstantCommand(Carousel.INSTANCE::removeBall),
                Flywheel.INSTANCE.on(shortLaunch),
                new Delay(delayBeforeLaunch),
                Elevator.INSTANCE.toHigh(),
                new Delay(delayBeforeDown),
                Flywheel.INSTANCE.off(),
                Elevator.INSTANCE.toLow());
    }

    private Command launchNext(boolean shortLaunch) {
        return new SequentialGroup(
                Carousel.INSTANCE.launchMoveToRight(true),
                new InstantCommand(Carousel.INSTANCE::removeBall),
                new Delay(delayAfterRotate),
                Flywheel.INSTANCE.on(shortLaunch),
                new Delay(delayBeforeLaunch),
                Elevator.INSTANCE.toHigh(),
                new Delay(delayBeforeDown),
                Flywheel.INSTANCE.off(),
                Elevator.INSTANCE.toLow());
    }

    private Command launchLast(boolean shortLaunch) {
        return new SequentialGroup(
                Carousel.INSTANCE.launchMoveToLeft(true),
                new InstantCommand(Carousel.INSTANCE::removeBall),
                new Delay(delayAfterRotate),
                Flywheel.INSTANCE.on(shortLaunch),
                new Delay(delayBeforeLaunch),
                Elevator.INSTANCE.toHigh(),
                new Delay(delayBeforeDown),
                Flywheel.INSTANCE.off(),
                Elevator.INSTANCE.toLow());
    }

    public Command launch(boolean shortLaunch) {
            if (Carousel.INSTANCE.hasBall(Carousel.BallState.GREEN_BALL, true)) {
                return launchCurrent(shortLaunch);
            }
            if (Carousel.INSTANCE.nextHasBall(Carousel.BallState.GREEN_BALL, true)) {
                return launchNext(shortLaunch);
            }
            if (Carousel.INSTANCE.lastHasBall(Carousel.BallState.GREEN_BALL, true)) {
                return launchLast(shortLaunch);
            }
            return new NullCommand();
    }

    public Command launchGreen(boolean shortLaunch){
        if (Carousel.INSTANCE.hasBall(Carousel.BallState.GREEN_BALL, false)) {
            return launchCurrent(shortLaunch);
        }
        if (Carousel.INSTANCE.nextHasBall(Carousel.BallState.GREEN_BALL, false)) {
            return launchNext(shortLaunch);
        }
        if (Carousel.INSTANCE.lastHasBall(Carousel.BallState.GREEN_BALL, false)) {
            return launchLast(shortLaunch);
        }
        return new NullCommand();
    }

    public Command launchPurple(boolean shortLaunch){
        if (Carousel.INSTANCE.hasBall(Carousel.BallState.PURPLE_BALL, false)) {
            return launchCurrent(shortLaunch);
        }
        if (Carousel.INSTANCE.nextHasBall(Carousel.BallState.PURPLE_BALL, false)) {
            return launchNext(shortLaunch);
        }
        if (Carousel.INSTANCE.lastHasBall(Carousel.BallState.PURPLE_BALL, false)) {
            return launchLast(shortLaunch);
        }
        return new NullCommand();
    }
}