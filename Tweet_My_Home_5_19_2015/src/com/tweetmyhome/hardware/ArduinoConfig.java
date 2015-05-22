/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

/**
 *
 * @author Klaw Strife
 */
    public class ArduinoConfig {
        private char EOF_CHARACTER = ';'; // default value
        private int time_out;
        private int data_rate;
        private String port;

        public ArduinoConfig() {
            setConfig(2000, 9600, "COM3",EOF_CHARACTER);
        }

        public ArduinoConfig(String port) {
            setConfig(2000, 9600, port, EOF_CHARACTER);
        }

        public ArduinoConfig(String port, char EOF_CHARACTER) {
            setConfig(2000, 9600, port, EOF_CHARACTER);
        }
        
        public ArduinoConfig(int data_rate,String port){
            setConfig(2000, data_rate, port,EOF_CHARACTER);            
        }

        public ArduinoConfig(int time_out, int data_rate, String port,char EOF_CHARACTER) {
            setConfig(time_out, data_rate, port,EOF_CHARACTER);
        }

        private void setConfig(int time_out, int data_rate, String port,char EOF_CHARACTER) {
            this.time_out = time_out;
            this.data_rate = data_rate;
            this.EOF_CHARACTER = EOF_CHARACTER;
            this.port = port;
        }

        public int getTime_out() {
            return time_out;
        }

        public int getData_rate() {
            return data_rate;
        }

        public String getPort() {
            return port;
        }

        public char getEOF_CHARACTER() {
            return EOF_CHARACTER;
        }

        public void setEOF_CHARACTER(char EOF_CHARACTER) {
            this.EOF_CHARACTER = EOF_CHARACTER;
        }
    }