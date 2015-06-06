/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

import java.util.EventObject;

/**
 *
 * @author Klaw Strife
 */
public class DeviceSensor extends EventObject {

    private final int pin;
    private final boolean activated;

    public DeviceSensor(Object source, int pin, boolean activated) {
        super(source);
        this.pin = pin;
        this.activated = activated;
    }

    public int getPin() {
        return pin;
    }

    public boolean isActivated() {
        return activated;
    }

    

}
