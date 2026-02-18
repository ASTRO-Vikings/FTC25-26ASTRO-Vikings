package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class Lifts implements Subsystem {
    public static final Lifts INSTANCE = new Lifts();
    public static final MotorEx motor = new MotorEx("lifts").zeroed();
    public static int maxHeight = -31775-260;
    private Lifts(){}

    //TODO once we get the pos's
    @Override
    public void initialize() {
        motor.setCurrentPosition(0);
        motor.zeroed();
        motor.zero();
    }

    public final Command down() {
        return new InstantCommand(()->{motor.setPower(1);});
    }

    public final Command up() {
        return new InstantCommand(()->{
            if(motor.getCurrentPosition() < maxHeight){
                motor.setPower(-1);
            }});
    }
    public final Command off(){
        return new InstantCommand(()->{motor.setPower(0);});
    }

    public String tele(){
        return "Lifts at " + motor.getCurrentPosition();
    }
//    @Override
//    public void periodic() {
//        motor.setPower(controller.calculate(motor.getState()));
//    }


}