/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.archwood.frc2607;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Ron
 */
public class Kicker {

    private int stepNumber;
    private Thread stateMachineThread = null;
    private DigitalInput cockedPositionSwitch;
    private Solenoid kickerRelease, kickerLatch;
    private SpeedController windingMotor;
    private boolean runStateMachine;

    private static final int windingSpeed = -1;

    public Kicker(DigitalInput posSwitch, Solenoid release, Solenoid latch,
                  SpeedController motor) {
        cockedPositionSwitch = posSwitch;
        kickerRelease = release;
        kickerLatch = latch;
        windingMotor = motor;
        stepNumber = 0;
        latch();
        motorOff();
    }

    // Start the state machine thread
    public void start() {
        runStateMachine = true;
        if (stateMachineThread == null) {
            stateMachineThread = new KickerThread();
            stateMachineThread.start();
        }
    }

    // Stop the state machine thread (this will abort an in-progress sequence)
    public void stop() {
        runStateMachine = false;
        try { stateMachineThread.join(); } catch (Exception e) {}
        stateMachineThread = null;
        latch();
        motorOff();
        stepNumber = 0;
    }

    public boolean isKicking() {
        return (stepNumber > 0);
    }
    
    // initiate the kick sequence
    public void kick() {
        if (!isKicking()) stepNumber = 1;       // start the kick sequence
    }

    public void latch() {
        kickerRelease.set(false);
        kickerLatch.set(true);
    }

    public void unlatch() {
        kickerRelease.set(true);
        kickerLatch.set(false);
    }

    public void motorOff() {
        windingMotor.set(0);
    }

    public void wind(double m) {
        windingMotor.set(windingSpeed * m);
    }

    public void unwind(double m) {
        windingMotor.set(-(windingSpeed * m));
    }

    public void wind() {
        wind(1.0);
    }
    
    public void unwind() {
        unwind(1.0);
    }
   
    private class KickerThread extends Thread {

        private boolean cockingTimeoutExpired;
        private Timer t;
        
        public void run() {
            t = new Timer();
            while (runStateMachine) {
                doStateMachine();
                try { Thread.sleep(40); } catch (Exception e) {}
            }
        }

        public void doStateMachine() {

            switch (stepNumber) {

                case 1:
                    cockingTimeoutExpired = false;
                    unlatch();
                    try { Thread.sleep(500); } catch (Exception e) {}
                    latch();
                    stepNumber = 2;
                    t.start();
                    t.reset();
                    break;

                case 2:
                    wind();
                    if (t.get() > 1400000) {
                        cockingTimeoutExpired = true;
                    }
                    if (cockingTimeoutExpired || cockedPositionSwitch.get()) {
                        motorOff();
                        stepNumber = 3;
                        t.reset();
                    }
                    break;

                case 3:
                    unwind(.73);
                    if (t.get() > 850000) {
                        motorOff();
                        stepNumber = 0;
                        t.stop();
                        t.reset();
                    }
                    break;

                default:
                    if (!cockingTimeoutExpired && !cockedPositionSwitch.get()) {
                        stepNumber = 2;
                        t.start();
                        t.reset();
                        wind();
                    }
                    break;
            }
        }
    }

}
