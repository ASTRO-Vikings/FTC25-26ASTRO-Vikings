package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.powerable.SetPower;

@Configurable
public class Lifts implements Subsystem {
    public static final Lifts INSTANCE = new Lifts();
    public static final MotorEx motor = new MotorEx("lifts");
    public static  double upPos = -.5;
    public static  double downPos = .5;
    private Lifts(){}

    //TODO once we get the pos's
//    private final ControlSystem controlSystem = ControlSystem.builder()
//            .posPid(0.005, 0, 0)
//            .elevatorFF(0)
//            .build();
//
//    public Command toLow = new RunToPosition(controlSystem, 0).requires(this);
//    public Command toHigh = new RunToPosition(controlSystem, 1200).requires(this);

//    @Override
//    public void periodic() {
//        motor.setPower(controlSystem.calculate(motor.getState()));
//    }
}