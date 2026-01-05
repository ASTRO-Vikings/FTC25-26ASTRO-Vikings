package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.subsystems.Carousel;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous")
public class Autonomous extends NextFTCOpMode {
    public Autonomous(){
        addComponents(
                new SubsystemComponent(Carousel.INSTANCE),
                BulkReadComponent.INSTANCE
        );
    }

    public Command autonomousRoutine(){
        return new SequentialGroup(
//                Carousel.INSTANCE.moveToLeft(),
//                new Delay(.5),
//                Carousel.INSTANCE.moveToRight()
        );
    }

    @Override
    public void onStartButtonPressed(){
    autonomousRoutine().schedule();
    }
}
