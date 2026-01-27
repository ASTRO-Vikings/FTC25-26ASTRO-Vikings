package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToVelocity;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;

public class Flywheel implements Subsystem {
    public static final Flywheel INSTANCE = new Flywheel();
    public final int vel = 1050;

    private Flywheel() {
    }

    public final MotorEx leftMotor = new MotorEx("launcherLeft").floatMode();
    public final MotorEx rightMotor = new MotorEx("launcherRight").floatMode();


    private final ControlSystem rightController = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();
    private final ControlSystem leftController = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();



    public final Command off() {
        return new InstantCommand(() -> {
            leftMotor.setPower(1);
            rightMotor.setPower(1);
            leftController.setGoal(new KineticState(0.0, 0));
            rightController.setGoal(new KineticState(0.0, 0));
        }
    ).requires(this);
}

    public final Command on = new InstantCommand(() -> {
        leftMotor.setPower(1);
        rightMotor.setPower(1);
        leftController.setGoal(new KineticState(0.0, vel));
        rightController.setGoal(new KineticState(0.0, -vel));
        Carousel.INSTANCE.removeBall();
    }).requires(this);

    public String getFlywheelSpeeds(){
        return String.format("Left motor: %f Goal: %f Right motor: %f Goal: %f",leftMotor.getVelocity(), leftController.getGoal().getVelocity(), rightMotor.getVelocity(), rightController.getGoal().getVelocity());
    }

    @Override
    public void initialize(){
        leftMotor.setPower(0);
        rightMotor.setPower(0);
//        leftMotor.setDirection(-1);
    }

    @Override
    public void periodic() {
        leftMotor.setPower(leftController.calculate(leftMotor.getState()));
        rightMotor.setPower(rightController.calculate(rightMotor.getState()));
    }
}