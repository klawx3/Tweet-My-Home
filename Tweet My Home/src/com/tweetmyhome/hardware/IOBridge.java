/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

import java.io.IOException;

/**
 *
 * @author Klaw Strife
 */
public interface IOBridge {

    public void connect();
    public void disconnect();
    public boolean isConnected();
    public void activateSecurityLed();
    public void desactivateSecurityLed();
    public void activateComunityMode();
    public void desactivateComunityMode();
    public void addIODeviceListener(IODeviceEventListener listener);
    public void removeIODeviceListener(IODeviceEventListener listener);
    
}
