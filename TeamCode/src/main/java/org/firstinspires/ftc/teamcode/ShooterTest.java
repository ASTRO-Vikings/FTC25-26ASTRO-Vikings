package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Configurable
@TeleOp(name = "ShooterTest")
public class ShooterTest extends OpMode {

    private DcMotor one;
    private DcMotor zero;
    public static double speed = .1;


    @Override
    public void init() {
        one = hardwareMap.get(DcMotor.class, "one");
        zero = hardwareMap.get(DcMotor.class, "zero");

    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            one.setPower(speed);
            zero.setPower(speed);
        } else {
            one.setPower(0);
            zero.setPower(0);
        }
        telemetry.update();
    }
}