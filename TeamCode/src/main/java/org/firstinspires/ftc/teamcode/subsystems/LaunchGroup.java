package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.conditionals.IfElseCommand;
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

    public Command launchAll = new SequentialGroup(
                Flywheel.INSTANCE.on,
                new Delay(2),
                Elevator.INSTANCE.toHigh,
                new Delay(0.5),
                Elevator.INSTANCE.toLow,
                new Delay(0.5),
                Carousel.INSTANCE.launchMoveToRight(),
                new Delay(1),
                Elevator.INSTANCE.toHigh,
                new Delay(0.5),
                Elevator.INSTANCE.toLow,
                new Delay(0.5),
                Carousel.INSTANCE.launchMoveToRight(),
                new Delay(1),
                Elevator.INSTANCE.toHigh,
                new Delay(0.5),
                Flywheel.INSTANCE.off(),
                Elevator.INSTANCE.toLow
        );



public Command launch = new SequentialGroup(
        Flywheel.INSTANCE.on,
        new Delay(2),
        Elevator.INSTANCE.toHigh,
        new Delay(0.5),
        Flywheel.INSTANCE.off(),
        new Delay(0.1),
        Elevator.INSTANCE.toLow);

//    public Command launch() {
//        Command launchCurrent =
//                new SequentialGroup(
//                        Flywheel.INSTANCE.on,
//                        new Delay(1),
//                        Elevator.INSTANCE.toHigh,
//                        new Delay(0.5),
//                        Flywheel.INSTANCE.off,
//                        Elevator.INSTANCE.toLow);
//
//        Command launchNext =
//                new SequentialGroup(
//                        Carousel.INSTANCE.launchMoveToRight(),
//                        new Delay(0.5),
//                        Flywheel.INSTANCE.on,
//                        new Delay(1),
//                        Elevator.INSTANCE.toHigh,
//                        new Delay(0.5),
//                        Flywheel.INSTANCE.off,
//                        Elevator.INSTANCE.toLow);
//
//        Command launchLast =
//                new SequentialGroup(
//                        Carousel.INSTANCE.launchMoveToLeft(),
//                        new Delay(0.5),
//                        Flywheel.INSTANCE.on,
//                        new Delay(1),
//                        Elevator.INSTANCE.toHigh,
//                        new Delay(0.5),
//                        Flywheel.INSTANCE.off,
//                        Elevator.INSTANCE.toLow);
//
//        return new IfElseCommand(
//                Carousel.INSTANCE::hasBall,//Condition
//                launchCurrent,//True Command
//                new IfElseCommand(//False Command
//                        Carousel.INSTANCE::nextHasBall,//Cond
//                        launchNext,//True
//                        new IfElseCommand(//False
//                                Carousel.INSTANCE::lastHasBall,//Cond
//                                launchLast//True
//                        )
//                )
//        );
//    }

}