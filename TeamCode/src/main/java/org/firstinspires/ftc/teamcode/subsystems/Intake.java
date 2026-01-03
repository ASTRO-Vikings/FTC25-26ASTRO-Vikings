package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.CRServoEx;
import dev.nextftc.hardware.powerable.SetPower;


public class Intake implements Subsystem{
    public static final Intake INSTANCE = new Intake();
    private Intake(){}
    private final CRServoEx intake = new CRServoEx("intake");


    public Command takeIn =
            new SetPower(intake, -1).and(
            new InstantCommand(()->Carousel.INSTANCE.addBall())
    );
    public Command stop = new SetPower(intake, 0);
}