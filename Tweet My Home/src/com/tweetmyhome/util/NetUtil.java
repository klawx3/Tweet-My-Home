/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.util;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.esotericsoftware.minlog.Log.*;
/**
 *
 * @author Klaw Strife
 */
public class NetUtil {
    private static final String URL_TO_TEST = "http://www.yahoo.com";

    public static boolean isConnectedToInternet() {
        try {
            URL url = new URL(URL_TO_TEST);
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            Object objData = urlConnect.getContent();
            return true;
        } catch (UnknownHostException e) {
            error("Unresolved host... check DNS or INTERNET Connection",e);
        } catch (IOException e) {
            error(null,e);
        }
        return false;
        
    }

}
