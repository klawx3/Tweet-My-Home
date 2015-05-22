/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.IOException;

/**
 *
 * @author Klaw Strife
 */
public class RaspberryPiGPIO implements IOBridge, Runnable, GpioPinListenerDigital {

    final GpioController gpio;

    public RaspberryPiGPIO() {
        gpio = GpioFactory.getInstance();
        GpioPinInput[] pin = null;
        gpio.addListener(this, pin);
    }
    
    private void loop(){
        
    }

    @Override
    public boolean sendDeviceFlag(DeviceFlag flag) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean connect() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean disconnect() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addIODeviceListener(IODeviceEventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeIODeviceListener(IODeviceEventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        loop();
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        PinState state = event.getState();
    }
    
}
