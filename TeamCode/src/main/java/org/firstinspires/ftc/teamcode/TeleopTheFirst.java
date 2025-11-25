package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Carousel;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Lifts;

import java.util.function.Supplier;

import dev.nextftc.bindings.Button;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;


@Configurable
@TeleOp(name = "Teleop")
public class TeleopTheFirst extends NextFTCOpMode {
    public TeleopTheFirst(){
        addComponents(
                new SubsystemComponent(Carousel.INSTANCE),
                new SubsystemComponent(Flywheel.INSTANCE),
                new SubsystemComponent(Lifts.INSTANCE),
                new SubsystemComponent(Intake.INSTANCE),
                new SubsystemComponent(Elevator.INSTANCE),
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

    @Override
    public void onInit() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

//      Path to follow during teleop
//        pathChain = () -> follower.pathBuilder() //Lazy Curve Generation
//                .addPath(new Path(new BezierLine(follower::getPose, new Pose(45, 98))))
//                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, Math.toRadians(45), 0.8))
//                .build();
    }

    @Override
    public void onStartButtonPressed() {
        //Carousel
        Gamepads.gamepad1().dpadLeft()
                .whenBecomesTrue(Carousel.INSTANCE.toLow);
        Gamepads.gamepad1().dpadRight()
                .whenBecomesTrue(Carousel.INSTANCE.toHigh);
        //Flywheel
        Button leftTrigger = Gamepads.gamepad1().leftTrigger().greaterThan(.1);

        leftTrigger
                .whenBecomesTrue(Flywheel.INSTANCE.on)
                .whenBecomesFalse(Flywheel.INSTANCE.off);
        //Lifts
        Gamepads.gamepad1().dpadUp()
                .whenBecomesTrue(Lifts.INSTANCE.toHigh)
        ;
        Gamepads.gamepad1().dpadDown()
                .whenBecomesTrue(Lifts.INSTANCE.toLow);

        //Elevator
        Gamepads.gamepad1().a()
                .whenBecomesTrue(Elevator.INSTANCE.toHigh)
                .whenBecomesFalse(Elevator.INSTANCE.toLow);

        //Intake
        Gamepads.gamepad1().b()
                .whenBecomesTrue(Intake.INSTANCE.takeIn)
                .whenBecomesFalse(Intake.INSTANCE.takeOut);
    }

    @Override
    public void onUpdate(){
        follower.update();
        telemetryM.update();

        //If not following path allow driving
        if (!automatedDrive) {
            if (!slowMode) follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    isRobotCentric
            );
            else follower.setTeleOpDrive(
                    -gamepad1.left_stick_y * slowModeMultiplier,
                    -gamepad1.left_stick_x * slowModeMultiplier,
                    -gamepad1.right_stick_x * slowModeMultiplier,
                    isRobotCentric
            );
        }
//        Follow path
//        if (gamepad1.aWasPressed()) {
//            follower.followPath(pathChain.get());
//            automatedDrive = true;
//        }
//        if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
//            follower.startTeleopDrive();
//            automatedDrive = false;
//        }

        //Toggle slowmode
        if (gamepad1.rightBumperWasPressed()) {
            slowMode = !slowMode;
        }

        telemetryM.debug("position", follower.getPose());
        telemetryM.debug("velocity", follower.getVelocity());
        telemetryM.debug("automatedDrive", automatedDrive);
        telemetryM.debug("Dpad up/down for lift");
        telemetryM.debug("Dpad left/right for carousel");
        telemetryM.debug("Left trigger for flywheels");
        telemetryM.debug("A for elevator");
        telemetryM.debug("B for intake");
    }
}