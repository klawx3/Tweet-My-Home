/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

/**
 *
 * @author Klaw Strife
 */
public class Sensor {

    private Integer id;
    private int attachedPin;
    private String location;
    private String name;
    private boolean repetitive;
    private String description;

    public Sensor(Integer id, int attachedPin, String location, String name, boolean repetitive, String description) {
        this.id = id;
        this.attachedPin = attachedPin;
        this.location = location;
        this.name = name;
        this.repetitive = repetitive;
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAttachedPin(int attachedPin) {
        this.attachedPin = attachedPin;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRepetitive(boolean repetitive) {
        this.repetitive = repetitive;
    }

    public void setDescription(String description) {
        this.description = description;
    }




}
