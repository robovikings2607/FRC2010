/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.archwood.frc2607;

import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;
//import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;
import javax.microedition.io.Connector;
import java.io.DataOutputStream;

/*
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class BasicDrive extends IterativeRobot {

    private SpeedController leftMotor, rightMotor, // kickerMotor,
                               vacuumMotor, ballPusher, winchMotor, winchMotor2;

    private Joystick driveStick, gameStick;

    //private Solenoid ballKicker, ballLocker;
    private Solenoid releaseSproinger;

    private Compressor theCompressor;
    private DigitalInput kickerSwitch;

    private int autonStepNumber;
//    private int kickStepNumber;
//    private int kickLoopCount;
//    private double cockingTotalSeconds;

    private RobotDrive driveTrain;

    private Encoder leftEncoder, rightEncoder;
    private Gyro testGyro;
    private DriverStationLCD myLCD;

    private DataOutputStream myTelemetryFile = null;
    private FileConnection fc = null;

    private boolean m_autonRan = false;
    private boolean m_teleopRan = false;
//    private boolean kickValue = false;
    private boolean SproingerReleased = false;
//    private boolean prevSproing = false;
//    private boolean kickWindingTimerExpired = false;
    private int autonMode;
//    private boolean autonSet;

    private Kicker theKicker;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        System.out.println("Entering BasicDrive.robotInit()");

//        frontLeftMotor = new Victor(3);
//        frontRightMotor = new Victor(1);
     leftMotor = new Jaguar(1);
     rightMotor = new Jaguar(2);
//     kickerMotor = new Jaguar(3);
     vacuumMotor = new Jaguar(4);
     ballPusher = new Victor(5);
     winchMotor = new Jaguar(6);
     winchMotor2 = new Jaguar(7);
//            leftMotor = new CANJaguar(6);
//            rightMotor = new CANJaguar(7);


/*       while (leftMotor == null) {
            try {
                leftMotor = new CANJaguar(6);
                System.out.println("CANJaguar(6) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(6)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }

        while (rightMotor == null) {
            try {
                rightMotor = new CANJaguar(7);
                System.out.println("CANJaguar(7) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(7)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }

          while (kickerMotor == null) {
            try {
                kickerMotor = new CANJaguar(8);
                System.out.println("CANJaguar(8) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(8)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }

     while (ballMagnet == null) {
            try {
                ballMagnet = new CANJaguar(9);
                System.out.println("CANJaguar(9) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(9)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }

     while (ballPusher == null) {
            try {
                ballPusher = new CANJaguar(12);
                System.out.println("CANJaguar(12) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(12)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }
     while (winchMotor == null) {
            try {
                winchMotor = new CANJaguar(13);
                System.out.println("CANJaguar(13) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(13)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }
   while (winchMotor2 == null) {
            try {
                winchMotor2 = new CANJaguar(14);
                System.out.println("CANJaguar(14) created successfully");
            } catch (Exception e) {
                System.out.println("Error creating new CANJaguar(14)");
                System.out.println("Exception " + e.getMessage());
                Timer.delay(0.2);
            }
        }
  */

        driveStick = new Joystick(1);
        gameStick = new Joystick(2);
//        ballKicker = new Solenoid(1);// ballKicker releases kicking mechanism
//        ballLocker = new Solenoid(2);
        releaseSproinger = new Solenoid(3); // release the hooking mechanism
//        ballLocker.set(true);
//        ballKicker.set(false);
        theCompressor = new Compressor(1,1);
        theKicker = new Kicker(new DigitalInput(3), // kickerSwitch
                               new Solenoid(1),     // ballKicker
                               new Solenoid(2),     // ballLocker
                               new Jaguar(3));      // kickerMotor
        theKicker.start();
        
        driveTrain = new RobotDrive(leftMotor, rightMotor);

        leftEncoder = new Encoder(6, 7, false, CounterBase.EncodingType.k1X);
        rightEncoder = new Encoder (10, 11, false, CounterBase.EncodingType.k1X);
//        kickerSwitch = new DigitalInput(3); /* kickerSwitch is true when
//                                               kicker is fully cocked. */
         testGyro = new Gyro(2);
        myLCD = DriverStationLCD.getInstance();

        try {
            fc = (FileConnection)Connector.open("file:///FRC2607-TelemetryTest.txt", Connector.WRITE);
            fc.create();
            myTelemetryFile = fc.openDataOutputStream();
        } catch (Exception e) {
            System.out.println("\nrobotInit() Exception " + e.getMessage() + " opening telemetry file");
            e.printStackTrace();
            myTelemetryFile = null;
            fc = null;
        }
        System.out.println("Leaving BasicDrive.robotInit()");

    }

    public void autonomousInit() {

        autonStepNumber = 0;
//        kickStepNumber = 0;
//        kickLoopCount = 0;
        theCompressor.start();
//        ballLocker.set(true);
//        ballKicker.set(false);
        testGyro.reset();
        leftEncoder.start();
        leftEncoder.reset();
        leftEncoder.setDistancePerPulse(18.5 / 360.0);  // this seems to work
//        testEncoder.setDistancePerPulse(12.75 / 240.0);
        rightEncoder.start();
        rightEncoder.reset();
        rightEncoder.setDistancePerPulse(18.5 / 360.0);
    }

// other ratios:  15 teeth on gearbox, 22 on wheel
// looks like 240 cts per gearbox rev, rather than 360 theoretical


    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

        getWatchdog().feed();
        if (!m_autonRan) {
            System.out.println("Entering BasicDrive.autonomousPeriodic() for first time...");
            m_autonRan = true;
            System.out.println("Watchdog is :" +
                    (getWatchdog().getEnabled() ? "enabled" : "disabled"));
        }

        // 360 cts per rotation per encoder spec
        // but on prototype robot, 360cts equates to approx a full wheel rev,
        // not a sprocket rev (around 1.5 sprocket revs)
        // 360 = 19in traveled
/*
        double myHeading = testGyro.getAngle();
        if (myHeading >= 0 && myHeading <=45)
            driveTrain.drive(.2, 0);
        else
            driveTrain.drive(0, 0);
        String msg = "Auton Angle:" + myHeading + "     ";
        myLCD.println(DriverStationLCD.Line.kMain6, 1, msg);
        myLCD.updateLCD();
*/
  /*      if (Math.abs(testEncoder.getDistance()) <= 144.0) {
//          if (Math.abs(testEncoder.getRaw()) <= 240) {
            driveTrain.drive(-.2,0);
            String msg = "Raw:" + testEncoder.getRaw();
            msg += " Count:" + testEncoder.get() + "        ";
            myLCD.println(DriverStationLCD.Line.kMain6, 1, msg);
            msg = "Dir:" + testEncoder.getDirection();
            msg += " Dist:" + testEncoder.getDistance() + "        ";
            myLCD.println(DriverStationLCD.Line.kUser2, 1, msg);
            msg = "Per:" + testEncoder.getPeriod();
            msg += " Rate:" + testEncoder.getRate() + "       ";
            myLCD.println(DriverStationLCD.Line.kUser3, 1, msg);
            msg = "Stopped:" + testEncoder.getStopped() + "       ";
            myLCD.println(DriverStationLCD.Line.kUser4, 1, msg);
            myLCD.updateLCD();
        }
        else
            driveTrain.drive(0,0);
    } */

    /* Steps in auton: 0 - move forward x feet
                       1 - kick ball
                       2 - turn x degrees
                       3 - move forward x feet
     */

    if (autonMode > 3) {
        try { Thread.sleep(2000); } catch (Exception e) {}
        autonMode -= 3;
    }

    switch (autonMode)
     {

        case 1:
        switch (autonStepNumber)
         {

            case 0:
            if (Math.abs(leftEncoder.getDistance()) <59)
                driveTrain.drive (.5, 0); // NOTE 2.28.10: positive is forward!
            else {
                driveTrain.drive(0,0);
                autonStepNumber = 1;
                }
            break;

            case 1:
                theKicker.kick();
                autonStepNumber = 2;
                break;
            case 2:
                if (!theKicker.isKicking()) {   // if (kickDone)
                    autonStepNumber = -1;
                    leftEncoder.reset();
                    rightEncoder.reset();
                    testGyro.reset();
                 }
            break;

        }
       break;

        case 2:
         {
    switch (autonStepNumber)
         {

            case 0:
            if (Math.abs(leftEncoder.getDistance()) <59)
                driveTrain.drive (.5, 0); // NOTE 2.28.10: positive is forward!
            else {
                driveTrain.drive(0,0);
                autonStepNumber = 1;
                }
            break;

            case 1:
                theKicker.kick();
                driveTrain.drive(.5, 0);
                autonStepNumber = 2;
                break;
            case 2:
                if (Math.abs(leftEncoder.getDistance()) >= 34) {
                    driveTrain.drive(0, 0); 
                    if (!theKicker.isKicking()) {
                        autonStepNumber = 3;
                        leftEncoder.reset();
                        rightEncoder.reset();
                        testGyro.reset();
                    }                
                }
                break;

            case 3:
                theKicker.kick();
                autonStepNumber = 4;
                break;

            case 4:
                if (!theKicker.isKicking()) {
                    autonStepNumber = -1;
                    leftEncoder.reset();
                    rightEncoder.reset();
                    testGyro.reset();
                 }
                break;

    }

       break;
        }
        case 3:
         {
    switch (autonStepNumber)
         {
   case 0:
            if (Math.abs(leftEncoder.getDistance()) <29)
                driveTrain.drive (.5, 0); // NOTE 2.28.10: positive is forward!
            else {
                driveTrain.drive(0,0);
                autonStepNumber = 1;
                }
            break;

            case 1:
              theKicker.kick();
              driveTrain.drive(.5, 0);
              autonStepNumber = 2;
              break;

        case 2:
            if (Math.abs(leftEncoder.getDistance()) >=34) {
                driveTrain.drive(0, 0); 
                if (!theKicker.isKicking()) {
                    autonStepNumber = 3;
                    leftEncoder.reset();
                    rightEncoder.reset();
                    testGyro.reset();
                }                
            }
            break;

            case 3:
                theKicker.kick();
                driveTrain.drive(.5 ,0);
                autonStepNumber = 4;
                break;

        case 4:
            if (Math.abs(leftEncoder.getDistance()) >=34) {
                driveTrain.drive(0, 0);
                if (!theKicker.isKicking()) {
                    autonStepNumber = 5;
                    leftEncoder.reset();
                    rightEncoder.reset();
                    testGyro.reset();
                }
            }
            break;

        case 5:
            theKicker.kick();
            if (!theKicker.isKicking()) {
                autonStepNumber = -1;
                leftEncoder.reset();
                rightEncoder.reset();
                testGyro.reset();
            }
            break;


          }
         }
    break;
default:
    {

    }
     }

     String msg = "Angle: " + testGyro.getAngle();
     myLCD.println(DriverStationLCD.Line.kUser2, 1, msg);
                myLCD.updateLCD();
  }

    public void teleopInit() {

        try {
            if (myTelemetryFile != null)
                myTelemetryFile.writeUTF("in teleopInit()....");
                myTelemetryFile.flush();
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage() + " from writeUTF() in teleopInit()");
            e.printStackTrace();
        }

        System.out.println("teleopInit() - starting Compressor");
        theCompressor.start();
        System.out.println("teleopInit() - pressure switch = " + theCompressor.getPressureSwitchValue());
        System.out.println("teleopInit() - enabled() = " + theCompressor.enabled());
  //      theCompressor.setRelayValue(Relay.Value.kOn);

//        ballLocker.set(true);
//        ballKicker.set(false);
        theKicker.latch();
        leftEncoder.start();
        leftEncoder.reset();
        rightEncoder.start();
        rightEncoder.reset();
//        kickValue = false;
//        kickStepNumber = 0;
//        kickLoopCount = 0;
//        myLCD.println(DriverStationLCD.Line.kMain6, 1, "Ready!!");
//        myLCD.updateLCD();
        SproingerReleased = false;
//        prevSproing = false;
}

	static int printSec = (int)((Timer.getUsClock() / 1000000.0) + 1.0);
	static final int startSec = (int)(Timer.getUsClock() / 1000000.0);

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {

        if (!m_teleopRan) {
            System.out.println("Entering BasicDrive.teleopPeriodic() for first time....");
            m_teleopRan = true;
            System.out.println("Watchdog is :" +
                    (getWatchdog().getEnabled() ? "enabled" : "disabled"));
        }
//       boolean kickValue = false;
//       boolean displaySensors = driveStick.getRawButton(7); /* display sensor
//                                                                values */
//      boolean resetSensors = driveStick.getRawButton(8); // reset sensors
       boolean kickButton;
       if (driveStick.getRawAxis(3) < -.5)
           kickButton = false;
       else
           kickButton = true;
        boolean windKicker = driveStick.getRawButton(1); // cock kicker - button A
        boolean unwindKicker = driveStick.getRawButton(4); // give string slack - button B
        double winch = -(Math.abs(gameStick.getRawAxis(2))); // operate the winch
//        boolean ballHugger = driveStick.getRawButton(3);// operate the ball hugger - button X
        boolean backPusher = driveStick.getRawButton(2) || gameStick.getRawButton(5);//operate the back pusher - button Y
//        boolean Sproing = gameStick.getRawButton(6);
//      boolean scissorLiftRaise = gameStick.getRawButton(2);/* raise the
//                                                              scissor lift */
//      boolean scissorLiftExtend = gameStick.getRawButton(3); /* extend the
//                                                              scissor lift */

       boolean vacuum = driveStick.getRawButton(5) || gameStick.getRawButton(7); //operate the vacuum
        // ballKicker.set(gameStick.getRawButton(8));


       if (gameStick.getRawButton(8) || driveStick.getRawButton(6) || !kickButton) {
            theKicker.kick();
        }

       if (driveStick.getRawButton(7) && driveStick.getRawButton(8)) {
           winch = -winch;
           if (!theKicker.isKicking()) {
               if (unwindKicker) theKicker.unwind();
               else if (windKicker) theKicker.wind();
                    else theKicker.motorOff();
           }
       }

      vacuumMotor.set((vacuum) ? -1 : 0);
      ballPusher.set((backPusher) ? -.7 : 0);

       if (gameStick.getRawButtonToggle(6)) {
            SproingerReleased = !SproingerReleased;
       }

        releaseSproinger.set(SproingerReleased);

        String msg = "Kicker is " + (kickerSwitch.get() ? "cocked" : "out   ");
        myLCD.println(DriverStationLCD.Line.kUser2, 1, msg);
        msg = "Vacuum is : " + (vacuum ? "on " : "off");
        myLCD.println(DriverStationLCD.Line.kUser4, 1, msg);
        myLCD.updateLCD();

  /*      if (displaySensors) {
            msg = "Raw:" + testEncoder.getRaw();
            msg += " Count:" + testEncoder.get() + "        ";
            myLCD.println(DriverStationLCD.Line.kMain6, 1, msg);
            msg = "Dir:" + testEncoder.getDirection();
            msg += " Dist:" + testEncoder.getDistance() + "        ";
            myLCD.println(DriverStationLCD.Line.kUser2, 1, msg);
            msg = "Per:" + testEncoder.getPeriod();
            msg += " Rate:" + testEncoder.getRate() + "       ";
            myLCD.println(DriverStationLCD.Line.kUser3, 1, msg);
            msg = "Stopped:" + testEncoder.getStopped() + "       ";
            myLCD.println(DriverStationLCD.Line.kUser4, 1, msg);
            msg = "Angle:" + testGyro.getAngle();
            msg += " PID:" + testGyro.pidGet() + "       ";
            myLCD.println(DriverStationLCD.Line.kUser5, 1, msg);
            myLCD.updateLCD();
        } */

        double driveAxis = -driveStick.getRawAxis(2);
        double turnAxis = -driveStick.getRawAxis(4);

        if (SproingerReleased){
            winchMotor2.set(winch);
            winchMotor.set(winch);
            if (Math.abs(driveAxis) > .66) {
                driveAxis = .66 * (Math.abs(driveAxis) / driveAxis);
            }
            if (Math.abs(turnAxis) > .66) {
                turnAxis = .66 * (Math.abs(turnAxis) / turnAxis);
            }
        } else {
            winchMotor2.set(0);
            winchMotor.set(0);
        }

        driveTrain.arcadeDrive(driveAxis,turnAxis);
        getWatchdog().feed();
//        prevSproing = Sproing;
    }

    public void disabledInit() {
        try {
            if (myTelemetryFile != null) {
                myTelemetryFile.writeUTF("Second test....in disabledInit()....\n");
                myTelemetryFile.writeUTF("this is the 2nd line");
                myTelemetryFile.flush();
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage() + " from writeUTF() in disabledInit()");
            e.printStackTrace();
        }

        driveTrain.drive(0,0);
        winchMotor.set(0);
        winchMotor2.set(0);
        theCompressor.stop();
        leftEncoder.stop();
        rightEncoder.stop();
        releaseSproinger.set(false);
    }
/*
   private boolean kick() {
          int kickerMotorSpeed = -1;  // check if should be 1 or -1

          double secondsElapsed = ++kickLoopCount / 50;
          switch (kickStepNumber) {
              case 0:
                  ballKicker.set(true);
                  ballLocker.set(false);
                  if (secondsElapsed >= .5) {
                      ballKicker.set(false);
                      ballLocker.set(true);
                      kickStepNumber = 1;
                      kickLoopCount = 0;
                  }
                  return false;
              case 1:
                  kickerMotor.set(kickerMotorSpeed);
                  kickWindingTimerExpired = (secondsElapsed >= 1.2);
                  if (kickerSwitch.get() || kickWindingTimerExpired) 
                  {
                      myLCD.println(DriverStationLCD.Line.kUser3, 1, 
                                    "Cocking took: " + secondsElapsed + " secs");
                      myLCD.updateLCD();
                      cockingTotalSeconds = secondsElapsed;
                      kickStepNumber = 2;
                      kickLoopCount = 0;
                  }
                  return false;
              case 2:
                  if (secondsElapsed >= .05);
                  {
                      kickerMotor.set(0);
                      kickStepNumber = 3;
                      kickLoopCount = 0;
                  }
                  return false;
              case 3:
                  kickerMotor.set(-kickerMotorSpeed / 1.3);
                  if (secondsElapsed >= .85) {  // was 1.02
                      kickerMotor.set(0);
                      kickLoopCount = 0;
                      kickStepNumber = 0;
                      return true;
                  }
                  return false;
          }

          return true;

    }
*/

    public void disabledPeriodic() {
       this.getWatchdog().feed();
/*
       if  (!autonSet && gameStick.getRawButton(1))
        {
            if (autonMode <= 7) // was 4
            {
                autonMode++;
            } else
            {
                autonMode = 1;
            }
            autonSet = true;
        } else if (!gameStick.getRawButton(1))
        {
            autonSet = false;
        }
*/
       if (gameStick.getRawButtonToggle(1)) {
           autonMode = (autonMode <= 7) ? ++autonMode : 1;
       }
       
        switch (autonMode)
        {
            case 1:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "Near Auton Mode        ");
                myLCD.updateLCD();
                break;
            case 2:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "Middle Auton Mode      ");
                myLCD.updateLCD();
                break;
            case 3:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "Far Auton Mode         ");
                myLCD.updateLCD();
                break;
            case 4:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "WAIT, Near Auton Mode  ");
                myLCD.updateLCD();
                break;
            case 5:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "WAIT, Middle Auton Mode");
                myLCD.updateLCD();
                break;
            case 6:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "WAIT, Far Auton Mode   ");
                myLCD.updateLCD();
                break;
            default:
                myLCD.println(DriverStationLCD.Line.kMain6, 2, "No Auton Mode          ");
                myLCD.updateLCD();
                break;
        }
   }

    public void disabledContinuous() {

    }

    public void teleopContinuous() {

    }

    public void autonomousContinuous() {

    }


}
