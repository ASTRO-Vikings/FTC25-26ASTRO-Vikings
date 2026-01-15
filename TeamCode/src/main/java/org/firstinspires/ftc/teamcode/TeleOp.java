package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Carousel;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.LaunchGroup;
import org.firstinspires.ftc.teamcode.subsystems.Lifts;

import java.util.function.Supplier;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.bindings.Button;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.FieldCentric;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.Direction;
import dev.nextftc.hardware.impl.IMUEx;
import dev.nextftc.hardware.impl.MotorEx;


@Configurable
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOp extends NextFTCOpMode {
    public TeleOp(){
        addComponents(
                new SubsystemComponent(Carousel.INSTANCE),
                new SubsystemComponent(Flywheel.INSTANCE),
                new SubsystemComponent(Lifts.INSTANCE),
                new SubsystemComponent(Intake.INSTANCE),
                new SubsystemComponent(Elevator.INSTANCE),
                new SubsystemComponent(LaunchGroup.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
                );
    }

    private TelemetryManager telemetryM;
    private Follower follower;
    public static Pose startingPose;
    private boolean automatedDrive;
    private Supplier<PathChain> pathChain;
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private boolean isRobotCentric = false; //TODO decide whether field centric or robot centric
    //TODO TEMP DRIVE MOTORS
    MotorEx frontLeft = new MotorEx("frontLeft");
    MotorEx frontRight = new MotorEx("frontRight");
    MotorEx backLeft = new MotorEx("backLeft");
    MotorEx backRight = new MotorEx("backRight");
    IMUEx imu = new IMUEx("imu", Direction.DOWN, Direction.FORWARD).zeroed();

    ColorSensor color;

    @Override
    public void onInit() {
        color = hardwareMap.colorSensor.get("color");

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        follower.update();

//      Path to follow during teleop
//        pathChain = () -> follower.pathBuilder() //Lazy Curve Generation
//                .addPath(new Path(new BezierLine(follower::getPose, new Pose(45, 98))))
//                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, Math.toRadians(45), 0.8))
//                .build();
    }

    @Override
    public void onStartButtonPressed() {
        //Carousel
        //Intake location rotation
        Gamepads.gamepad1().dpadLeft()
                .whenBecomesTrue(new ParallelGroup(
                    Carousel.INSTANCE.intakeMoveToLeft(),
                    new InstantCommand(()->{BindingManager.setLayer("Can Intake");})));

        Gamepads.gamepad1().dpadRight()
                .whenBecomesTrue(new ParallelGroup(
                        Carousel.INSTANCE.intakeMoveToRight(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Intake");})));

        //Launch location rotation
        Gamepads.gamepad1().leftBumper()
                .whenBecomesTrue(new ParallelGroup (
                        Carousel.INSTANCE.launchMoveToLeft(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Launch");})));

        Gamepads.gamepad1().rightBumper()
                .whenBecomesTrue(new ParallelGroup(
                        Carousel.INSTANCE.launchMoveToRight(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Launch");})));

        //Launching
        Button rightTrigger = Gamepads.gamepad1().rightTrigger().greaterThan(.1);

        rightTrigger
                .inLayer("Can Launch")
                .whenBecomesTrue(LaunchGroup.INSTANCE.launch);

        Gamepads.gamepad1().y()
                .inLayer("Can Launch")
                .whenBecomesTrue(LaunchGroup.INSTANCE.launchAll);

        //Lifts
        Gamepads.gamepad1().dpadUp()
                .whenBecomesTrue(Lifts.INSTANCE.up());

        Gamepads.gamepad1().dpadDown()
                .whenBecomesTrue(Lifts.INSTANCE.down());

        //Intake
        Button leftTrigger = Gamepads.gamepad1().leftTrigger().greaterThan(.1);

       leftTrigger
                .inLayer("Can Intake")
                .whenBecomesTrue(Intake.INSTANCE.takeIn)
                .whenBecomesFalse(Intake.INSTANCE.stop);
       //Drive Code
        follower.startTeleopDrive();
        }



    @Override
    public void onUpdate(){
        follower.update();
        telemetryM.update();
        //TODO TEMPORARAY ROBOCENTRIC DRIVE CODE UNTIL PEDROPATHING CONSTANTS ARE DONE TO USE PEDROPATHING TELEOP
        double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);

        //END TODO

//
//        //If not following path allow driving
//        if (!automatedDrive) {
//            if (!slowMode) follower.setTeleOpDrive(
//                    -gamepad1.left_stick_y,
//                    -gamepad1.left_stick_x,
//                    -gamepad1.right_stick_x,
//                    isRobotCentric
//            );
//            else follower.setTeleOpDrive(
//                    -gamepad1.left_stick_y * slowModeMultiplier,
//                    -gamepad1.left_stick_x * slowModeMultiplier,
//                    -gamepad1.right_stick_x * slowModeMultiplier,
//                    isRobotCentric
//            );
//        }
////        Follow path
////        if (gamepad1.aWasPressed()) {
////            follower.followPath(pathChain.get());
////            automatedDrive = true;
////        }
////        if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
////            follower.startTeleopDrive();
////            automatedDrive = false;
////        }
//
//        //Toggle slowmode
//        if (gamepad1.leftTriggerWasPressed()) {
//            slowMode = !slowMode;
//        }

//        telemetryM.debug("position", follower.getPose());
//        telemetryM.debug("velocity", follower.getVelocity());
//        telemetryM.debug("automatedDrive", automatedDrive);
        telemetryM.debug("Dpad up/down for lifts");
        telemetryM.debug("Bumper left/right for launch carousel");
        telemetryM.debug("Dpad left/right for intake carousel");
        telemetryM.debug("Left trigger for intake");
        telemetryM.debug("Right trigger for launch one");
        telemetryM.debug("Y for launch all");
        telemetryM.debug(Flywheel.INSTANCE.getFlywheelSpeeds());
        telemetryM.debug(Carousel.INSTANCE.getBallIndex());
        telemetryM.debug(Carousel.INSTANCE.getBalls());
        telemetryM.debug(Carousel.INSTANCE.getTelemetryStr());
        telemetryM.debug("Red", color.red());
        telemetryM.debug("Green", color.green());
        telemetryM.debug("Blue", color.blue());
        telemetryM.debug("Alpha", color.alpha());
        telemetryM.debug("Elevator pos:", Lifts.INSTANCE.tele());
        telemetryM.update(telemetry);
    }


}