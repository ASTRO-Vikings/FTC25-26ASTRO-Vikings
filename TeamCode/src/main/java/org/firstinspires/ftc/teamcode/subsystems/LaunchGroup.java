package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
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
            Elevator.INSTANCE.toHigh
    );
}