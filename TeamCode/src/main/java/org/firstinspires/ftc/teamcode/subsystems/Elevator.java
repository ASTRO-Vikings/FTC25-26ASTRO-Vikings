package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;


public class Elevator implements Subsystem{
    public static final Elevator INSTANCE = new Elevator();
    public final double upPos = 0;
    public final double downPos = 1;
    private Elevator(){}
    private final ServoEx elevator = new ServoEx("elevator");

    public Command toLow = new SetPosition(elevator, downPos).requires(this);
    public Command toHigh = new SetPosition(elevator, upPos).requires(this);

    @Override
    public void initialize(){
        toLow.schedule();
    }
}