package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.NullCommand;
import dev.nextftc.core.subsystems.SubsystemGroup;

public class LaunchGroup extends SubsystemGroup {
    public static final LaunchGroup INSTANCE = new LaunchGroup();

    private LaunchGroup() {
        super(
                Elevator.INSTANCE,
                Flywheel.INSTANCE,
                Carousel.INSTANCE
        );
    }

    public Command launchAll() {
        return new SequentialGroup(
                Flywheel.INSTANCE.on,
                new Delay(1),
                Elevator.INSTANCE.toHigh,
                new Delay(0.5),
                Elevator.INSTANCE.toLow,
                new Delay(0.5),
                Carousel.INSTANCE.launchMoveToRight(),
                new Delay(0.5),
                Elevator.INSTANCE.toHigh,
                new Delay(0.5),
                Elevator.INSTANCE.toLow,
                new Delay(0.5),
                Carousel.INSTANCE.launchMoveToRight(),
                new Delay(0.5),
                Elevator.INSTANCE.toHigh,
                new Delay(0.5),
                Flywheel.INSTANCE.off,
                Elevator.INSTANCE.toLow
        );
    }


    public Command launch() {//////doing nohing
        Carousel.INSTANCE.teleStr = "Launching";
        if (Carousel.INSTANCE.hasBall()) {
            Carousel.INSTANCE.teleStr = "actulally launching";

            return new SequentialGroup(
                    Flywheel.INSTANCE.on,
                    new Delay(1),
                    Elevator.INSTANCE.toHigh,
                    new Delay(0.5),
                    Flywheel.INSTANCE.off,
                    Elevator.INSTANCE.toLow);
        } else if (Carousel.INSTANCE.nextHasBall()) {
            Carousel.INSTANCE.teleStr = "next actually has ball";

            return new SequentialGroup(
                    Carousel.INSTANCE.launchMoveToRight(),
                    new Delay(0.5),
                    Flywheel.INSTANCE.on,
                    new Delay(1),
                    Elevator.INSTANCE.toHigh,
                    new Delay(0.5),
                    Flywheel.INSTANCE.off,
                    Elevator.INSTANCE.toLow);
        } else if (Carousel.INSTANCE.lastHasBall()) {
            Carousel.INSTANCE.teleStr = "last actually has ball";

            return new SequentialGroup(
                    Carousel.INSTANCE.launchMoveToLeft(),
                    new Delay(0.5),
                    Flywheel.INSTANCE.on,
                    new Delay(1),
                    Elevator.INSTANCE.toHigh,
                    new Delay(0.5),
                    Flywheel.INSTANCE.off,
                    Elevator.INSTANCE.toLow);
        } else {
            return new NullCommand();
        }
    }
}