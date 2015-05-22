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
 * @param <O>
 * @param <I>
 */
public interface IOBridge {

    public boolean sendDeviceFlag(DeviceFlag flag) throws IOException;
    public boolean connect() throws IOException;
    public boolean disconnect() throws IOException;
    public boolean isConnected();
    public void addIODeviceListener(IODeviceEventListener listener);
    public void removeIODeviceListener(IODeviceEventListener listener);
    
}
