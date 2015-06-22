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
    private final long date;
    private final int value;
    private final long idTwitterUser;



    public HistorySensor(int id,int idSensor, long fecha, int valor, long idUsuarioTwitter) {
        this.idHistory = id;
        this.idSensor = idSensor;
        this.date = fecha;
        this.value = valor;
        this.idTwitterUser = idUsuarioTwitter;
    }

    public HistorySensor(int idSensor, long date, int value, long idTwitterUser) {
        idHistory = null;
        this.idSensor = idSensor;
        this.date = date;
        this.value = value;
        this.idTwitterUser = idTwitterUser;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public long getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }

    public long getIdTwitterUser() {
        return idTwitterUser;
    }

    @Override
    public String toString() {
        return "HistorySensor{" + "idHistory=" + idHistory + ", idSensor=" + idSensor + ", date=" + date + ", value=" + value + ", idTwitterUser=" + idTwitterUser + '}';
    }
    

    
    
    
}
