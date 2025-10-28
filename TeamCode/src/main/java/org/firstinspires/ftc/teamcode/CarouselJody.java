package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Configurable
@TeleOp(name = "Carousel Test Jody")
public class CarouselJody extends OpMode {

    private DcMotor turntable;
    public static int rpm = 20;
    private final double maxRPM = 60;
    private int rotAmt = 920;

    @Override
    public void init() {
        turntable = hardwareMap.get(DcMotor.class, "turntable");
        turntable.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void start(){
        turntable.setTargetPosition(0);
        turntable.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    @Override
    public void loop() {
        double rpmToPower = rpm/maxRPM;
        if (gamepad1.aWasPressed()) {
            turntable.setPower(rpmToPower);
            turntable.setTargetPosition(turntable.getTargetPosition()-(rotAmt/3));
            telemetry.addLine("controller pressing");
        } else if (gamepad1.b) {
            turntable.setPower(-rpmToPower);
        }
        else {
            turntable.setPower(rpmToPower);
        }
        if (gamepad1.xWasPressed()){
            turntable.setTargetPosition(turntable.getTargetPosition() + rotAmt);
        }
        telemetry.addData("Current Postion: ", turntable.getCurrentPosition());
        telemetry.addData("Current Power: ", rpmToPower);
        telemetry.update();
    }
}