package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.conditionals.IfElseCommand;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
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

    public Command launch = new SequentialGroup(
                    Flywheel.INSTANCE.on,
                    new Delay(1),
                    Elevator.INSTANCE.toHigh,
                    new Delay(0.5),
                    Flywheel.INSTANCE.off,
                    Elevator.INSTANCE.toLow
                    );

    public Command launchAll = new SequentialGroup(
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