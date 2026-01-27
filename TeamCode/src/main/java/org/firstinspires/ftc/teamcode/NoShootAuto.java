package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "No Shoot Auto", preselectTeleOp = "TeleOp")
public class NoShootAuto extends LinearOpMode {
    private TelemetryManager telemetryM;

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft ;
    DcMotor backRight;
    DcMotor leftLaunch;
    DcMotor rightLaunch;
    DcMotor carousel;
    Servo elevator;

    private final double COUNTS_PER_360_DEGREES = 2786.2;
    private final double TICKS_PER_DEGREE = COUNTS_PER_360_DEGREES / 360.0;
    private  final int offset = 0;
    public final int POSITION_LEFT_MIDDLE = (int)((60 + offset)*TICKS_PER_DEGREE);
    public final int POSITION_RIGHT_MIDDLE = (int)((180 + offset)*TICKS_PER_DEGREE);
    public final int POSITION_LEFT_RIGHT = (int)((300 + offset)*TICKS_PER_DEGREE);
    public boolean goLeft = false;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        leftLaunch = hardwareMap.get(DcMotor.class,"launcherLeft");
        rightLaunch = hardwareMap.get(DcMotor.class,"launcherRight");

        carousel = hardwareMap.get(DcMotor.class,"carousel");

        elevator = hardwareMap.get(Servo.class, "elevator");

        while(!isStarted()) {
            if (gamepad1.aWasPressed()) {
                goLeft = !goLeft;
            }
            telemetryM.debug("Going left: " + goLeft);
            telemetryM.update(telemetry);
        }
        waitForStart();
//
//        carousel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        carousel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        carousel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        carousel.setTargetPosition(POSITION_LEFT_MIDDLE);
//        carousel.setPower(1);
//
//        leftLaunch.setPower(.65);
//        rightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
//        rightLaunch.setPower(.65);
//
//        Thread.sleep(250);
//
//        elevator.setPosition(0);
//
//        Thread.sleep(1000);

//        elevator.setPosition(1);
//
//        Thread.sleep(400);
//
//        carousel.setTargetPosition(POSITION_LEFT_RIGHT);
//
//        Thread.sleep(500);
//
//        elevator.setPosition(0);
//
//        Thread.sleep(1000);
//
//        elevator.setPosition(1);
//
//        Thread.sleep(400);
//
//        carousel.setTargetPosition(POSITION_RIGHT_MIDDLE);
//
//        Thread.sleep(500);
//
//        elevator.setPosition(0);
//
//        Thread.sleep(1000);
//
//        elevator.setPosition(1);

//        Thread.sleep(400);
//
//        carousel.setTargetPosition(0);

        frontLeft .setPower(goLeft ? -1 :  1);
        backLeft  .setPower(goLeft ?  1 : -1);
        frontRight.setPower(goLeft ?  1 : -1);
        backRight .setPower(goLeft ? -1 :  1);

        Thread.sleep(500);
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        leftLaunch.setPower(0);
        rightLaunch.setPower(0);
        carousel.setPower(0);
    }
}
