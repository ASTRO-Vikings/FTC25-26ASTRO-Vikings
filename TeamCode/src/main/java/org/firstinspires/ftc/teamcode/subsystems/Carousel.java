package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.util.Hashtable;

import javax.net.ssl.SSLEngineResult;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

public class Carousel implements Subsystem {
    public static final Carousel INSTANCE = new Carousel();
    private final double COUNTS_PER_360_DEGREES = 2786.2;
    private final double TICKS_PER_DEGREE = COUNTS_PER_360_DEGREES / 360.0;
    final int offset = 60;
    public final int POSITION_LEFT = 0 + offset;
    public final int POSITION_LEFT_MIDDLE = 60 + offset;
    public final int POSITION_MIDDLE = 120 + offset;
    public final int POSITION_RIGHT_MIDDLE = 180 + offset;
    public final int POSITION_RIGHT = 240 + offset;
    public final int POSITION_LEFT_RIGHT = 300 + offset;
    private HardwareMap hardwareMap;
    boolean hasBeenInit = false;

    private Carousel() {}
    public void evilInit(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
    }
    private final MotorEx motor = new MotorEx("carousel").zeroed();
    private final ControlSystem controlSystem = ControlSystem.builder()
            .angular(AngleType.DEGREES,
                    feedback -> feedback.posPid(0.04, 0, 0.001))
            .basicFF(0.05, 0, 0)
            .build();

    // Enum to keep track of the current logical state
    public enum CarouselState {
        LEFT, LEFT_MIDDLE, MIDDLE, RIGHT_MIDDLE, RIGHT, LEFT_RIGHT
    }
    public enum BallState {
        NO_BALL, GREEN_BALL, PURPLE_BALL
    }

    private BallState[] ballStates = {BallState.NO_BALL, BallState.NO_BALL, BallState.NO_BALL};


    public String teleStr = "telemetry works";


    // Track the current desired state
    public CarouselState currentState = CarouselState.LEFT;
    private int currentBallIndex = 0;
    private int currentMotifIndex = 0;
    public void incrementMotifIndex(){
        currentMotifIndex++;
        if (currentMotifIndex >= 3) {currentMotifIndex = 0;}
    }
    private NormalizedColorSensor colorSensor;
    Limelight3A limelight;

    public void addBall(){
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        double red = colors.red;
        double blue = colors.blue;
        double green = colors.green;
        teleStr = "Red: " + colors.red + "Blue: " + colors.blue + "Green: " + colors.green;
        if(blue > green * 1.2){
            ballStates[currentBallIndex] = BallState.PURPLE_BALL;
        } else if(green > blue * 1.2){
            ballStates[currentBallIndex] = BallState.GREEN_BALL;
        } else {
            ballStates[currentBallIndex] = BallState.NO_BALL;
        }
    }


    public void removeBall(){
        ballStates[currentBallIndex] = BallState.NO_BALL;
    }
    public Command scanBalls(){
        return new SequentialGroup(
                new InstantCommand(()->{addBall();}),
                new Delay(0.5),
                intakeMoveToLeft(),
                new Delay(0.5),
                new InstantCommand(()->{addBall();}),
                new Delay(0.5),
                intakeMoveToLeft(),
                new Delay(0.5),
                new InstantCommand(()->{addBall();}),
                new Delay(0.5),
                intakeMoveToLeft(),
                new Delay(0.5),
                new InstantCommand(()->{addBall();})
        );
    }

    public boolean hasBall(BallState state, boolean isGeneric){
        if (isGeneric){
            teleStr=("Generic check");
            return ballStates[currentBallIndex] != BallState.NO_BALL;
        }
        teleStr=(state.name() + " check");
        return ballStates[currentBallIndex] == state;
    }
    public boolean nextHasBall(BallState state, boolean isGeneric){
        if (isGeneric){
            teleStr=("Generic check");
            return ballStates[(currentBallIndex+1)%3] != BallState.NO_BALL;
        }
        teleStr=(state.name() + " check");
        return ballStates[(currentBallIndex+1)%3] == state;
    }
    public boolean lastHasBall(BallState state, boolean isGeneric){
        if (isGeneric){
            teleStr=("Generic check");
            return ballStates[(currentBallIndex+2)%3] != BallState.NO_BALL;
        }
        teleStr=(state.name() + " check");
        return ballStates[(currentBallIndex+2)%3] == state;
    }
    public String getTelemetryStr(){
        return teleStr;
    }

    @Override
    public void initialize() {
        currentState = CarouselState.LEFT;
        motor.setCurrentPosition(0);
        controlSystem.setGoal(new KineticState(0));
        ballStates[0] = BallState.NO_BALL;
        ballStates[1] = BallState.NO_BALL;
        ballStates[2] = BallState.NO_BALL;
    }



    @Override
    public void periodic() {
        if (!hasBeenInit) {
            colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");
//            limelight = hardwareMap.get(Limelight3A .class, "limelight");
//            limelight.setPollRateHz(100);
//            limelight.start();
//            limelight.pipelineSwitch(0);
            hasBeenInit = true;
//            LLResult result = limelight.getLatestResult();
//            result.getFiducialResults().get(0).getFiducialId();
        }

        KineticState motorState = motor.getState();
        double currentAngleDegrees = (motorState.getPosition() / TICKS_PER_DEGREE) % 360.0;
        if (currentAngleDegrees < 0) {
            currentAngleDegrees += 360.0;
        }
        KineticState wrappedState = new KineticState(currentAngleDegrees, motorState.getVelocity() / TICKS_PER_DEGREE);

        motor.setPower(controlSystem.calculate(wrappedState));
    }

    public Command intakeMoveToRight() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.RIGHT;
                    break;
                    
                case LEFT_RIGHT:
                    currentState = CarouselState.RIGHT;
                    break;

                case RIGHT:
                    currentState = CarouselState.MIDDLE;
                    break;

                case RIGHT_MIDDLE:
                    currentState = CarouselState.MIDDLE;
                    break;

                case MIDDLE:
                    currentState = CarouselState.LEFT;
                    break;

                case LEFT_MIDDLE:
                    currentState = CarouselState.LEFT;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }

    public Command intakeMoveToLeft() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.MIDDLE;
                    break;

                case LEFT_RIGHT:
                    currentState = CarouselState.LEFT;
                    break;

                case RIGHT:
                    currentState = CarouselState.LEFT;
                    break;

                case RIGHT_MIDDLE:
                    currentState = CarouselState.RIGHT;
                    break;

                case MIDDLE:
                    currentState = CarouselState.RIGHT;
                    break;

                case LEFT_MIDDLE:
                    currentState = CarouselState.MIDDLE;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }
    public Command launchMoveToLeft() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.LEFT_MIDDLE;
                    
                    break;
                case LEFT_RIGHT:
                    
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case RIGHT:
                    
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
                case RIGHT_MIDDLE:
                    
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
                case MIDDLE:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    
                    break;
                case LEFT_MIDDLE:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }
    public Command launchMoveToRight() {
        return new InstantCommand(() -> {
            switch (currentState) {
                case LEFT:
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case LEFT_RIGHT:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    break;
                case RIGHT:
                    
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
                case RIGHT_MIDDLE:
                    currentState = CarouselState.LEFT_MIDDLE;
                    break;
                case MIDDLE:
                    currentState = CarouselState.RIGHT_MIDDLE;
                    break;
                case LEFT_MIDDLE:
                    
                    currentState = CarouselState.LEFT_RIGHT;
                    break;
            }
            setGoalForCurrentState();
        }).requires(this);
    }

    private void setGoalForCurrentState() {
        int targetPos;
        switch (currentState) {
            case LEFT:
                targetPos = POSITION_LEFT;
                currentBallIndex = 0;
                break;
            case LEFT_RIGHT:
                targetPos = POSITION_LEFT_RIGHT;
                currentBallIndex = 2;
                break;
            case RIGHT:
                targetPos = POSITION_RIGHT;
                currentBallIndex = 1;
                break;
            case RIGHT_MIDDLE:
                targetPos = POSITION_RIGHT_MIDDLE;
                currentBallIndex = 0;
                break;
            case MIDDLE:
                targetPos = POSITION_MIDDLE;
                currentBallIndex = 2;
                break;
            case LEFT_MIDDLE:
                targetPos = POSITION_LEFT_MIDDLE;
                currentBallIndex = 1;
                break;
            default:
                targetPos = POSITION_LEFT;
                currentBallIndex = 0;
        }
        KineticState newGoal = new KineticState(targetPos);
        controlSystem.setGoal(newGoal);
    }
    public String getBallIndex(){
        return String.format("Current Ball index: %d",currentBallIndex);
    }
    public String getBalls(){
        return String.format("Balls are 0: %s 1: %s 2: %s", ballStates[0], ballStates[1],ballStates[2]);
    }
}