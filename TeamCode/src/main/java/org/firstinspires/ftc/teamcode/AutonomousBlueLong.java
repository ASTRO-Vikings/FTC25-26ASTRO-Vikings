package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Carousel;
import org.firstinspires.ftc.teamcode.subsystems.LaunchGroup;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous Blue")
public class AutonomousBlueLong extends NextFTCOpMode {
    public AutonomousBlueLong(){
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(Carousel.INSTANCE),
                new SubsystemComponent(LaunchGroup.INSTANCE),
                new PedroComponent(Constants::createFollower)
        );
    }
    Pose startPose = new Pose(48.000, 8.000,Math.toRadians(90));

    Pose shootPose = new Pose(58.000, 18.000,Math.toRadians(140));
    Pose endPose = new Pose(48.000, 28.000);

    PathChain pathchainStartToShoot;
    PathChain pathchainShootToEnd;

    LLResultTypes.FiducialResult tag;
    Limelight3A limelight;
    boolean launchPurple = false;
    private TelemetryManager telemetryM;

    @Override
    public void onInit() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        Carousel.INSTANCE.evilInit(hardwareMap);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.start();
        limelight.pipelineSwitch(0);
        limelight.setPollRateHz(60);



    }

    String pattern = "pgp";

    @Override
    public void onStartButtonPressed(){
        Carousel.INSTANCE.initialize();
        if(limelight.getLatestResult().isValid()){
            tag = limelight.getLatestResult().getFiducialResults().get(0);
        } else{
            tag = null;
        }
        if (tag != null){
            switch (tag.getFiducialId()){
                case 21:
                    pattern = "gpp";
                    break;
                case 23:
                    pattern = "ppg";
                    break;
                default:
                    pattern = "pgp";
                    break;
            }
        }

        PedroComponent.follower().setPose(startPose);
        pathchainStartToShoot = PedroComponent.follower()
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                startPose,
                                new Pose(31.000, 20.000),
                                shootPose
                        )
                )
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();

        pathchainShootToEnd = PedroComponent.follower()
                .pathBuilder()
                .addPath(
                        new BezierLine(
                                shootPose,
                                endPose
                        )
                ).build();
        Command autonomousRoutine;

        autonomousRoutine = new SequentialGroup(
                Carousel.INSTANCE.scanBalls(),
                new Delay(1),
                new FollowPath(pathchainStartToShoot),
                Carousel.INSTANCE.launchMoveToRight(true),
                new Delay(0.25),
                launchMotif(),
                new Delay(2),
                new FollowPath(pathchainShootToEnd)
        );
        autonomousRoutine.schedule();
    }

    private SequentialGroup launchMotif(){
        switch (pattern){
            case "ppg":
                return new SequentialGroup(
                        new Delay(0.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(false).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(false).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchGreen(false).schedule())
                        );
            case "pgp":
                return new SequentialGroup(
                        new Delay(0.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(false).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchGreen(false).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(false).schedule())
                );
            default:
                return new SequentialGroup(
                        new Delay(0.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchGreen(false).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(false).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(false).schedule())
                );
        }
    }
    @Override
    public void onUpdate(){
        telemetryM.debug("Pos:", PedroComponent.follower().getPose());
        telemetryM.debug("Pattern:", pattern);
        telemetryM.debug(Carousel.INSTANCE.getBallIndex());
        telemetryM.debug(Carousel.INSTANCE.getBalls());
        telemetryM.update(telemetry);
    }

    @Override
    public void onStop() {
        TeleOp.startingPose = PedroComponent.follower().getPose();
    }
}
