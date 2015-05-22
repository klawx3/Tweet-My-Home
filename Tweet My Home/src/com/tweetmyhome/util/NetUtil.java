/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.util;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Klaw Strife
 */
public class NetUtil {
    private static final String googleDNS = "8.8.8.8";
    
    public static boolean isConnectedToInternet(){
        try {
            InetAddress byName = Inet4Address.getByName(googleDNS);
            return byName.isReachable(2000);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
    
}
