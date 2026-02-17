package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

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
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.GamepadEx;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.DriverControlledCommand;
import dev.nextftc.hardware.impl.Direction;
import dev.nextftc.hardware.impl.IMUEx;


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
                BindingsComponent.INSTANCE,
                new PedroComponent(Constants::createFollower)
                );
    }

    private TelemetryManager telemetryM;
//    private Follower follower = PedroComponent.follower();
    public static Pose startingPose;
    boolean isTurningToTag = false;

    private boolean automatedDrive = false;
    private Supplier<PathChain> pathChain;
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private Limelight3A limelight;
    private boolean isRobotCentric = false;
    boolean turnToTag = false;
    IMUEx imu = new IMUEx("imu", Direction.DOWN, Direction.FORWARD).zeroed();

    boolean shortLaunch = false;
    public static boolean shooting = false;
    RevBlinkinLedDriver blinkin;
    GamepadEx driverGamepad = Gamepads.gamepad1();
    DriverControlledCommand driverControlled = new PedroDriverControlled(
            driverGamepad.leftStickY().negate(),
            driverGamepad.leftStickX().negate(),
            driverGamepad.rightStickX().negate(),
            isRobotCentric
    );

    @Override
    public void onInit() {
        if (startingPose == null){
            startingPose = new Pose(0,0,270);
        }
        PedroComponent.follower().setPose(startingPose);
        Carousel.INSTANCE.evilInit(hardwareMap);
        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_LAVA_PALETTE);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
    }

    @Override
    public void onWaitForStart(){
        Gamepads.gamepad1().a().whenBecomesTrue(new InstantCommand(()->{isRobotCentric = !isRobotCentric;}));
        telemetry.addData("Robot centric: ", isRobotCentric);
    }

    @Override
    public void onStartButtonPressed() {
        GamepadEx carouselGamepad = Gamepads.gamepad2();
        
        //Carousel
        //Intake location rotation
        carouselGamepad.dpadLeft()
                .whenBecomesTrue(new ParallelGroup(
                    Carousel.INSTANCE.intakeMoveToLeft(),
                    new InstantCommand(()->{BindingManager.setLayer("Can Intake");})));

        carouselGamepad.dpadRight()
                .whenBecomesTrue(new ParallelGroup(
                        Carousel.INSTANCE.intakeMoveToRight(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Intake");})));

        carouselGamepad.a()
                        .whenBecomesTrue(new InstantCommand(()->{
                            turnToTag = ! turnToTag;
                        }));

        //Launch location rotation
        carouselGamepad.leftBumper()
                .whenBecomesTrue(new ParallelGroup (
                        Carousel.INSTANCE.launchMoveToLeft(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Launch");})));

        carouselGamepad.rightBumper()
                .whenBecomesTrue(new ParallelGroup(
                        Carousel.INSTANCE.launchMoveToRight(),
                        new InstantCommand(()->{BindingManager.setLayer("Can Launch");})));

        //Launching
        Button rightTrigger = carouselGamepad.rightTrigger().greaterThan(.1);

        rightTrigger
                .inLayer("Can Launch")
                .whenBecomesTrue(
                        new InstantCommand(() ->
                        {
                            LaunchGroup.INSTANCE.launch(shortLaunch).schedule();
                            shooting = true;
                        }
                        )
                );
        carouselGamepad.x()
                        .inLayer("Can Launch")
                        .whenBecomesTrue(
                                new InstantCommand(()->
                                {
                                    LaunchGroup.INSTANCE.launchPurple(shortLaunch).schedule();
                                    shooting = true;
                                }
                                )
                        );
        carouselGamepad.b()
                .inLayer("Can Launch")
                .whenBecomesTrue(
                        new InstantCommand(()->
                        {
                            shooting = true;
                            LaunchGroup.INSTANCE.launchGreen(shortLaunch).schedule();
                        }
                        )
                );

        carouselGamepad.y()
                .inLayer("Can Launch")
                .whenBecomesTrue(LaunchGroup.INSTANCE.launchAll(shortLaunch));

        //Lifts
        Lifts.INSTANCE.initialize();
        carouselGamepad.dpadUp()
                .whenBecomesTrue(Lifts.INSTANCE.up())
                        .whenBecomesFalse(Lifts.INSTANCE.off());

        carouselGamepad.dpadDown()
                .whenBecomesTrue(Lifts.INSTANCE.down())
                .whenBecomesFalse(Lifts.INSTANCE.off());

        //Intake
        Button leftTrigger = carouselGamepad.leftTrigger().greaterThan(.1);

       leftTrigger
                .inLayer("Can Intake")
                .whenBecomesTrue(Intake.INSTANCE.takeIn())
                .whenBecomesFalse(Intake.INSTANCE.stop());

       driverGamepad
               .a()
               .whenBecomesTrue(new InstantCommand(()->PedroComponent.follower().setPose(new Pose(0,0,Math.toRadians(-90)))));

        driverControlled.schedule();
        limelight.start();
        limelight.pipelineSwitch(0);
        limelight.setPollRateHz(60);

        Carousel.INSTANCE.scanBalls().schedule();
    }

        



    @Override
    public void onUpdate() {
        if(!Carousel.INSTANCE.allHaveBalls()){
            blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_LAVA_PALETTE);
        } else{
            blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_OCEAN_PALETTE);
        }

//        double tagPos;
//        if(limelight.getLatestResult().isValid()){
//            tagPos = limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
//            telemetryM.debug("tsg pos x", tagPos);
//
//        } else{
//            tagPos = 0;
//        }
//        if (turnToTag && !isTurningToTag) {
//            isTurningToTag = true;
//
//            driverControlled.cancel();
//
//            new TurnBy(Angle.fromDeg(tagPos))
//                    .then(new InstantCommand(() -> {
//                        isTurningToTag = false;
//                        driverControlled.schedule();
//                    }))
//                    .schedule();
//        }


        telemetryM.debug("position", PedroComponent.follower().getPose());
        telemetryM.debug("velocity", PedroComponent.follower().getVelocity());
//      telemetryM.debug("automatedDrive", automatedDrive);
        telemetryM.debug("Dpad up/down for lifts");
        telemetryM.debug("Bumper left/right for launch carousel");
        telemetryM.debug("Dpad left/right for intake carousel");
        telemetryM.debug("Left trigger for intake");
        telemetryM.debug("Right trigger for launch one");
        telemetryM.debug("Y for launch all");
        telemetryM.debug("X for Purple B for Green");
        telemetryM.debug("A for toggle short launch which is currently ", shortLaunch);
        telemetryM.debug(Carousel.INSTANCE.getBallIndex());
        telemetryM.debug(Carousel.INSTANCE.getBalls());
        telemetryM.debug("Elevator pos:", Lifts.INSTANCE.tele());
        telemetryM.debug("Turning to tag:", turnToTag);
        telemetryM.update(telemetry);
        }

        @Override
        public void onStop() {
            TeleOp.startingPose = PedroComponent.follower().getPose();
        }
    }

