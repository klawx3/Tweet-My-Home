/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static com.esotericsoftware.minlog.Log.*;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 *
 * @author Klaw Strife
 */
public class TweetMensajeCount {
    private long count;
    private FileWriter fwriter;
    private File f;

    public TweetMensajeCount(File f) {
        this.f = f;
        if (f.exists()) {
            FileReader freader = null;
            BufferedReader breader = null;
            try {
                freader = new FileReader(f);
                breader = new BufferedReader(freader);
                String readedLine = breader.readLine();
                this.count = Long.parseLong(readedLine);
            } catch (IOException ex) {
                error(ex.toString(), ex);
            } finally {
                try {
                    if (breader != null) {
                        breader.close();
                    }
                    if (freader != null) {
                        freader.close();
                    }
                } catch (IOException ex) {
                    error(ex.toString(), ex);
                }
            }
        } else {
            try {
                count = 0;
                fwriter = new FileWriter(f, false);
                fwriter.write(Long.toString(count));
                fwriter.flush();
            } catch (IOException ex) {
                error(ex.toString(), ex);
            } finally {
//                if (fwriter != null) {
//                    try {
//                        fwriter.close();
//                    } catch (IOException ex) {
//                        error(ex.toString(), ex);
//                    }
//                }
            }
        }
    }

    public void incementCount() {
        try {
            fwriter = new FileWriter(f, false);
            String countLongString = Long.toString(++count);
            fwriter.write(countLongString, 0, countLongString.length());
            fwriter.flush();
        } catch (IOException ex) {
            error(ex.toString(), ex);
        }

    }

    public long getCount() {
        return count;
    }

    public String getHashTagCount() {
        return "#" + Long.toString(count);
    }

    public String getHexHashTagCount() {
        return "#" + Long.toHexString(count);
    }

}
