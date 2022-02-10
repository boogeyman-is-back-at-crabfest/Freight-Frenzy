package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Imports the Artemis Hardware Map Class which initializes all the hardware once the user presses the INIT button
 * **/
import org.firstinspires.ftc.teamcode.HardwareMap.ArtemisHardwareMap;

/**
 * The name attribute specifies the OpMode name on the robot controller and group attribute specifies the group
 * in which that OpMode is located
 **/

/**
 * This class uses the controller inputs such as joysticks and triggers and dynamically calls various methods in the
 * (imported) Hardware map class once these inputs are called
 **/
@TeleOp(name = "Artemis TeleOp")
public class ArtemisTeleOp extends OpMode {


    /**
     * The hardware map initialization object which initializes all our motors and servos
     **/
    // this is just a test (2/9/22 by Rishit)
    ArtemisHardwareMap hardwareMapInitialize = new ArtemisHardwareMap();

    /**
     * This is called ONCE when the driver presses the init button
     **/
    @Override
    public void init() {
        telemetry.addData("Robot Initialized Successfully in TeleOp", " Wait for hardware to initialize");

        hardwareMapInitialize.init(hardwareMap);

        telemetry.addData("Robot Hardware Initialized Successfully in TeleOp", "Press Play to Start");
    }

    /***
     * This is called ONCE when the driver presses the play button
     * */
    @Override
    public void start() {
        telemetry.addData("Robot in Play Mode in TeleOp", "Control the Robot Now");
    }

    /**
     * This is called MULTIPLE TIMES when the driver presses the play button
     **/
    @Override
    public void loop() {
        /**
         * Gamepad inputs for moving and turning the robot
         * **/
        double leftStickY = -gamepad1.left_stick_y;
        double leftStickX = -gamepad1.left_stick_x * 1.5;
        double rightStickX = gamepad1.right_stick_x;
        if (leftStickY > 0) {
            telemetry.addData("Moving", "Forwards");
        } else if (leftStickY < 0) {
            telemetry.addData("Moving", "Backwards");
        } else {
            telemetry.addData("Moving", "Not Moving");
        }
        telemetry.addData("Top Left Power ", leftStickY + leftStickX + rightStickX);
        telemetry.addData("Bottom Left Power ", leftStickY + leftStickX + rightStickX);
        telemetry.addData("Top Right Power ", leftStickY + leftStickX - rightStickX);
        telemetry.addData("Bottom Right Power ", leftStickY + leftStickX - rightStickX);
        hardwareMapInitialize.moveRobot(leftStickY, leftStickX, rightStickX);


        boolean levelOne = gamepad2.dpad_down;
        boolean levelTwo = gamepad2.dpad_right;
        boolean levelThree = gamepad2.dpad_up;
        boolean defaultPos = gamepad2.dpad_left;


        if (levelOne) {
            hardwareMapInitialize.moveLevelOne(telemetry);
        }
        else if (levelTwo) {
            hardwareMapInitialize.moveLevelTwo(telemetry);
        } else if (levelThree) {
            hardwareMapInitialize.moveLevelThree(telemetry);
        } else if(defaultPos){
            hardwareMapInitialize.moveArmDown(telemetry);
        }



        if (gamepad2.left_bumper) {
            hardwareMapInitialize.spinCarouselBlue(telemetry);
        }
        if (gamepad2.right_bumper) {
            hardwareMapInitialize.spinCarouselRed(telemetry);
        }
        if (gamepad2.y) {
            hardwareMapInitialize.intake();
        }
        if (gamepad2.b) {
            hardwareMapInitialize.outtake();
        }
        hardwareMapInitialize.carouselMotor.setPower(0);

        if (gamepad2.a) {
            hardwareMapInitialize.moveArmUpwardsTest(telemetry);
        }
        hardwareMapInitialize.armMotor.setPower(0);

        if (gamepad2.x) {
            hardwareMapInitialize.moveArmReverseTest(telemetry);
        }
        hardwareMapInitialize.armMotor.setPower(0);


    }


    /**
     * This is called ONCE when the driver presses the stop button and logs that the robot has stopped
     **/
    @Override
    public void stop() {
        telemetry.addData("Robot has Stopped and Wont Move", "Controller Inputs Now Disabled");
    }
}
