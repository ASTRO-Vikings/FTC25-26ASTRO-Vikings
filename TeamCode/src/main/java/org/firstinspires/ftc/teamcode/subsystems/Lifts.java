package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class Lifts implements Subsystem {
    public static final Lifts INSTANCE = new Lifts();
    public static final MotorEx motor = new MotorEx("lifts").zeroed();
    public static int goal = 30000;
    private Lifts(){}

    //TODO once we get the pos's
    @Override
    public void initialize() {
        motor.setCurrentPosition(0);
        motor.zeroed();
        motor.zero();
    }
    private final ControlSystem controller = ControlSystem.builder()
            .posPid(0.005, 0, 0)
            .elevatorFF(0.04)
            .build();

    public final Command down() {
        return new RunToPosition(controller, 0).requires(this);
    }

    public final Command up() {
        return new RunToPosition(controller, goal).requires(this);
    }

    public String tele(){
        return controller.getGoal().toString() + motor.getPower();
    }
    @Override
    public void periodic() {
        motor.setPower(controller.calculate(motor.getState()));
    }


}