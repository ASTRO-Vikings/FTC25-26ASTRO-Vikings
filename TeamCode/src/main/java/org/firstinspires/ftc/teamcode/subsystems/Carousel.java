package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;


public class Carousel implements Subsystem{
    public static final Carousel INSTANCE = new Carousel();
    public final int rotAmt = 100;
    private  Carousel(){}
    private final MotorEx motor = new MotorEx("carouselMotor");
    private final ControlSystem controlSystem = ControlSystem.builder()
            .posPid(0.005,0,0)
            .basicFF(0.01,0.02,0.03)
            .build();

    @Override
    public void periodic(){
        motor.setPower(controlSystem.calculate(motor.getState()));
    }

    public Command toLow = new RunToPosition(controlSystem, controlSystem.getGoal().getPosition()-rotAmt).requires(this);
    public Command toHigh = new RunToPosition(controlSystem, controlSystem.getGoal().getPosition()+rotAmt).requires(this);
}