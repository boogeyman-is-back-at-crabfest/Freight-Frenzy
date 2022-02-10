/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * This is the HardwareMap package
 **/
package org.firstinspires.ftc.teamcode.HardwareMap;

/**
 * Imports physical hardware to manipulate
 **/

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This program is not an OpMode. Instead it initialize all the hardware including motors and servos and
 * provides various methods on how to get it to move and turn(A helper class)
 **/
public class ArtemisHardwareMap {
    private int levelVariable;
    private Telemetry telemetry;
    static final double MOTOR_TICK_COUNTS = 537.6;


    /**
     * These motors are the 4 mecanum wheels of our robot
     **/
    public DcMotorEx armMotor;
    public DcMotorEx topLeftDriveMotor;
    public DcMotorEx bottomLeftDriveMotor;
    public DcMotorEx topRightDriveMotor;
    public DcMotorEx bottomRightDriveMotor;

    public DcMotorEx carouselMotor;
    public CRServo intakeServo;

    private ElapsedTime runtime = new ElapsedTime();


    /**
     * DO NOT REMOVE. This hardware map will use the parent hardware map which contains all the names of the parts
     * in which we will use in this class to map and set methods for
     **/
    HardwareMap hwMap;

    /**
     * This method initializes all the motors and servos using the parent hardware map
     **/
    public void init(HardwareMap ahwMap) {

        /**
         * Assigns the parent hardware map to local ArtemisHardwareMap class variable
         * **/
        hwMap = ahwMap;

        /**
         * Hardware initialized and String Names are in the Configuration File for Hardware Map
         * **/
        armMotor = hwMap.get(DcMotorEx.class, "arm-Motor");
        topLeftDriveMotor = hwMap.get(DcMotorEx.class, "Top-Left-Motor");
        bottomLeftDriveMotor = hwMap.get(DcMotorEx.class, "Bottom-Left-Motor");
        topRightDriveMotor = hwMap.get(DcMotorEx.class, "Top-Right-Motor");
        bottomRightDriveMotor = hwMap.get(DcMotorEx.class, "Bottom-Right-Motor");
        intakeServo = hwMap.get(CRServo.class, "intake-Servo");

        carouselMotor = hwMap.get(DcMotorEx.class, "carousel-motor");




        topLeftDriveMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        topRightDriveMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        bottomLeftDriveMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        bottomLeftDriveMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        carouselMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        /**
         * Since we are putting the motors on different sides we need to reverse direction so that one wheel doesn't pull us backwards
         */
        topLeftDriveMotor.setDirection(DcMotorEx.Direction.REVERSE);
        bottomLeftDriveMotor.setDirection(DcMotorEx.Direction.REVERSE);
        topRightDriveMotor.setDirection(DcMotorEx.Direction.FORWARD);
        bottomRightDriveMotor.setDirection(DcMotorEx.Direction.FORWARD);



        /**
         * We are setting the motor 0 mode power to be brake as it actively stops the robot and doesn't rely on the surface to slow down once the robot power is set to 0
         */
        topRightDriveMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        topLeftDriveMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        bottomRightDriveMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        bottomLeftDriveMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        /**
         * The 4 mecanum wheel motors are set to 0 power to keep it from moving when the user presses the INIT button
         */
        topLeftDriveMotor.setPower(0);
        topRightDriveMotor.setPower(0);
        bottomLeftDriveMotor.setPower(0);
        bottomRightDriveMotor.setPower(0);
//        armMotor.setPower(0);
    }

    /**
     * This method will take in 3 inputs : The Left Stick X/Y and Right Stick X
     * - Left Stick Y will make the robot move forward and backwards ( Positive value will = forwwawrd and Negative value will = backwawrds)
     * - Left Stick X will allow the robot to strafe Left and Right ( Positive value makes topLeft and bottomRight motors run wwhich amkes the robot go right and the negative makes bottomLeft and topRight motors move which makes it go left)
     * - Right STick X allows the robot to turn left or right( Positive value will make the left motors turn more which will make the robot turn right and the negative values will make the right motors turn more which will make it turn left)
     **/
    public void moveRobot(double leftStickY, double leftStickX, double rightStickX){
        /**
         * Wheel powers calculated using gamepad 1's inputs leftStickY, leftStickX, and rightStickX
         * **/
        double topLeftPower = (leftStickY + leftStickX + rightStickX)*.6;
        double bottomLeftPower = (leftStickY - leftStickX + rightStickX)*.6;
        double topRightPower = (leftStickY - leftStickX - rightStickX)*.6;
        double bottomRightPower = (leftStickY + leftStickX - rightStickX)*.6;

        /**
         * Setting the wheel's power
         */

        topLeftDriveMotor.setPower(topLeftPower);
        topRightDriveMotor.setPower(topRightPower);
        bottomLeftDriveMotor.setPower(bottomLeftPower);
        bottomRightDriveMotor.setPower(bottomRightPower);
    }

//    public void moveArm(double leftStick){
//        double move = leftStick;
//        armMotor.setPower(move);
//
//
//    }

    /**
     * This autonomous move method allows the robot to move forwards and backwards like a tank drive and is controlled by a boolean isForwards
     **/
    public void autonomousMotorMove(double speed) {
        topLeftDriveMotor.setPower(speed);
        topRightDriveMotor.setPower(speed);
        bottomLeftDriveMotor.setPower(speed);
        bottomRightDriveMotor.setPower(speed);
    }

    public void autonomousTurnLeft(double speed) {
        topLeftDriveMotor.setPower(-speed);
        topRightDriveMotor.setPower(speed);
        bottomLeftDriveMotor.setPower(-speed);
        bottomRightDriveMotor.setPower(speed);
    }

    public void autonomousTurnRight(double speed) {
        topLeftDriveMotor.setPower(speed);
        topRightDriveMotor.setPower(-speed);
        bottomLeftDriveMotor.setPower(speed);
        bottomRightDriveMotor.setPower(-speed);
    }

    public void autonomousStrafeBackRight(double speed) {
        topLeftDriveMotor.setPower(0);
        topRightDriveMotor.setPower(speed);
        bottomLeftDriveMotor.setPower(speed);
        bottomRightDriveMotor.setPower(0);
    }

    public void autonomousStrafeBackLeft(double speed) {
        topLeftDriveMotor.setPower(speed);
        topRightDriveMotor.setPower(0);
        bottomLeftDriveMotor.setPower(0);
        bottomRightDriveMotor.setPower(speed);
    }

    /**
     * This autonomous strafe method allows the robot to strafe using mecanum wheels and the direction
     * and the direction is specified via a boolean variable
     **/
    public void autonomousMotorStrafe(boolean topLeft, boolean bottomLeft, boolean leftStrafe, boolean bottomRight) {
        if (topLeft) {
            topLeftDriveMotor.setPower(0);
            bottomLeftDriveMotor.setPower(0.5);
            topRightDriveMotor.setPower(0.5);
            bottomRightDriveMotor.setPower(0);
        } else if (bottomLeft) {
            topLeftDriveMotor.setPower(-0.5);
            bottomLeftDriveMotor.setPower(0);
            topRightDriveMotor.setPower(0);
            bottomRightDriveMotor.setPower(-0.5);
        } else if (leftStrafe) {
            topLeftDriveMotor.setPower(-0.5);
            bottomLeftDriveMotor.setPower(0.5);
            topRightDriveMotor.setPower(0.5);
            bottomRightDriveMotor.setPower(-0.5);
        } else if (bottomRight) {
            topLeftDriveMotor.setPower(0);
            bottomLeftDriveMotor.setPower(-0.5);
            topRightDriveMotor.setPower(-0.5);
            bottomRightDriveMotor.setPower(0);
        }
    }

    public void intake() {
        intakeServo.setPower(0.5);
    }
    public void outtake() {
        intakeServo.setPower(-0.5);
    }



    public void spinCarousel(double power) {
        carouselMotor.setPower(power);
    }




    public void moveLevelOne(Telemetry telemetry) {
        levelVariable = 1;
        runtime.reset();
        while (runtime.seconds() < 0.2) {
            armMotor.setPower(0.55);
        }
        armMotor.setPower(0);
        telemetry.addData("Level",1);
        runtime.reset();
    }
//
    public void moveLevelTwo(Telemetry telemetry) {
        levelVariable = 2;
        runtime.reset();
        while (runtime.seconds() < 0.2) {
            armMotor.setPower(0.83);
        }
        armMotor.setPower(0);
        telemetry.addData("Level",2);
        runtime.reset();

    }

    public void moveLevelThree(Telemetry telemetry) {
        levelVariable = 3;
        runtime.reset();
        while (runtime.seconds() < 0.3) {
            armMotor.setPower(0.9);
            // 1 works very well probably even better than 0.9
        }
        armMotor.setPower(0);
        telemetry.addData("Level",3);
        runtime.reset();

    }

    public void moveArmDown(Telemetry telemetry) {
        telemetry.addData("Lowering from ",levelVariable);
        runtime.reset();
        if(levelVariable == 1){
            runtime.reset();
            while (runtime.seconds() < 0.2) {
                armMotor.setPower(-0.4);
            }
            armMotor.setPower(0);
            runtime.reset();

        } else if(levelVariable == 2){
            runtime.reset();
            while (runtime.seconds() < 0.2) {
                armMotor.setPower(-0.7);
            }
            armMotor.setPower(0);
            runtime.reset();

        } else if(levelVariable == 3){
            runtime.reset();
            while (runtime.seconds() < 0.2) {
                armMotor.setPower(-0.9);
                // 1 works very well probably even better than 0.9
            }
            armMotor.setPower(0);
            runtime.reset();

        }
    }

    public void spinCarouselRed(Telemetry telemetry) {
        carouselMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        double circumfrence = 3.14 * 1;
        double rotationsNeeded = 23 / circumfrence;
        int encoderDrivingTarget = (int) (rotationsNeeded * MOTOR_TICK_COUNTS);
        carouselMotor.setTargetPosition(-encoderDrivingTarget);
        carouselMotor.setPower(1);
        carouselMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (carouselMotor.isBusy()) {
            telemetry.addData("Encoder Motor", "Driving 18 inches");
            telemetry.update();
        }
        carouselMotor.setPower(0);
    }
    public void spinCarouselBlue(Telemetry telemetry) {
        carouselMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        double circumfrence = 3.14 * 1;
        double rotationsNeeded = 23 / circumfrence;
        int encoderDrivingTarget = (int) (rotationsNeeded * MOTOR_TICK_COUNTS);
        carouselMotor.setTargetPosition(encoderDrivingTarget);
        carouselMotor.setPower(1);
        carouselMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (carouselMotor.isBusy()) {
            telemetry.addData("Encoder Motor", "Moving Carousel");
            telemetry.update();
        }
        carouselMotor.setPower(0);
    }

    public void moveArmUpwardsTest(Telemetry telemetry){
        armMotor.setPower(-.5);
        telemetry.addData("work?",-.5);
//


    }
    public void moveArmReverseTest(Telemetry telemetry){
        runtime.reset();
        while (runtime.seconds() < 0.2) {
            armMotor.setPower(0.2);
        }
        armMotor.setPower(0);
        runtime.reset();
//


    }


}

