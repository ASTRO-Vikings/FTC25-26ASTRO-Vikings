package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@Autonomous(name = "Auto")
public class AutoTest extends NextFTCOpMode {
    public AutoTest(){
        addComponents(
                new SubsystemComponent(Carousel.INSTANCE),
                BulkReadComponent.INSTANCE
        );
    }

    public Command autonomousRoutine(){
        return new SequentialGroup(
                Carousel.INSTANCE.toLow,
                new Delay(.5),
                Carousel.INSTANCE.toHigh
        );
    }

    @Override
    public void onStartButtonPressed(){
    autonomousRoutine().schedule();
    }
}
