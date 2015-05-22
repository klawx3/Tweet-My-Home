/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qmpc.gui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author Klaw Strife
 */
public class JLabelProgressTime implements Runnable{
    private JLabel label;
    private boolean terminadoPorUsuario;
    private static final int SLEEP_TIME = 100;
    private float elapsedTime;
    private long milli;
    private boolean colorSeteado;

    public JLabelProgressTime(long milli,JLabel label) {
        this.milli = milli;
        this.label = label;
        terminadoPorUsuario = false;
        elapsedTime = 0;
        colorSeteado = false;
    }

    @Override
    public void run() {
       
        while(!terminadoPorUsuario){
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(JLabelProgressTime.class.getName()).log(Level.SEVERE, null, ex);
            }
            elapsedTime += SLEEP_TIME;
            if(elapsedTime > milli && !colorSeteado){
                label.setForeground(Color.red);
                colorSeteado = true;
            }
            label.setText(String.format("%.1f", elapsedTime/1000));
        }
    }

    public void terminar() {
        terminadoPorUsuario = true;
    }

}
