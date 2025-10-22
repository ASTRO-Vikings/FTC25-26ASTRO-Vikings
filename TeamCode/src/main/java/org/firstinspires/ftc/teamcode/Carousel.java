package org.firstinspires.ftc.teamcode;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

public class Carousel implements Subsystem {
    public static final Carousel INSTANCE = new Carousel();
    private Carousel() {}
    MotorEx carouselMotor = new MotorEx("one");

    private ControlSystem controlSystem = ControlSystem.builder()
            .basicFF(0.005, 0,0)
            .build();

    public Command spin = new RunToPosition(controlSystem, carouselMotor.getState().getPosition()+100).requires(this);

    @Override
    public void periodic(){
        carouselMotor.setPower(controlSystem.calculate(carouselMotor.getState()));
    }
}
