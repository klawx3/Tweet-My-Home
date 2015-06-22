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
public class Comunity {
    
    private boolean activated;
    private IOBridge iob;

    public Comunity(boolean activated,IOBridge iob) {
        this.activated = activated;
        this.iob = iob;
    }

    public boolean isActivated() {
        return activated;
    }
    
    public void activateComunityMode(){
        activated = true;
        iob.activateComunityMode();
    }
    
    public void desactivateComunityMode(){
        activated = false;
        iob.desactivateComunityMode();
    }

    
}
