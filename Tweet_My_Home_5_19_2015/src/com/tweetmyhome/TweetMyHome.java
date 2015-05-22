/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.tweetmyhome.db.entity.SimpleDirectMessage;
import com.tweetmyhome.db.entity.AbstractMessage;
import com.tweetmyhome.db.entity.SimpleMention;
import com.esotericsoftware.minlog.Log;
import com.tweetmyhome.db.TweetMyHomeDatabase;
import com.tweetmyhome.hardware.Arduino;
import com.tweetmyhome.hardware.IOBridge;
import com.tweetmyhome.util.NetUtil;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.tweetmyhome.hardware.ArduinoConfig;
import com.tweetmyhome.util.TweetMyHomeProperties;
import com.tweetmyhome.util.TweetMyHomeProperties.Key;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.TweetStringAnalizer.ErrorString;
import com.tweetmyhome.exceptions.TweetStringException;
import com.tweetmyhome.hardware.DeviceSensor;
import com.tweetmyhome.hardware.IODeviceEventListener;
import com.tweetmyhome.hardware.RaspberryPiGPIO;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


public final class TweetMyHome implements IODeviceEventListener,TwitterStatusChangedListener{
    /**
     * Don't modify this constants for correct app execution
     */
    private final static boolean TESTING_DEV = true;    
    private final static boolean DEBUGGIN = true;
    private final static boolean INTERNET_REQUIRED_DEV = false;
    
    private TweetMyHomeDatabase db;
    private TwitterStatus ts;
    private ExecutorService threads;
    private TweetMyHomeProperties p;
    private IOBridge iob;
    private Security sec;

    public TweetMyHome(TweetMyHomeProperties p) {
        this.p = p;
        if (!TESTING_DEV) {
            info("Starting Tweet My Home...");
            if (NetUtil.isConnectedToInternet() || !INTERNET_REQUIRED_DEV) {
                info("Internet conection found...");
                db = new TweetMyHomeDatabase(this.p);
                if (db.connect()) {
                    info("Connected to DBMS...");
                    try {
                        startApp();
                    } catch (IOException ex) {
                        error(ex.getMessage());
                    }
                } else {
                    error("Not connected to DBMS");
                }
            } else {
                error("No internet connection found");
            }
        } else {
            trace("Testing Mode...");
            try {
                testCode();
            } catch (Exception ex) {
                error(ex.toString(),ex);
            }
        }
        //info("Exiting Aplication...");

    }

    public void startApp() throws IOException {
        sec = new Security(false);
        if (INTERNET_REQUIRED_DEV) {
            threads = Executors.newCachedThreadPool();
            ts = new TwitterStatus(db, 1000L);
            ts.addTwitterStatusChangedListener(this);
            threads.execute(ts);
        }

        // --------------------------------------------------------- //
        if (p.getValueByKey(Key.arduinoIOBridge).equalsIgnoreCase("true")) {
            iob = new Arduino(new ArduinoConfig(p.getValueByKey(Key.arduinoPort),
                    p.getValueByKey(Key.arduinoEofChar).charAt(0)));
        } else {
            iob = new RaspberryPiGPIO();
        }
        iob.addIODeviceListener(this);
        iob.connect();
        trace("'startApp()' function already executed...");
    }
    
    /**
     * IO DEVICE (HARDWARE) MESSAGE
     * @param device 
     */
    @Override
    public void recivedIOEvent(DeviceSensor device) {
        debug(device.toString());
    }

    /**
     * TWITTER RECIVED MESSAGE
     * @param am 
     */
    @Override
    public void recivedAbstractMessage(AbstractMessage am) {
        if(am instanceof SimpleDirectMessage){

        }else if(am instanceof SimpleMention){
        
        }
    }

    
    /**
     * Only enter in testing mode
     * @throws java.io.IOException
     * @throws com.tweetmyhome.exceptions.TweetStringException
     */
    public void testCode() throws IOException, TweetStringException {
        String string = "definir @cristian como usuario";

        TweetStringAnalizer t = new TweetStringAnalizer(string);
        if (t.isErrorFounded()) {
            debug("CON ERRORES :C");
            ErrorString e = t.getErrorString();
            debug("getCommandKey():" + e.getCommandKey());
            debug("getStringWithError():" + e.getStringWithError());
            debug("getStringPos():" + e.getStringPos());
        } else {
            debug("SIN ERRORES =3");
            debug("TweetFlag Flag:" + t.getFlagTweetFlag().getFlag().name());
            debug("TweetFlag Value:" + t.getFlagTweetFlag().getValue().name());

            List<TweetVariable> varList = t.getTweetVariableList();
            if( varList != null ){
                debug("Existen variables asociadas a el string <3!!!!!");
                t.getTweetVariableList().forEach(v -> debug("\t\t\t" + v.getVaribleIdentifier() + "\t" + v.getVariable()));
            }else{
                debug("No Existen variables asociadas a el string :CCC");
            }
            
            debug("rawCommandAsociated:" + t.getRawCommandAsociated());
        }


//        ArduinoConfig arduinoConfig = new ArduinoConfig(p.getValueByKey(Key.arduinoPort),
//                p.getValueByKey(Key.arduinoEofChar).charAt(0));
//        iob = new Arduino(arduinoConfig);
//        iob.addIODeviceListener(d -> {
//            debug(d.toString());
//        });
//        try {
//            iob.connect();
//        } catch (IOException | UnsatisfiedLinkError ex) {
//            error(ex.getLocalizedMessage(),ex);
//        }
//        Map<String, Flag> dev_commands = new HashMap<>();
//        dev_commands.put("activar seguridad", Flag.SECURITY);
//        dev_commands.put("desactivar seguridad", Flag.SECURITY);
//        dev_commands.put("definir $user como administrador", Flag.ADMIN);
//        dev_commands.put("definir $user como usuario", Flag.USER);
//        dev_commands.put("definir $sensor como $activo en $ubicacion", Flag.NULL);
//
//        String comandoIngresado = "DEFINIR IRL como true en pieza";
//        TweetString tweetString = new TweetString(comandoIngresado, dev_commands);
//        debug("Comando Ingresado:" + comandoIngresado);
//        if (tweetString.getMatchedEntry() != null) {
//            String key = tweetString.getMatchedEntry().getKey();
//            debug("Comando Encontrado:" + key);
//            if (tweetString.getTweetVariableList() != null) {
//                debug("Variables encontradas:");
//                tweetString.getTweetVariableList().forEach(var -> {
//                    debug(var.toString());
//                });
//            }
//        } else {
//            debug("No existe Coincidencia...");
//        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TweetMyHomeProperties prop = new TweetMyHomeProperties();
        if (!prop.isFirstTimeCreated()) {
            
            if (DEBUGGIN) {
                Log.set(Log.LEVEL_DEBUG);
                Log.set(Log.LEVEL_TRACE);
            } else {
                Log.set(Log.LEVEL_INFO);
            }
            prop.printProperties();
            new TweetMyHome(prop);
        } else {
            File file = new File(TweetMyHomeProperties.FILE_NAME);            
            info(String.format("Properties file created at: %s"
                    ,file.getAbsolutePath()));
            info(String.format("Please edit '%s' to correct aplication function"
                    , TweetMyHomeProperties.FILE_NAME));
            info("Exiting aplication");

        }
        /*----------------------------------------*/;

    }


}
