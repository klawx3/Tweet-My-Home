/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.util.SODetector;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *@deprecated 
 * @author Klaw Strife
 */
public class Arduino implements IOBridge,SerialPortEventListener {

    public static final char ARDUINO_SPLIT_REGEX_CHAR = '|';
    private ArduinoConfig config;
    private CommPortIdentifier portId;
    private Enumeration portEnum;
    private SerialPort serialPort;
    private InputStream input;
    private OutputStream output;
    public boolean connected;
    private List<IODeviceEventListener> listeners ;
    private SerialOutputAnalizer analizer;

    public Arduino(ArduinoConfig config) {
        this.config = config;
        portEnum = null;
        portId = null;
        connected = false;
        if (SODetector.isUnix()) {
            System.setProperty("gnu.io.rxtx.SerialPorts", this.config.getPort());
        }

    }



    @Override
    public void connect()  {
        try {
            portEnum = CommPortIdentifier.getPortIdentifiers();
        } catch (Exception e) {
            throw new UnsatisfiedLinkError("Arduino drivers not found");
        }
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getName().equals(config.getPort())) {
                portId = currPortId;
                break;
            }
        }
        if (portId == null) {
            try {
                throw new IOException(String.format("Arduino Port [%s] not found ", config.getPort()));
            } catch (IOException ex) {
                Logger.getLogger(Arduino.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            serialPort = (SerialPort) portId.open(getClass().getName(), config.getTime_out());
            serialPort.setSerialPortParams(config.getData_rate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            try {
                input = serialPort.getInputStream();
            } catch (IOException ex) {
                Logger.getLogger(Arduino.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                output = serialPort.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(Arduino.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = true;
            trace("Arduino [" + config.getPort() + "] Connection open");

        } catch (PortInUseException | UnsupportedCommOperationException ex) {
            Logger.getLogger(Arduino.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }
        
        try {
            analizer = new SerialOutputAnalizer(config.getEOF_CHARACTER());
            serialPort.addEventListener(this);
            debug("event arduino added!");
        } catch (TooManyListenersException ex) {
            error(ex.getLocalizedMessage());
        }
        connected = true;
    }

    @Override
    public void disconnect() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
        debug("Serial port comunication closed");
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void addIODeviceListener(IODeviceEventListener listener) {
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }
    @Override
    public void removeIODeviceListener(IODeviceEventListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(DeviceSensor ds){
        if (listeners != null) listeners.forEach(l -> l.recivedIOEvent(ds) );
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        debug("Arduno event fired!");
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                byte chunk[] = new byte[input.available()];
                input.read(chunk, 0, input.available());
                analizer.setChunk(chunk);
                while (analizer.hasNext()) {
                    String arduinoText = analizer.next();
                    String[] split = arduinoText.split(Character.toString(ARDUINO_SPLIT_REGEX_CHAR));
                    if (split.length != 2) {
                        throw new IOException(String.format("Error arduino string [%s] don't recognized.\n"
                                + "ExampleString:TEMP%c23%c", arduinoText, ARDUINO_SPLIT_REGEX_CHAR, config.getEOF_CHARACTER()));
                    }
                    debug("ArduinoText:" + arduinoText);
                    //fireEvent(new DeviceSensor(this, split[0], split[1])); OJO---------------------------
                }
            } catch (IOException ex) {
                error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void activateSecurityLed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void desactivateSecurityLed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void activateComunityMode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void desactivateComunityMode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
    public final class SerialOutputAnalizer {

        private final char caracterDeCorte;
        private final Vector<String> cadenas;
        private final List<Byte> list_chunk;
        private boolean primera_llamada;

        public SerialOutputAnalizer(char caracterDeCorte) {
            cadenas = new Vector<>();
            list_chunk = new ArrayList<>();
            this.caracterDeCorte = caracterDeCorte;
        }

        public boolean hasNext() {
            if (cadenas.isEmpty()) {
                return false;
            } else {
                if (primera_llamada) {
                    primera_llamada = false;
                } else {
                    cadenas.remove(0);
                }
                if (cadenas.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        public String next() {
            return cadenas.get(0);
        }

        public void setChunk(byte[] chuck) {
            primera_llamada = true;
            chuckToList(chuck);
            searchStrings();
        }

        private void searchStrings() {
            int posicion_de_corte = -1;
            for (int i = 0; i < list_chunk.size(); i++) { // busco el corte
                if (((byte) list_chunk.get(i)) == caracterDeCorte) {
                    posicion_de_corte = i;
                    break;
                }
            }
            if (posicion_de_corte != -1) { // si encontro el corte
                int list_chunk_size = list_chunk.size();
                byte[] correct = new byte[posicion_de_corte + 1];
                for (int i = 0; i < posicion_de_corte + 1; i++) { // copio a correct antes del corte y elimino
                    if (i != posicion_de_corte) {
                        correct[i] = (list_chunk.get(0));
                    }
                    list_chunk.remove(0);
                }
                cadenas.add(new String(correct).trim());
                if (posicion_de_corte < list_chunk_size) {
                    searchStrings(); // funcion recursiva para seguir la comprobacion
                }
            }

        }

        private void chuckToList(byte[] chuck) {
            for (int i = 0; i < chuck.length; i++) {
                list_chunk.add(chuck[i]);
            }
        }

    }
    

}
