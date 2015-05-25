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
import static com.esotericsoftware.minlog.Log.*;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import generated.TweetMyHomeDevices;
import generated.TweetMyHomeDevices.Sensor;
import java.util.List;
/**
 *
 * @author Klaw Strife
 */
public class RaspberryPiGPIO implements IOBridge, Runnable, GpioPinListenerDigital {

    private GpioController gpio;
    private TweetMyHomeDevices devicesInfo;
    
    public RaspberryPiGPIO(TweetMyHomeDevices devicesInfo) {
//        this.devicesInfo = devicesInfo;
//        try {
//            gpio = GpioFactory.getInstance();
//            List<Sensor> sensors = devicesInfo.getSensor();
//            sensors.size();
//            for(Sensor sensor : sensors){
//                Pin pinByName = RaspiPin.getPinByName("GPIO 1");
//            }
//            
//           // GpioPinDigitalInput asd = gpio.provisionDigitalInputPin(, "sd");
//        } catch (UnsatisfiedLinkError | IllegalArgumentException e) {
//            error(e.toString());
//        }

    }
    /*
    public boolean sendDeviceFlag(DeviceFlag flag) throws IOException;
    public boolean connect() throws IOException;
    public boolean disconnect() throws IOException;
    public boolean isConnected();
    public void addIODeviceListener(IODeviceEventListener listener);
    public void removeIODeviceListener(IODeviceEventListener listener);
    
    */

    @Override
    public void run() {
        
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        PinState state = event.getState();
    }

    @Override
    public boolean sendDeviceFlag(DeviceFlag flag) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean connect() throws IOException {
//        gpio.addListener(this, gpis);
        return true;
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
        
    }

    @Override
    public void removeIODeviceListener(IODeviceEventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }




    
}
