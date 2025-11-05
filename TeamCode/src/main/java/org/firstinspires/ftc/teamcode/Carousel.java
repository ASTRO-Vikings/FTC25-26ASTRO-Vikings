package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

public class Carousel implements Subsystem{
    public static final Carousel INSTANCE = new Carousel();
    private  Carousel(){}
        private MotorEx motor = new MotorEx("one");
    private ControlSystem controlSystem = ControlSystem.builder()
            .posPid(0.005,0,0)
            .elevatorFF(0)
            .build();

    @Override
    public void periodic(){
        motor.setPower(controlSystem.calculate(motor.getState()));
    }

    public Command toLow = new RunToPosition(controlSystem, 0).requires(this);
    public Command toHigh = new RunToPosition(controlSystem, 100).requires(this);
}