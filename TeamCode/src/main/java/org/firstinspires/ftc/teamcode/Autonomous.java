package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.Carousel;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous")
public class Autonomous extends NextFTCOpMode {

    MotorEx frontLeft = new MotorEx("frontLeft");
    MotorEx frontRight = new MotorEx("frontRight").reversed();
    MotorEx backLeft = new MotorEx("backLeft");
    MotorEx backRight = new MotorEx("backRight").reversed();
    public Autonomous(){
        addComponents(
                new SubsystemComponent(Carousel.INSTANCE),
                BulkReadComponent.INSTANCE
        );
    }

    public Command autonomousRoutine() {

        return new SequentialGroup(
                new ParallelGroup(
                        new SetPower(frontLeft, 1),
                        new SetPower(backLeft, 1),
                        new SetPower(frontRight, 1),
                        new SetPower(backRight, 1)
                ),
                new Delay(1),
                new ParallelGroup(
                        new SetPower(frontLeft, 0),
                        new SetPower(backLeft, 0),
                        new SetPower(frontRight, 0),
                        new SetPower(backRight, 0)
                )
        );
    }

    @Override
    public void onStartButtonPressed(){
        autonomousRoutine().schedule();
    }
}
