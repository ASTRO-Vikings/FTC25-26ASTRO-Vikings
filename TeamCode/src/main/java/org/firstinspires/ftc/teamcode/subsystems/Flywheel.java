package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToVelocity;
import dev.nextftc.hardware.impl.MotorEx;

public class Flywheel implements Subsystem {
    public static final Flywheel INSTANCE = new Flywheel();
    public final int vel = 1000;
    private Flywheel() { }

    private final MotorEx leftMotor = new MotorEx("launcherLeft");
    private final MotorEx rightMotor = new MotorEx("launcherRight");

    private final ControlSystem rightController = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();
    private final ControlSystem leftController = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();

    public final Command off = new InstantCommand(() -> {
        leftController.setGoal(new KineticState(0.0,0.0));
        rightController.setGoal(new KineticState(0.0,0.0));
    }).requires(this);

    public final Command on = new InstantCommand(() -> {
        leftController.setGoal(new KineticState(0.0,vel));
        rightController.setGoal(new KineticState(0.0,vel));
    }).requires(this);

    @Override
    public void initialize(){
        leftMotor.setDirection(-1);
    }

    @Override
    public void periodic() {
        leftMotor.setPower(leftController.calculate(leftMotor.getState()));
        rightMotor.setPower(rightController.calculate(rightMotor.getState()));
    }
}