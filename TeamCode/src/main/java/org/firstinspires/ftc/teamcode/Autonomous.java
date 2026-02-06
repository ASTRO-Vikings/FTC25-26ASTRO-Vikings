package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Carousel;
import org.firstinspires.ftc.teamcode.subsystems.LaunchGroup;

import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.units.Angle;
import dev.nextftc.extensions.pedro.TurnBy;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous")
public class Autonomous extends NextFTCOpMode {
    public Autonomous(){
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(Carousel.INSTANCE),
                new SubsystemComponent(LaunchGroup.INSTANCE),
                new PedroComponent(Constants::createFollower)
        );
    }
    Pose startPose = new Pose(96,8,Angle.fromDeg(90).inRad);
    Pose shootPose = new Pose(84,16,Angle.fromDeg(60).inRad);

    PathChain pathchain;

    LLResultTypes.FiducialResult tag;
    Limelight3A limelight;
    boolean launchPurple = false;
    @Override
    public void onInit() {
        Carousel.INSTANCE.evilInit(hardwareMap);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.start();
        limelight.pipelineSwitch(0);
        limelight.setPollRateHz(60);



    }

    @Override
    public void onStartButtonPressed(){
        if(limelight.getLatestResult().isValid()){
            tag = limelight.getLatestResult().getFiducialResults().get(0);
        } else{
            tag = null;
        }
        if (tag != null){
            if(tag.getFiducialId() == 24){
                launchPurple = true;
            } else{
                launchPurple = false;
            }
        }
        PedroComponent.follower().setPose(startPose);
        pathchain = PedroComponent.follower()
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(96.000, 8.000),
                                new Pose(113.000, 20.000),
                                new Pose(84.000, 16.000)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(60))
                .build();
        Command autonomousRoutine;

        autonomousRoutine = new SequentialGroup(
                Carousel.INSTANCE.scanBalls(),
                new Delay(1),
                new FollowPath(pathchain),
                Carousel.INSTANCE.launchMoveToRight(),
                LaunchGroup.INSTANCE.launchPurple(false)
        );


        autonomousRoutine.schedule();
    }

    @Override
    public void onStop() {
        TeleOp.startingPose = PedroComponent.follower().getPose();
    }
}
