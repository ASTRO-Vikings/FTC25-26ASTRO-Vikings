package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

public class Scoop implements Subsystem {
    public static final Scoop INSTANCE = new Scoop();
    private Scoop() { }

    private final ServoEx servo = new ServoEx("scoopServo");

    public Command up = new SetPosition(servo, 0.1).requires(this);
    public Command down = new SetPosition(servo, 0.2).requires(this);
}