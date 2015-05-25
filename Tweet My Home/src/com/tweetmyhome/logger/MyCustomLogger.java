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

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        StringBuilder builder = new StringBuilder(256);
        builder.append("[");
        builder.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
        builder.append("]");
        switch (level) {
            case LEVEL_ERROR:
                builder.append(" ERROR: ");
                break;
            case LEVEL_WARN:
                builder.append("  WARN: ");
                break;
            case LEVEL_INFO:
                builder.append("  INFO: ");
                break;
            case LEVEL_DEBUG:
                builder.append(" DEBUG: ");
                break;
            case LEVEL_TRACE:
                builder.append(" TRACE: ");
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
