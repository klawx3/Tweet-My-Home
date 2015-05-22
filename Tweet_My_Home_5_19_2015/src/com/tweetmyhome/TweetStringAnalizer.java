/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;


import com.tweetmyhome.TweetFlag.Value;
import com.tweetmyhome.exceptions.TweetStringException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author Klaw Strife
 */
public class TweetStringAnalizer {

    public static final String VARIABLE_STRING_IDENTIFIER = "$";
    public static final TweetStringDictionary TWEET_STRING_COMMANDS_DIC = new TweetStringDictionary();
    private final String rawString;
    private boolean errorFounded;
    private Entry<String, TweetFlag> matchedEntry;
    private List<TweetVariable> tweetVariableList;    
    private ErrorString errorString;


    public TweetStringAnalizer(String rawString) throws TweetStringException {        
        this.rawString = rawString;
        analizeString();
    }
    /**
     * Analisis lexico y sintactico de la cadena
     * @throws TweetStringException 
     */
    private void analizeString() throws TweetStringException {
        for (Entry<String, TweetFlag> commandEntry : TWEET_STRING_COMMANDS_DIC.getCommands().entrySet()) {
            int errorPos = 0;
            boolean errorFound = false;
            StringTokenizer rawStringTokenized = new StringTokenizer(rawString.toLowerCase());
            StringTokenizer keyStringTokenized = new StringTokenizer(commandEntry.getKey());
            while (keyStringTokenized.hasMoreTokens() && rawStringTokenized.hasMoreTokens() && !errorFound) {
                String keyToken = keyStringTokenized.nextToken();
                String rawToken = rawStringTokenized.nextToken();
                if (keyToken.startsWith(VARIABLE_STRING_IDENTIFIER, 0)) { // es una variable ?
                    if (tweetVariableList == null) {
                        tweetVariableList = new ArrayList<>();
                    }
                    tweetVariableList.add(new TweetVariable(keyToken,rawToken));
                } else if (!keyToken.equals(rawToken)) { // entonces es un error
                    errorFound = true;
                    if (tweetVariableList != null) {
                        tweetVariableList.clear();
                    }
                    if (errorString == null) { // primera vez error
                        errorString = new ErrorString(errorPos, commandEntry.getKey());
                    } else if (errorPos > errorString.getStringPos()) { // tengo un nuevo max error
                        errorString = new ErrorString(errorPos, commandEntry.getKey());
                    }
                }
                errorPos++;
            }
            if (!errorFound) {
                matchedEntry = commandEntry;
                defineFlagValue(); // se define en caso de no tener flag definida ya que puede ser definida por variable
                break;
            }
        }
        if(matchedEntry == null){
            errorFounded = true;
        }
    }
    
        private void defineFlagValue() throws TweetStringException {
        TweetFlag flag = matchedEntry.getValue();
        if(flag.getValue().equals(Value.BY_VARIABLE)){
            if(tweetVariableList.size() > 1){
                throw new TweetStringException("Can't set Flag 'Value.BY_VARIABLE' if exist more than one tweet variable");
            }else{
                TweetVariable var = tweetVariableList.get(0);
                String variable = var.getVariable();
                boolean variableFound = false;
                for (Entry<String, Value> entry : TWEET_STRING_COMMANDS_DIC.getVariableDictionary().entrySet()) {
                    if(entry.getKey().equalsIgnoreCase(variable)){
                        if(variableFound){
                            throw new TweetStringException("Duplicated variables");
                        }
                        variableFound = true;
                        flag.setValue(entry.getValue());                    
                    }
                }
                if(!variableFound){
                    errorFounded = true;
                    
                }

            }
        }
    }


    /**
     * Obtienela bandera digitado por el usuario
     * puede retornar null en caso de no haber encontrado la cadena en el diccionario de comandos
     * La idea es llamar antes a la funcion isErrorFounded() 
     * @return 
     */
//    VER EL TEMA EN CASO DE SER VARIABLES DEFINIDAS POR EL USUARIO. CAMBIAR LA FLAG
    public TweetFlag getFlagTweetFlag() {
        if (matchedEntry != null) {
            return matchedEntry.getValue();
        }
        return null;

    }
     /**
     * Obtiene el comando por el usuario
     * puede retornar null en caso de no haber encontrado la cadena en el diccionario de comandos
     * La idea es llamar antes a la funcion isErrorFounded() 
     * @return 
     */

    public String getRawCommandAsociated(){
        if (matchedEntry != null) {
            return matchedEntry.getKey();
        }
        return null;
    }

    /**
     * Retorna una lista de variables, puede contener null en caso de no contener variables
     * @return 
     */

    public List<TweetVariable> getTweetVariableList() {
        return tweetVariableList;
    }

    /**
     * Retorna true en caso de haber encontrado algun error
     * @return 
     */

    public boolean isErrorFounded() {
        return errorFounded;
    }

    /**
     * Retorna el error encontrado.
     * La idea es llamar antes a la funcion isErrorFounded() 
     * @return 
     */

    public ErrorString getErrorString() {
        return errorString;
    }

    /**
     * Se define el valor de la flag por el valor que tengan las variables
     */
    
    public class ErrorString{
        private final Integer stringPos;
        private final String commandKey;
        private String stringWithError;

        public ErrorString(Integer stringPos, String commandKey) {
            this.stringPos = stringPos;
            this.commandKey = commandKey;
        }

        public Integer getStringPos() {
            return stringPos;
        }

        public String getCommandKey() {
            return commandKey;
        }

        public String getStringWithError() {
            return stringWithError;
        }

        public void setStringWithError(String stringWithError) {
            this.stringWithError = stringWithError;
        }
  
        
        
    }
    
    

}
