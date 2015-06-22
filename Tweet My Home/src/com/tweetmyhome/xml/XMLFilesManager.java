/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.xml;
import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.jaxb.devices.TweetMyHomeDevices;
import com.tweetmyhome.jaxb.dict.TweetMyHomeDictionary;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Klaw Strife
 */
public class XMLFilesManager {

    public static final String TWEET_MY_HOME_DEVICES_XML_FILE = "tweetmyhome_devices.xml";
    public static final String  TWEET_MY_HOME_DICTIONARY_XML_FILE = "tweetmyhome_dictionary.xml";

    public static TweetMyHomeDevices getTweetMyHomeDevices() {
        TweetMyHomeDevices tmh = null;
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(TweetMyHomeDevices.class.getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            tmh = (TweetMyHomeDevices) unmarshaller.unmarshal(new File(TWEET_MY_HOME_DEVICES_XML_FILE));
        } catch (javax.xml.bind.JAXBException ex) {
            error(ex.toString(), ex);
        }
        return tmh;
    }

    public static TweetMyHomeDictionary getTweetMyHomeDictionaryCommand() {
        TweetMyHomeDictionary tmdc = null;
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(TweetMyHomeDictionary.class.getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            tmdc = (TweetMyHomeDictionary) unmarshaller.unmarshal(new File(TWEET_MY_HOME_DICTIONARY_XML_FILE));
        } catch (javax.xml.bind.JAXBException ex) {
            error(ex.toString(), ex);
        }
        return tmdc;
    }

}
