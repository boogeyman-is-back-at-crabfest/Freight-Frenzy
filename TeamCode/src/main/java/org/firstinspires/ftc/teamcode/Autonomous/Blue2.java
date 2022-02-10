/* Copyright (c) 2020 FIRST. All rights reserved.
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


package org.firstinspires.ftc.teamcode.Autonomous;


/**
 * Imports OpMode class and the Autonomous declaration and Elapsed Time
 **/

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * Imports the list collection from Java
 * **/
import java.util.List;
import java.util.Random;

/**
 * Imports Vuforia libraries
 * **/
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;


/**
 * Imports the tensorflow lite object detector and object recognition
 * **/

/**
 * Imports the Artemis Hardware Map Class which initializes all the hardware once the user presses the INIT button
 * **/
import org.firstinspires.ftc.teamcode.HardwareMap.ArtemisHardwareMap;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.drive.opmode.ManualFeedforwardTuner;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

/**
 * The name attribute specifies the OpMode name on the robot controller and group attribute specifies the group
 * in which that OpMode is located
 **/

/**
 * This class uses Tensorflow lite along with the webcam to dynamically choose wheter to score points via wobble goal
 * or shooting rings all on its own
 **/
@Autonomous(name = "Blue2")
public class Blue2 extends LinearOpMode {


    ArtemisHardwareMap hardwareMapInitialize = new ArtemisHardwareMap();


    /**
     * The hardware map initialization object which initializes all our motors and servos
     **/

    /**
     * This stores our instance of the Vuforia localization engine and the license key.
     */
    private static final String VUFORIA_KEY = "AZFZpQv/////AAABma3flO1LSErYoIle7LztDPNRoW1dHp8UeguAk39po+KNco8nBQHysaMDKgzW/BH4Ue3+xmBKFZGWOesaq1FWHBHBpX3v4xlIImr1jMgxvbMvripQmY6vApS6VM3KkX1zkJ/pjj0iZ0BPExCxFC3aEY/GdRhb6QVtsmQ156Un7by4Awrqhub1Hwu5Ve+tBaapU8jaEuxjGU3AtURKMvDibswkbdbMG4d8QqCKf2Eh4tXbeWds1ox6wdolhkQrZTdFuITJc/nW9bM0nh95hQPRBRA5hQl6KWRyCCcRTvfbVggppr5MFMGICuc/TxXnYknRYBjVH9jRWiVcJrOPmad0qTOn8/e9ZrtD8ofS9sW51mTO";
    private VuforiaLocalizer vuforia;

    /**
     * This stores our instance of the TensorFlow Object Detection engine and the corresponding ring variables.
     */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Ball";
    private static final String LABEL_SECOND_ELEMENT = "Cube";
    private static final String LABEL_THIRD_ELEMENT = "Duck";
    private static final String LABEL_FOURTH_ELEMENT = "Marker";
    private static int position = 0;
    private TFObjectDetector tfod;
    private String object = " ";
    private ElapsedTime runtime = new ElapsedTime();

    /**
     * This method initializes hardware and logs it if it was successful
     **/
    public void initializeHardware() {
        telemetry.addData("Robot Initialized Successfully in Autonomous", " Wait for Hardware to Initialize");
        telemetry.update();
        hardwareMapInitialize.init(hardwareMap);
        telemetry.addData("Robot Hardware Initialized Successfully in Autonomous", "Press Play to Start");
        telemetry.update();
    }


    /**
     * This method initializes the tensorflow software and the Webcam to initialize Vuforia
     **/


    private void initVuforia() {

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.6f;
        tfodParameters.inputSize = 300;
        tfodParameters.isModelTensorFlow2 = true;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT, LABEL_THIRD_ELEMENT, LABEL_FOURTH_ELEMENT);
    }

    public void initializeTensorVuforia() {
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
        }
        telemetry.addData("Tensorflow and Vuforia ", "Initialized");
        telemetry.update();
    }





    @Override
    public void runOpMode() throws InterruptedException {
        new ManualFeedforwardTuner();


        /**
         * Initialize Hardware functions and Software functions called
         * **/
        initializeHardware();
        initializeTensorVuforia();
        hardwareMapInitialize.init(hardwareMap);

        //runs the actual opmode code here
        waitForStart();
        telemetry.addData("Robot Status Autonomous: ", "Is in Play Mode");



        position = 0;
        runtime.reset();
        // Check for storage hub level
        while (position == 0 && runtime.seconds() < 5) {
            checkObjects();
        }
        runtime.reset();
        if(position == 0){
            position = 3;
        }

        telemetry.addData("Position", position);

        hardwareMapInitialize.intake();
        sleep(1000);
        decideLevel();
//             Drive up, turn left, place cargo on storage hub, look straight again
        if (position != 0) {
            telemetry.addData("YOOOO IT WORKS?", position);
            telemetry.update();
            //ROAD RUNNER THINGS
            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
            Pose2d startPose = new Pose2d(0, 0, 0);
            drive.setPoseEstimate(startPose);

            TrajectorySequence dropOff = drive.trajectorySequenceBuilder(startPose)
                    .strafeRight(50)
                    .forward(25)
                    .build();
            TrajectorySequence parking = drive.trajectorySequenceBuilder(dropOff.end())
                    .turn(Math.toRadians(171))
                    .forward(75)
                    .build();



//            drive.followTrajectorySequence(carosuel);
//            hardwareMapInitialize.spinCarouselRed(telemetry);
            drive.followTrajectorySequence(dropOff);
            hardwareMapInitialize.outtake();
            drive.followTrajectorySequence(parking);
            telemetry.update();
            // STOP THE ROAD RUNNER THINGS

        }

        stopRobot();
        telemetry.update();

    }
    /**
     * This method logs that the robot was stopped and shuts down tesnorflow
     **/
    public void stopRobot() {
        telemetry.addData("Robot Status Autonomous: ", "Has Stopped");
        telemetry.update();
        if (tfod != null) {
            tfod.shutdown();
        }
    }
    public void decideLevel() {
         if (position == 1) {
            hardwareMapInitialize.moveLevelOne(telemetry);
        } else if (position == 2) {
            hardwareMapInitialize.moveLevelTwo(telemetry);
        } else if (position == 3) {
            hardwareMapInitialize.moveLevelThree(telemetry);

        }
    }
    private void checkObjects() {
        if (tfod != null) {
            //getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null && updatedRecognitions.size() != 0) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                int i = 0;
                for (Recognition recognition : updatedRecognitions) {

                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                    telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getTop());
                    telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getRight(), recognition.getBottom());
                    // check label to see which target zone to go after.
                    if (recognition.getLabel().equals("Ball")) {
                        object = "Ball";
                    } else if (recognition.getLabel().equals("Cube")) {
                        object = "Cube";
                    } else if (recognition.getLabel().equals("Duck")) {
                        object = "Duck";
                    } else if (recognition.getLabel().equals("Marker")) {
                        object = "Marker";
                        //object = "Marker";
                        if (recognition.getRight() < 200 && recognition.getTop() > 230) {
                            telemetry.addData("Duck ", "Left");
                            position = 1;
                        }
                        if (recognition.getRight() < 400 && recognition.getRight() > 200 && recognition.getTop() > 230) {
                            telemetry.addData("Duck ", "Middle");
                            position = 2;

                        }
                        if (recognition.getRight() < 600 && recognition.getRight() > 400 && recognition.getTop() > 230) {
                            telemetry.addData("Duck ", "Right");
                            position = 3;
                        }
                    }
                }

                return;
            }
        }


        telemetry.addData("# Object Detected", "0");
        telemetry.addData("TFOD", "No Items Detected");
    }


}








