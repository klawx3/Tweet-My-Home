/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

import java.util.Date;

/**
 *
 * @author Klaw Strife
 */
public class HistorySensor {
    
    private final Integer idHistory;
    private final int idSensor;
    private final Date date;
    private final int value;
    private final int idTwitterUser;



    public HistorySensor(int id,int idSensor, Date fecha, int valor, int idUsuarioTwitter) {
        this.idHistory = id;
        this.idSensor = idSensor;
        this.date = fecha;
        this.value = valor;
        this.idTwitterUser = idUsuarioTwitter;
    }

    public HistorySensor(int idSensor, Date date, int value, int idTwitterUser) {
        idHistory = null;
        this.idSensor = idSensor;
        this.date = date;
        this.value = value;
        this.idTwitterUser = idTwitterUser;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public Date getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }

    public int getIdTwitterUser() {
        return idTwitterUser;
    }



    
    
    
}
