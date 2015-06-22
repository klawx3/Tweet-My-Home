/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.tweetmyhome.TweetFlag.*;
import com.tweetmyhome.jaxb.dict.TweetMyHomeDictionary;
import com.tweetmyhome.jaxb.dict.TweetMyHomeDictionary.Command;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.jaxb.dict.TweetMyHomeDictionary.Variable;
import java.util.Map;

/**
 * Comandos de texto ingresados por twitter al hogar
 * Funciona como "Diccionario" de comandos
 * @author Klaw Strife
 */
public final class TweetStringDictionary {


    private final Map<String, TweetFlag> commandsMap;
    private final Map<String, TweetFlag.Value> variableDictionary;
    private final TweetMyHomeDictionary dic;
    
    public TweetStringDictionary(TweetMyHomeDictionary dic) {
        this.dic = dic;
        commandsMap = new HashMap<>();
        variableDictionary = new HashMap<>();
        List<TweetMyHomeDictionary.Command> commands = dic.getCommand();
        for (Command c : commands) {
            boolean error = false;
            String flagString = c.getFlag();
            String valueString = c.getValue();

            Flag posibleFlag = parseFlag(flagString);
            Value posibleValue = parseValue(valueString);
            if (posibleFlag == null) {
                warn("Flag [" + flagString + "] not recognized in " + c.getString());
                error = true;
            }
            if (posibleValue == null) {
                warn("Value [" + posibleValue + "] not recognized in " + c.getString());
                error = true;
            }
            if (!error) {
                TweetFlag tf = new TweetFlag(posibleFlag, posibleValue);
                commandsMap.put(c.getString(), tf);
                debug(String.format("Dictionary word updated [%s,%s,%s]", c.getString(), valueString, flagString));
            }

        }
        List<TweetMyHomeDictionary.Variable> variables = dic.getVariable();
        for (Variable v : variables) {
            boolean error = false;
            Value posibleValue = parseValue(v.getValue());
            if (posibleValue == null) {
                warn("Value [" + v.getValue() + "] not recognized in " + v.getString());
                error = true;
            }
            if (!error) {
                variableDictionary.put(v.getValue(), posibleValue);
                debug(String.format("Variable word updated [%s,%s]", v.getString(), v.getValue()));
            }
        }
    }
    

    /**
     * Return null if is do not founded
     * @param flagString
     * @return 
     */
    private Flag parseFlag(String flagString){
        Flag flag = null;
        for(Flag f : Flag.values()){
            if(f.name().equalsIgnoreCase(flagString)){
                flag = f;
                break;
            }
        }
        return flag;
    }
    /**
     * return null if is do not founded
     * @param valueString
     * @return 
     */
    private Value parseValue(String valueString){
        Value value = null;
        for(Value v : Value.values()){
            if(v.name().equalsIgnoreCase(valueString)){
                value = v;
                break;
            }
        }
        return value;
    }
    
    public Map<String, TweetFlag> getCommands() {
        return commandsMap;
    }

    public Map<String, TweetFlag.Value> getVariableDictionary() {
        return variableDictionary;
    }

    
}
