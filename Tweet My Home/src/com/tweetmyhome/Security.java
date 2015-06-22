/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.tweetmyhome.hardware.IOBridge;

/**
 *
 * @author Klaw Strife
 */
public class Security {

    private boolean enabled;
    private long enabledMilliStartTime;
    private IOBridge io;

    public Security(boolean enabled,IOBridge io) {
        this.io = io;
        this.enabled = enabled;
        if(enabled){
            io.activateSecurityLed();
            setEnabledMillisStartTime();
        }else{
            io.desactivateSecurityLed();
            resetMilliStartTime();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        if (!enabled) {
            this.enabled = true;
            setEnabledMillisStartTime();
            io.activateSecurityLed();
        }

    }

    public void disable() {
        if (enabled) {
            enabled = false;
            resetMilliStartTime();
            io.desactivateSecurityLed();
        }
    }

    /**
     * @return Retorna -1 en caso de estar la seguridad desabilitada. Retorna el tiempo que ha
     * estado la seguridad activa en millisegundos
     */
    public long getEnabledMillisTime() {
        return enabledMilliStartTime == -1 ? -1 : System.currentTimeMillis() - enabledMilliStartTime;
    }

    private void setEnabledMillisStartTime() {
        enabledMilliStartTime = System.currentTimeMillis();
    }

    private void resetMilliStartTime() {
        enabledMilliStartTime = -1;
    }

}
