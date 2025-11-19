package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToVelocity;
import dev.nextftc.hardware.impl.MotorEx;

public class Flywheel implements Subsystem {
    public static final Flywheel INSTANCE = new Flywheel();
    public final int vel = 500;
    private Flywheel() { }

    private final MotorEx rightMotor = new MotorEx("flywheelMotorLeft");
    private final MotorEx leftMotor = new MotorEx("flywheelMotorRight");

    private final ControlSystem controller = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();

    public final Command off = new RunToVelocity(controller, 0.0).requires(this).named("FlywheelOff");
    public final Command on = new RunToVelocity(controller, vel).requires(this).named("FlywheelOn");

    @Override
    public void periodic() {
        leftMotor.setPower(controller.calculate(leftMotor.getState()));
        rightMotor.setPower(controller.calculate(rightMotor.getState()));
    }
}