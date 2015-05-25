/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.xml;
import static com.esotericsoftware.minlog.Log.*;
import generated.TweetMyHomeDevices;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Klaw Strife
 */
public class XMLFilesManager {

    public static final String TWEET_MY_HOME_DEVICES_XML_FILE = "tweetmyhome_devices.xml";

    public static TweetMyHomeDevices getTweetMyHomeDevices() {
        TweetMyHomeDevices tmh = null;
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(TweetMyHomeDevices.class.getPackage().getName());
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            tmh = (TweetMyHomeDevices) unmarshaller.unmarshal(new File(TWEET_MY_HOME_DEVICES_XML_FILE));
        } catch (javax.xml.bind.JAXBException ex) {
            error("Sensor file [" + TWEET_MY_HOME_DEVICES_XML_FILE + "] not founded in this context [" + ex.toString()+ "]");
        }
        return tmh;

    }

    /**
     * Only a testing function
     * @param d 
     */
    public static void setTweetMyHomeDevices(TweetMyHomeDevices d) {

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(d.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(d, System.out);
        } catch (javax.xml.bind.JAXBException ex) {
            error(null, ex); 
        }
    }
}
