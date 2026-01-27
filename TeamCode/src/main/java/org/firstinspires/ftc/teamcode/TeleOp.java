package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
//import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorGoBildaPinpoint;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Carousel;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.LaunchGroup;
//import org.firstinspires.ftc.teamcode.subsystems.Lifts;

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
//                new SubsystemComponent(Lifts.INSTANCE),
                new SubsystemComponent(Intake.INSTANCE),
                new SubsystemComponent(Elevator.INSTANCE),
                new SubsystemComponent(LaunchGroup.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
                );
    }
    Gamepad.RumbleEffect customRumbleEffect;
    ElapsedTime runtime = new ElapsedTime();
    boolean secondHalf = false;
    final double HALF_TIME = 120.0;
    private TelemetryManager telemetryM;
//    private Follower follower;
    public static Pose startingPose;
    private boolean automatedDrive;
    private Supplier<PathChain> pathChain;
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private boolean isRobotCentric = true; //TODO decide whether field centric or robot centric
    //TODO TEMP DRIVE MOTORS
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft ;
    DcMotor backRight;
    IMU imu;
double botHeading;
    ColorSensor color;

    @Override
    public void onInit() {
        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                )
        );

        imu.initialize(parameters);

        imu.resetYaw();
        Carousel.INSTANCE.motor.zeroed();

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        color = hardwareMap.colorSensor.get("color");
        customRumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.0, 1.0, 500)  //  Rumble right motor 100% for 500 mSec
                .addStep(0.0, 0.0, 300)  //  Pause for 300 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .addStep(0.0, 0.0, 250)  //  Pause for 250 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .build();

//        follower = Constants.createFollower(hardwareMap);
//        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
//        follower.update();
        Flywheel.INSTANCE.off().schedule();
        Flywheel.INSTANCE.leftMotor.setPower(0);
        Flywheel.INSTANCE.rightMotor.setPower(0);
        Carousel.INSTANCE.reset();
//        Lifts.INSTANCE.reset().schedule();
    }

    @Override
    public void onStartButtonPressed() {
        //Carousel
        //Intake location rotation
        Gamepads.gamepad2().dpadLeft()
                .whenBecomesTrue(new ParallelGroup(
                    Carousel.INSTANCE.intakeMoveToLeft(),
                    new InstantCommand(()->{BindingManager.setLayer("Can Intake");})));

        Gamepads.gamepad2().dpadRight()
                .whenBecomesTrue(new ParallelGroup(
                        Carousel.INSTANCE.intakeMoveToRight(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Intake");})));

        //Launch location rotation
        Gamepads.gamepad2().leftBumper()
                .whenBecomesTrue(new ParallelGroup (
                        Carousel.INSTANCE.launchMoveToLeft(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Launch");})));

        Gamepads.gamepad2().rightBumper()
                .whenBecomesTrue(new ParallelGroup(
                        Carousel.INSTANCE.launchMoveToRight(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Launch");})));

        //Launching
        Button rightTrigger = Gamepads.gamepad2().rightTrigger().greaterThan(.1);

        rightTrigger
                .inLayer("Can Launch")
                .whenBecomesTrue(LaunchGroup.INSTANCE.launch);

        Gamepads.gamepad2().y()
                .inLayer("Can Launch")
                .whenBecomesTrue(LaunchGroup.INSTANCE.launchAll);

        //Lifts
//        Lifts.INSTANCE.motor.zero();
//        Gamepads.gamepad2().dpadUp()
//                .whenBecomesTrue(Lifts.INSTANCE.up());
//
//        Gamepads.gamepad2().dpadDown()
//                .whenBecomesTrue(Lifts.INSTANCE.down());

        //Intake
        Button leftTrigger = Gamepads.gamepad2().leftTrigger().greaterThan(.1);

       leftTrigger
                .inLayer("Can Intake")
                .whenBecomesTrue(Intake.INSTANCE.takeIn)
                .whenBecomesFalse(Intake.INSTANCE.stop);

        }



    @Override
    public void onUpdate(){
        botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        telemetryM.update();
        if ((runtime.seconds() > HALF_TIME) && !secondHalf)  {
            gamepad1.runRumbleEffect(customRumbleEffect);
            secondHalf =true;
        }

        if(gamepad1.aWasPressed()){
            slowMode=!slowMode;
        }

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

        frontLeft.setPower(frontLeftPower   * (!slowMode ? .8 : slowModeMultiplier));
        backLeft.setPower(backLeftPower     * (!slowMode ? .8 : slowModeMultiplier));
        frontRight.setPower(frontRightPower * (!slowMode ? .8 : slowModeMultiplier));
        backRight.setPower(backRightPower   * (!slowMode ? .8 : slowModeMultiplier));



//        telemetryM.debug("position", follower.getPose());
//        telemetryM.debug("velocity", follower.getVelocity());
        telemetryM.debug("slowmode", slowMode);
//        telemetryM.debug("Dpad up/down for lifts");
        telemetryM.debug("Bumper left/right for launch carousel");
        telemetryM.debug("Dpad left/right for intake carousel");
        telemetryM.debug("Left trigger for intake");
        telemetryM.debug("Right trigger for launch one");
        telemetryM.debug("Y for launch all");
        telemetryM.debug("A for slow mode");
//        telemetryM.debug(Flywheel.INSTANCE.getFlywheelSpeeds());
//        telemetryM.debug(Carousel.INSTANCE.getBallIndex());
//        telemetryM.debug(Carousel.INSTANCE.getBalls());
//        telemetryM.debug(Carousel.INSTANCE.getTelemetryStr());
//        telemetryM.debug("Red", color.red());
//        telemetryM.debug("Green", color.green());
//        telemetryM.debug("Blue", color.blue());
//        telemetryM.debug("Alpha", color.alpha());
//        telemetryM.debug("Elevator pos:", Lifts.INSTANCE.tele());
        telemetryM.update(telemetry);
    }


}