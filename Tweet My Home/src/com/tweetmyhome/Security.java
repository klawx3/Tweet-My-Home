/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

/**
 *
 * @author Klaw Strife
 */
public class Security {

    private boolean enabled;
    private long enabledMilliStartTime;

    public Security(boolean enabled) {
        this.enabled = enabled;
        if(enabled){
            setEnabledMillisStartTime();
        }else{
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
        }

    }

    public void disable() {
        if (enabled) {
            enabled = false;
            resetMilliStartTime();
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
