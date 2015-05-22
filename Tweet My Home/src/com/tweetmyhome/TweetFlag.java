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
public class TweetFlag {

    private final Flag flag;
    private Value value;

    public static enum Flag {
        ALARM,      // Cuando se maneja la alarma del hogar
        COMUNITY,   // cuando se maneja el modo comunitario
        USER,       // para administrar usuarios
        NULL        // Indica valor nulo [NO DEVERIA SER USADO]
    }

    public static enum Value {
        ON,         //Activo o activar
        OFF,        //Desactivo o desactivar
        STATUS,     //Informacion acerca de...
        ADD,        //Agregar
        DEL,        //Eliminar
        LIST,       //Listado de...
        BY_VARIABLE,//El valor lo define una variable en el string... deveria hacer referencia a algun valor anterior
        MOD,        //Modificar algo.. (hasta ahora usuarios)
        NULL        //Indica valor nulo
    }

    
    public TweetFlag(Flag flag, Value value) {
        this.flag = flag;
        this.value = value;
    }
    
    public void setValue(Value v){
        this.value = v;
    }

    public Flag getFlag() {
        return flag;
    }

    public Value getValue() {
        return value;
    }


    
}
