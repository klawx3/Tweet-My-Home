/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.tweetmyhome.exceptions.TweetMyHomeException;
import generated.TweetMyHomeDevices;
import java.util.List;
import static com.esotericsoftware.minlog.Log.*;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Klaw Strife
 */
public class RaspberryPiGPIO implements IOBridge, GpioPinListenerDigital {

    private GpioController gpio;
    private final TweetMyHomeDevices devicesInfo;
    private Map<GpioPinDigitalOutput,ActuatorType> mapGpioDigitalOutput;
    private List<GpioPinDigitalInput> listGpioDigitalInput;
    private List<IODeviceEventListener> listeners;
    private final static String GPIO_REF_STRING = "GPIO ";
    private boolean connected;


    
    public enum ActuatorType{
        SECURITY_LED,COMUNITY_LED,OTHER    
    }
    
    public RaspberryPiGPIO(TweetMyHomeDevices devicesInfo) throws TweetMyHomeException {
        connected = false;
        this.devicesInfo = devicesInfo;        
        if(devicesInfo.getActuator().size() < 2){
            throw new TweetMyHomeException("This APP require at last 2 'actuators' as digitial output pins...");
        }
    }
    
    private void fireEvent(DeviceSensor d){
        if(listeners != null) for(IODeviceEventListener  l : listeners) l.recivedIOEvent(d);
    }


    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        PinState state = event.getState();
        Pin pin = event.getPin().getPin();        
        fireEvent(new DeviceSensor(this, pinNameToInteger(pin.getName()), state.isHigh()));
    }

    private int pinNameToInteger(String name) { // FALTA !!!! PLIX
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connect() {
        try {
            gpio = GpioFactory.getInstance();
            defineActuators();
            defineSensors();
            connected = true;
        } catch (TweetMyHomeException ex) {
            error(ex.toString(),ex);
        }

    }

    @Override
    public void disconnect() {
        gpio.shutdown();
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void addIODeviceListener(IODeviceEventListener listener) {
        if(listeners == null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    @Override
    public void removeIODeviceListener(IODeviceEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Activate all pins defined as security mode
     */
    @Override
    public void activateSecurityLed() {
        mapGpioDigitalOutput.forEach((k, v) -> {
            if (v.equals(ActuatorType.SECURITY_LED)) {
                if (!k.isHigh()) {
                    k.high();
                } else {
                    debug("Security led already HIGH... ");
                }
            }
        });

    }

    /**
     * Desactivate all pins defined as security mode
     */
    @Override
    public void desactivateSecurityLed() {
        mapGpioDigitalOutput.forEach((k, v) -> {
            if (v.equals(ActuatorType.SECURITY_LED)) {
                if (!k.isLow()) {
                    k.low();
                } else {
                    debug("Security led already Low... ");
                }
            }
        });
    }
    /**
     * Activate all pins defined as comunity mode
     */
    @Override
    public void activateComunityMode() {            
        mapGpioDigitalOutput.forEach((k, v) -> {
            if (v.equals(ActuatorType.COMUNITY_LED)) {
                if (!k.isHigh()) {
                    k.high();
                } else {
                    debug("Comunity led already high... ");
                }
            }
        });
    }

    /**
     * Desactivate all pins defined as comunity mode
     */
    @Override
    public void desactivateComunityMode() {      
        mapGpioDigitalOutput.forEach((k, v) -> {
            if (v.equals(ActuatorType.COMUNITY_LED)) {
                if (!k.isLow()) {
                    k.low();
                } else {
                    debug("Comunity led already Low... ");
                }
            }
        });
    }

    private void defineActuators() throws TweetMyHomeException {
        List<TweetMyHomeDevices.Actuator> actuators = devicesInfo.getActuator();
        mapGpioDigitalOutput = new HashMap<>();
        boolean comunityDef = false, securityDef = false;
        for (TweetMyHomeDevices.Actuator act : actuators) {
            Pin pinByName = RaspiPin.getPinByName(getGpioPinName(act));
            GpioPinDigitalOutput digitalOutputPin = gpio.provisionDigitalOutputPin(pinByName);
            if (act.getName().toLowerCase().contains("security")) {
                mapGpioDigitalOutput.put(digitalOutputPin, ActuatorType.SECURITY_LED);
                debug(act.getName() + " Defined as SECURITY_LED in pin " 
                        + getGpioPinName(act));
                securityDef = true;
            } else if (act.getName().toLowerCase().contains("comunity")) {
                mapGpioDigitalOutput.put(digitalOutputPin, ActuatorType.COMUNITY_LED);
                debug(act.getName() + " Defined as COMUNITY_LED in pin " 
                        + getGpioPinName(act));
                comunityDef = true;
            } else { // error or other pin defined
                mapGpioDigitalOutput.put(digitalOutputPin, ActuatorType.OTHER);
                debug(act.getName() + " Defined as OTHER_ACTUATOR in pin " 
                        + getGpioPinName(act));
            }
        }
        if(!securityDef || !comunityDef){
            throw new TweetMyHomeException("SECURITY_LED or COMUNITY_LED not defined."
                    + "please type in <name> tag 'comunity' or 'security'");
        }
    }

    private void defineSensors() {
        listGpioDigitalInput = new ArrayList<>();
        devicesInfo.getSensor().forEach(s -> {
            Pin pinByName = RaspiPin.getPinByName(getGpioPinName(s));
            GpioPinDigitalInput booleanSensor = gpio.provisionDigitalInputPin(pinByName, PinPullResistance.PULL_DOWN);
            booleanSensor.addListener(this);
            listGpioDigitalInput.add(booleanSensor);
            debug(s.getName() 
                    + " Defined as Boolean Sensor [resistor=PinPullResistance.PULL_DOWN] in pin " + getGpioPinName(s));
        });

    }    

    private String getGpioPinName(TweetMyHomeDevices.Actuator act) {
        return GPIO_REF_STRING + act.getAttachedPin().intValue();
    }

    private String getGpioPinName(TweetMyHomeDevices.Sensor sen) {
        return GPIO_REF_STRING + sen.getAttachedPin().intValue();
    }



    
}
