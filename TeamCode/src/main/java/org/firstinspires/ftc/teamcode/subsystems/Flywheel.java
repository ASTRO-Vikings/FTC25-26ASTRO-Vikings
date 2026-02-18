package org.firstinspires.ftc.teamcode.subsystems;


import org.firstinspires.ftc.teamcode.TeleOpRed;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

public class Flywheel implements Subsystem {
    public static final Flywheel INSTANCE = new Flywheel();
    public final int longVel = 500;//x drive wheels 600 //gecko wheels 500
    public final int shortVel = 430;//x drive wheels 550 //gecko wheels 430
    private Flywheel() {

    }

    private final MotorEx leftMotor = new MotorEx("launcherLeft").floatMode();
    private final MotorEx rightMotor = new MotorEx("launcherRight").floatMode();


    private final ControlSystem rightController = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();
    private final ControlSystem leftController = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF(0.01, 0.02, 0.03)
            .build();
    private int multiplier = 0;

    public final Command off() {
        return new InstantCommand(() -> {
            multiplier = 0;
            TeleOpRed.shooting = false;
        }).requires(this);
    }
    public final Command on(boolean shortLaunch){
        if (shortLaunch)
        {
            return new InstantCommand(() -> {
                multiplier = 1;
                leftController.setGoal(new KineticState(0.0, shortVel));
                rightController.setGoal(new KineticState(0.0, -shortVel));
            }).requires(this);
        } else {
            return new InstantCommand(() -> {
                multiplier = 1;
                leftController.setGoal(new KineticState(0.0, longVel));
                rightController.setGoal(new KineticState(0.0, -longVel));
            }).requires(this);
        }
    }

    public String getFlywheelSpeeds(){
        return String.format("Left motor: %f Goal: %f Right motor: %f Goal: %f",leftMotor.getVelocity(), leftController.getGoal().getVelocity(), rightMotor.getVelocity(), rightController.getGoal().getVelocity());
    }

    @Override
    public void periodic() {
        leftMotor.setPower(leftController.calculate(leftMotor.getState()) * multiplier);
        rightMotor.setPower(rightController.calculate(rightMotor.getState()) * multiplier);
    }
}