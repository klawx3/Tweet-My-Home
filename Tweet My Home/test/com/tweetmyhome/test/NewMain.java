/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.test;

import com.tweetmyhome.Security;
import com.tweetmyhome.SecurityThreshhold;
import com.tweetmyhome.TweetFlag;
import com.tweetmyhome.TweetFlag.Flag;
import com.tweetmyhome.TweetFlag.Value;
import com.tweetmyhome.TweetMensajeCount;
import com.tweetmyhome.TweetStringAnalizer;
import com.tweetmyhome.TweetVariable;
import com.tweetmyhome.exceptions.TweetStringException;
import com.tweetmyhome.xml.XMLFilesManager;
import com.tweetmyhome.jaxb.devices.TweetMyHomeDevices;
import com.tweetmyhome.jaxb.dict.TweetMyHomeDictionary;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import static twitter4j.TwitterMethod.UPDATE_STATUS;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamAdapter;
import twitter4j.UserStreamListener;



public class NewMain{

    public static void main(String[] args) throws InterruptedException {
        TweetMyHomeDictionary tweetMyHomeDictionaryCommand = XMLFilesManager.getTweetMyHomeDictionaryCommand();
        tweetMyHomeDictionaryCommand.getCommand().forEach(c -> {
            System.out.println(c.getString());
            System.out.println(c.getFlag());
            System.out.println(c.getValue());
        });
        System.out.println("---------------------------");
        tweetMyHomeDictionaryCommand.getVariable().forEach(v->{
            System.out.println(v.getString());
            System.out.println(v.getValue());
        });
        //-------------
//        Scanner leer = new Scanner(System.in);
//        System.out.print("Ingrese cantdad de numeros a multiplicar:");
//        int cantidad = leer.nextInt();
//        int acu = 0;
//        for (int i = 1; i <= cantidad; i++) {
//            for (int j = 1; j <= cantidad; j++) {
//                System.out.print(j*i + "\t");
//            }         
//            System.out.println();
//        }
//        final Scanner l = new Scanner(System.in);
//        Security sec = new Security(true);
//        SecurityThreshhold sec_thr = new SecurityThreshhold(sec);
//        new Thread(() -> {
//            while (true) {
//                switch (l.nextInt()) {
//                    case 0:
//                        System.exit(1);
//                        break;
//                    case 1:
//                        sec_thr.estimulate();
//                        System.out.println("---estimulated---");
//                        break;
//                    default:
//                        System.out.println("0 exit - 1 estimulate");
//                }
//            }
//        }).start();
//
//        while(true){
//            System.out.println(sec_thr.toString());
//            Thread.sleep(125);
//        }
    }

}
