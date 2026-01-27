//package org.firstinspires.ftc.teamcode.subsystems;
//
//import com.bylazar.configurables.annotations.Configurable;
//
//import dev.nextftc.control.ControlSystem;
//import dev.nextftc.control.KineticState;
//import dev.nextftc.core.commands.Command;
//import dev.nextftc.core.commands.utility.InstantCommand;
//import dev.nextftc.core.subsystems.Subsystem;
//import dev.nextftc.hardware.controllable.RunToPosition;
//import dev.nextftc.hardware.impl.MotorEx;
//import dev.nextftc.hardware.impl.ServoEx;
//import dev.nextftc.hardware.powerable.SetPower;
//
//@Configurable
//public class Lifts implements Subsystem {
//    public static final Lifts INSTANCE = new Lifts();
//    public static final MotorEx motor = new MotorEx("lifts");
//    public static int goal = 28500;
//    private Lifts(){}
//
//    //TODO once we get the pos's
//    @Override
//    public void initialize(){
//        motor.zero();
//        motor.zeroed();
//    }
//    private final ControlSystem controller = ControlSystem.builder()
//            .posPid(0.005, 0, 0)
//            .elevatorFF(0.04)
//            .build();
//
//    public final Command down() {
//        return new RunToPosition(controller, 0).requires(this);
//    }
//
//    public final Command up() {
//        return new RunToPosition(controller, goal).requires(this);
//    }
//    public final Command reset(){
//        return new InstantCommand(
//                ()-> {
//                    motor.setCurrentPosition(0);
//                    controller.setGoal(new KineticState(0));
//                }
//        );
//    }
//
//    public String tele(){
//        return controller.getGoal().toString() + motor.getPower();
//    }
//    @Override
//    public void periodic() {
//        motor.setPower(controller.calculate(motor.getState()));
//    }
//
//
//}