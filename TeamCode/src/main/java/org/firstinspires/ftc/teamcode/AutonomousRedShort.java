package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
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

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous Red Short")
public class AutonomousRedShort extends NextFTCOpMode {
    public AutonomousRedShort(){
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(Carousel.INSTANCE),
                new SubsystemComponent(LaunchGroup.INSTANCE),
                new PedroComponent(Constants::createFollower)
        );
    }
    Pose startPose = new Pose(129, 130,Math.toRadians(45));
    Pose scanPose = new Pose(120, 120,Math.toRadians(135));

    Pose shootPose = new Pose(110, 107,Math.toRadians(45));
    Pose endPose = new Pose(96,120);

//    PathChain pathchainStartToScan;
//    PathChain pathchainScanToShoot;
//    PathChain pathchainShootToEnd;
public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;

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

            Path1 = PedroComponent.follower()
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(129.000, 130.000), new Pose(120.000, 120.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(135))
                    .build();

            Path2 = PedroComponent.follower()
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(120.000, 120.000), new Pose(110.000, 107.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(55))
                    .build();

            Path3 = PedroComponent.follower()
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(110.000, 107.000), new Pose(96.000, 120.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(55), Math.toRadians(90))
                    .build();

//        pathchainScanToShoot = PedroComponent.follower()
//                .pathBuilder()
//                .addPath(
//                        new BezierCurve(
//                                scanPose,
//                                new Pose(115,115),
//                                shootPose
//                        )
//                )
//                .setLinearHeadingInterpolation((Math.toRadians(135)),(Math.toRadians(45)))
//                .build();
//        pathchainStartToScan = PedroComponent.follower()
//                .pathBuilder()
//                .addPath(
//                        new BezierCurve(
//                                startPose,
//                                new Pose(125, 125),
//                                scanPose
//                        )
//                )
//                .setLinearHeadingInterpolation(Math.toRadians(45),Math.toRadians(135))
//                .build();
//
//        pathchainShootToEnd = PedroComponent.follower()
//                .pathBuilder()
//                .addPath(
//                        new BezierCurve(
//                                shootPose,
//                                new Pose(100,115),
//                                endPose
//                        )
//                ).build();
        Command autonomousRoutine;

        autonomousRoutine = new SequentialGroup(
                Carousel.INSTANCE.scanBalls(),
                new Delay(1),
                new FollowPath(Path1),
                new Delay(0.5),
                new InstantCommand(()->{
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
                }),
                new Delay(0.5),
                new FollowPath(Path2),
                Carousel.INSTANCE.launchMoveToRight(true),
                new Delay(0.25),
                launchMotif(),
                new Delay(2),
                new FollowPath(Path3)
        );
        autonomousRoutine.schedule();
    }

    private SequentialGroup launchMotif(){
        switch (pattern){
            case "ppg":
                return new SequentialGroup(
                        new Delay(0.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(true).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(true).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchGreen(true).schedule())
                        );
            case "pgp":
                return new SequentialGroup(
                        new Delay(0.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(true).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchGreen(true).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(true).schedule())
                );
            default:
                return new SequentialGroup(
                        new Delay(0.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchGreen(true).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(true).schedule()),
                        new Delay(2.5),
                        new InstantCommand(()->LaunchGroup.INSTANCE.launchPurple(true).schedule())
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
        TeleOpRed.startingPose = PedroComponent.follower().getPose();
    }
}
