/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.tweetmyhome.TweetFlag.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comandos de texto ingresados por twitter al hogar
 * Funciona como "Diccionario" de comandos
 * @author Klaw Strife
 */
public final class TweetStringDictionary {


    private final Map<String, TweetFlag> commandsMap;
    private final Map<String, TweetFlag.Value> variableDictionary;
  
    public TweetStringDictionary() {
        commandsMap = new HashMap<>();
        variableDictionary = new HashMap<>();
        //Generar flags
        TweetFlag alarmOn       = new TweetFlag(Flag.ALARM,     Value.ON);
        TweetFlag alarmOff      = new TweetFlag(Flag.ALARM,     Value.OFF);
        TweetFlag alarmStatus   = new TweetFlag(Flag.ALARM,     Value.STATUS);
        TweetFlag comunityOn    = new TweetFlag(Flag.COMUNITY,  Value.ON);
        TweetFlag comunityOff   = new TweetFlag(Flag.COMUNITY,  Value.OFF);
        TweetFlag alarmValue    = new TweetFlag(Flag.ALARM,     Value.BY_VARIABLE);
        TweetFlag comunityValue = new TweetFlag(Flag.COMUNITY,  Value.BY_VARIABLE);
        TweetFlag userList      = new TweetFlag(Flag.USER,      Value.LIST);
        TweetFlag userAdd       = new TweetFlag(Flag.USER,      Value.ADD);
        TweetFlag userDel       = new TweetFlag(Flag.USER,      Value.DEL);
        TweetFlag userMod       = new TweetFlag(Flag.USER,      Value.MOD);
                
        //Generar Commandos.. faltaria generar comandos en otros idiomas ¿usar file?
        commandsMap.put("activar seguridad",           alarmOn);
        commandsMap.put("desactivar seguridad",        alarmOff);
        commandsMap.put("estado alarma",               alarmStatus);
        commandsMap.put("activar modo comunitario",    comunityOn);
        commandsMap.put("desactivar modo comunitario", comunityOff);
        commandsMap.put("alarma $var1",                alarmValue);
        commandsMap.put("comunitario $var1",           comunityValue);
        commandsMap.put("listar usuarios",             userList);
        commandsMap.put("agregar $var1",               userAdd);
        commandsMap.put("eliminar $var1",              userDel);
        commandsMap.put("definir $var1 como $var2",    userMod);
        commandsMap.put("dejar $var1 como $var2",    userMod);
        
        
        variableDictionary.put("on", Value.ON);
        variableDictionary.put("off", Value.OFF);
        variableDictionary.put("estado", Value.STATUS);
    }
    

    public Map<String, TweetFlag> getCommands() {
        return commandsMap;
    }

    public Map<String, TweetFlag.Value> getVariableDictionary() {
        return variableDictionary;
    }

    
}
