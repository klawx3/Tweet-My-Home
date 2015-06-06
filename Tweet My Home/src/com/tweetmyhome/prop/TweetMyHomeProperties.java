/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.prop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import static com.esotericsoftware.minlog.Log.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Klaw Strife
 */
public final class TweetMyHomeProperties {
    public static final String FILE_NAME = "tweetmyhome.properties";
    public static final String COMMENTS = "hola ke ase";
    private final Properties prop;
    private final boolean firstTimeCreated;
    
    private FileOutputStream out;
    private FileInputStream in;


    /**
     * Keys and  default values
     */
    public static enum Key{
        debuggin            ("true"),
        arduinoIOBridge     ("false"),
        arduinoPort         ("COM3"),
        arduinoBitrate      ("9600"),
        arduinoEofChar      (";"),
        databaseIp          ("localhost"),     
        databasePort        ("3306"),
        databaseUser        ("root"),
        databasePassword    ("123456"),
        databaseName        ("tweetmyhome_db"),
        twitterSuperUser    ("@MyHome");
        
        Key(String defaultValue){
            this.defaultValue = defaultValue;
        }        
        public String getDefaultValue(){
            return defaultValue;
        }        
        private final String defaultValue;

    }
    
    public TweetMyHomeProperties() {
        prop = new Properties();
        if (!isPropertiesFileExist()) {
            firstTimeCreated = true;
            setDafaultProperties();
            saveProperties();
        } else {
            firstTimeCreated = false;
            loadProperties();

        }
        printProperties();
    }

    
    public Properties getProperties(){
        return prop;
    }

    public String getValueByKey(Key key) {
        return prop.getProperty(key.name());
    }

    public boolean isFirstTimeCreated() {
        return firstTimeCreated;
    }

    private void setDafaultProperties(){
        trace("Settings default properties...");
        for(Key k : Key.values()){            
            prop.setProperty(k.name(), k.getDefaultValue());
        }
    }
    
    public void saveProperties() {
        trace("Saving properties to file...");
        try {
            out = new FileOutputStream(FILE_NAME);
            prop.store(out, COMMENTS);
            out.close();
        } catch (FileNotFoundException ex) {
            error(ex.getLocalizedMessage());
        } catch (IOException ex) {
            error(ex.getLocalizedMessage());
        }
    }
    
    public void printProperties() {
        debug("Listing properties...");
        if (prop != null) {
            prop.forEach((k, v) -> {
                debug(String.format("Key:%s\tValue:%s", k, v));
            });
        }else{
            debug("Properties prop -> does't exist!");
        }
    }

    public void loadProperties(){
        trace("Loading properties from file...");
        try {
            in = new FileInputStream(FILE_NAME);
            prop.load(in);
            in.close();  
            
        } catch (IOException ex) {
            error(ex.getLocalizedMessage());
        }
    }
    
    private boolean isPropertiesFileExist(){
        return new File(FILE_NAME).isFile();
    }
}
