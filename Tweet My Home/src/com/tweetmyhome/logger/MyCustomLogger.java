/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.logger;

import static com.esotericsoftware.minlog.Log.LEVEL_DEBUG;
import static com.esotericsoftware.minlog.Log.LEVEL_ERROR;
import static com.esotericsoftware.minlog.Log.LEVEL_INFO;
import static com.esotericsoftware.minlog.Log.LEVEL_TRACE;
import static com.esotericsoftware.minlog.Log.LEVEL_WARN;
import com.esotericsoftware.minlog.Log.Logger;
import com.tweetmyhome.util.SODetector;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Klaw Strife
 */
public class MyCustomLogger extends Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final boolean unixShell;

    public MyCustomLogger() {
        unixShell = SODetector.isUnix();
        if (unixShell) {
            System.out.println("Unix/Linux Shell detected for logger");
        }
    }

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        StringBuilder builder = new StringBuilder(256);
        builder.append("[");
        if (unixShell) {
            builder.append(ANSI_CYAN);
            builder.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
            builder.append(ANSI_RESET);
        } else {
            builder.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
        }

        builder.append("]");
        switch (level) {
            case LEVEL_ERROR:
                if (unixShell) {
                    builder.append(ANSI_RED);
                    builder.append(" ERROR: ");
                    builder.append(ANSI_RESET);
                } else{
                    builder.append(" ERROR: ");
                }
                break;
            case LEVEL_WARN:
                if (unixShell) {
                    builder.append(ANSI_CYAN);
                     builder.append("  WARN: ");
                     builder.append(ANSI_RESET);
                }else{
                builder.append("  WARN: ");}
                break;
            case LEVEL_INFO:
                if (unixShell) {
                    builder.append(ANSI_GREEN);
                    builder.append("  INFO: ");
                    builder.append(ANSI_RESET);
                } else {
                    builder.append("  INFO: ");
                }
                break;
            case LEVEL_DEBUG:
                if (unixShell) {
                    builder.append(ANSI_YELLOW);
                    builder.append(" DEBUG: ");
                    builder.append(ANSI_RESET);
                } else {
                    builder.append(" DEBUG: ");
                }
                break;
            case LEVEL_TRACE:
                if (unixShell) {
                    builder.append(ANSI_GREEN);
                    builder.append(" TRACE: ");
                    builder.append(ANSI_RESET);
                } else {
                    builder.append(" TRACE: ");
                }

                break;
        }
        if (category != null) {
            builder.append('[');
            builder.append(category);
            builder.append("] ");
        }
        builder.append(message);
        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            ex.printStackTrace(new PrintWriter(writer));
            builder.append('\n');
            builder.append(writer.toString().trim());
        }
        System.out.println(builder);
    }
    

}
