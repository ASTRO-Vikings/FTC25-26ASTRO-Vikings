package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Configurable
@TeleOp(name = "Carousel Test")
public class CarouselTest extends OpMode {

    private DcMotor one;
    public static int rpm = 15;
    private final double maxRPM = 60;

    @Override
    public void init() {
        one = hardwareMap.get(DcMotor.class, "one");
    }

    @Override
    public void loop() {
        double rpmToPower = rpm/maxRPM;
        if (gamepad1.a) {
            one.setPower(rpmToPower);
            telemetry.addLine("controller pressing");
        } else {
            one.setPower(0);
        }

        telemetry.addData("Current Power: ", rpmToPower);
        telemetry.update();
    }
}