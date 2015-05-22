/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.util;

/**
 *
 * @author Administrador
 */
public class SODetector {

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.contains("win"));
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.contains("mac"));
    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.contains("nix") || os.contains("nux"));
    }

    public static boolean isSolaris() {
        String os = System.getProperty("os.name").toLowerCase();
        // Solaris
        return (os.contains("sunos"));
    }
    public static String soInfo(){
        return System.getProperty("os.name");
    }
}
