/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qmpc.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Klaw Strife
 */
public class JProgressBarElapsedTime implements Runnable{

    private static final long SLEEP_TIME = 100; 
    private JProgressBar bar;
    private long finalMilli;
    private long elapsedMilli;
    private boolean terminadoPorUsuario;
    

    public JProgressBarElapsedTime(long finalMilli, JProgressBar bar) {
        this.bar = bar;
        this.finalMilli = finalMilli;
        elapsedMilli = 0;
        terminadoPorUsuario = false;
    }

    @Override
    public void run() {
        boolean terminado = false;
        while (!terminado) {
            if(terminadoPorUsuario){
                break;
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(JProgressBarElapsedTime.class.getName()).log(Level.SEVERE, null, ex);
            }
            elapsedMilli += SLEEP_TIME;
            System.err.println(elapsedMilli);
            int round = Math.round(elapsedMilli * 100f / finalMilli);
            if(round > 100){
                break;
            }
            bar.setValue(round);
            
        }
    }
    
    public void terminar(){
        terminadoPorUsuario = true;
    }

}
