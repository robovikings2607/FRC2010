/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.archwood.frc2607;

/**
 *
 * @author Ron
 */
public class Joystick extends edu.wpi.first.wpilibj.Joystick {

    private int prevButtonValues;

    public Joystick(final int port) {
        super(port);
        prevButtonValues = 0;
    }

    public boolean getRawButtonToggle(final int button) {

        int bitValue = (0x1 << (button - 1));
        boolean prevButtonWasOff = ((bitValue & prevButtonValues) == 0);
        boolean curButtonIsOn = getRawButton(button);

        if (curButtonIsOn) prevButtonValues = prevButtonValues | bitValue;
        else prevButtonValues = prevButtonValues & ~bitValue;

        return (prevButtonWasOff && curButtonIsOn);
    }
}
