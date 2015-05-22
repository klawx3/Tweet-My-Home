/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

import java.util.EventObject;

/**
 *
 * @author Klaw Strife
 */
public class DeviceSensor extends EventObject {



    private String nombre;
    private String value;

    public DeviceSensor(Object source, String nombre, String value) {
        super(source);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DeviceSensor{" + "nombre=" + nombre + ", value=" + value + '}';
    }

}
