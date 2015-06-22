/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.esotericsoftware.minlog.Log;
import com.tweetmyhome.db.TweetMyHomeDatabase;
import com.tweetmyhome.hardware.IOBridge;
import com.tweetmyhome.util.NetUtil;
import com.tweetmyhome.prop.TweetMyHomeProperties;
import com.tweetmyhome.prop.TweetMyHomeProperties.Key;
import java.io.File;
import java.io.IOException;
import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.db.entity.HistoryComunityMode;
import com.tweetmyhome.db.entity.HistorySecurity;
import com.tweetmyhome.db.entity.HistorySensor;
import com.tweetmyhome.db.entity.SimpleDirectMessage;
import com.tweetmyhome.db.entity.SimpleMention;
import com.tweetmyhome.db.entity.TwitterUser;
import com.tweetmyhome.db.entity.TwitterUser.UserRol;
import com.tweetmyhome.exceptions.TweetMyHomeException;
import com.tweetmyhome.exceptions.TweetStringException;
import com.tweetmyhome.hardware.DeviceSensor;
import com.tweetmyhome.hardware.IODeviceEventListener;
import com.tweetmyhome.hardware.RaspberryPiGPIO;
import com.tweetmyhome.logger.MyCustomLogger;
import com.tweetmyhome.util.TwitterUserUtil;
import com.tweetmyhome.xml.XMLFilesManager;
import com.tweetmyhome.jaxb.devices.TweetMyHomeDevices;
import com.tweetmyhome.jaxb.dict.TweetMyHomeDictionary;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.api.DirectMessagesResourcesAsync;
import twitter4j.conf.ConfigurationBuilder;

public final class TweetMyHome implements IODeviceEventListener, UserStreamListener {

    private final static boolean INTERNET_REQUIRED_DEV = true;
    private final static boolean RASPBERRY_ON_BOARD = true;
    private final static String TENDENCE = "#TMH";
    private final static File COUNT_FILE = new File("messajes.count");
    private final TweetMyHomeProperties p;
    private final TweetMyHomeDevices tmh_device_xml;
    private final TweetMyHomeDictionary tmh_dic_xml;
    private final TweetStringDictionary tweetDictionary;
    private final TweetMensajeCount tweetCount;
    private final SecurityThreshhold sect ;
    private TweetMyHomeDatabase db;
    private Security sec;
    private Comunity com;
    private TwitterStream tws;
    private Twitter tw;
    private IOBridge iob;

    public TweetMyHome() throws TweetMyHomeException, IOException, TweetStringException {
        
        Log.setLogger(new MyCustomLogger());
        p = new TweetMyHomeProperties();
        if (!p.isFirstTimeCreated()) {            
            if (p.getValueByKey(Key.debuggin).equalsIgnoreCase("true")) {
                Log.set(Log.LEVEL_DEBUG);
                Log.set(Log.LEVEL_TRACE);
            } else {
                Log.set(Log.LEVEL_INFO);
            }
        } else {
            File file = new File(TweetMyHomeProperties.FILE_NAME);            
            info(String.format("Properties file created at: %s"
                    ,file.getAbsolutePath()));
            info(String.format("Please edit '%s' to correct aplication function"
                    , TweetMyHomeProperties.FILE_NAME));
            info("Exiting aplication...");
            System.exit(0);
        }
        tweetCount = new TweetMensajeCount(COUNT_FILE);
        tmh_device_xml = XMLFilesManager.getTweetMyHomeDevices();
        if (tmh_device_xml == null) {
            throw new TweetMyHomeException("Fail to read [" 
                    + XMLFilesManager.TWEET_MY_HOME_DEVICES_XML_FILE + "] file");
        }
        tmh_dic_xml = XMLFilesManager.getTweetMyHomeDictionaryCommand();
        if (tmh_dic_xml == null) {
            throw new TweetMyHomeException("Fail to read [" 
                    + XMLFilesManager.TWEET_MY_HOME_DICTIONARY_XML_FILE + "] file");
        }
        tweetDictionary = new TweetStringDictionary(tmh_dic_xml);
        trace("XML Files already Readed [" + XMLFilesManager.TWEET_MY_HOME_DEVICES_XML_FILE 
                + "," + XMLFilesManager.TWEET_MY_HOME_DICTIONARY_XML_FILE + "]");
        
        if (!NetUtil.isConnectedToInternet() && INTERNET_REQUIRED_DEV) {
            throw new TweetMyHomeException("Not Connected to Internet");
        }
        trace("Internet conecction detected");
        trace("Trying to connect to DBMS...");
        db = new TweetMyHomeDatabase(p);
        if (!db.connect()) {
            throw new TweetMyHomeException("Not Connected to DBMS");
        }
        trace("Connected to DBMS");
        db.addTweetMyHomeDevices(tmh_device_xml);

        
        if (RASPBERRY_ON_BOARD) {
            if (p.getValueByKey(Key.arduinoIOBridge).equalsIgnoreCase("true")) {
                throw new TweetMyHomeException("Arduino not supported...");
            } else {
                iob = new RaspberryPiGPIO(tmh_device_xml);
            }
            if (iob != null) {
                iob.addIODeviceListener(this);
                iob.connect();
                trace("GPIO Link established");
                com = new Comunity(false, iob);
                sec = new Security(false, iob);
                //sect = new SecurityThreshhold(sec);
                trace("Security , Security threshold & Comunity initiated");
            }
        } else {
            warn("Raspberry PI GPIO omited. APP prob. won't work well");
        }
        sect = new SecurityThreshhold(sec);
        trace("Setting Twitter OAuth parameters...");
        ConfigurationBuilder cb1 = new ConfigurationBuilder();
        cb1.setDebugEnabled(false)
                .setOAuthConsumerKey("RaTG5hw5OwhQQugghtLthG0ug")
                .setOAuthConsumerSecret("brJ0vcrdGnCHMTxDCmKoThPpJubD6e2xol5WjdO9bBa19nzkXp")
                .setOAuthAccessToken("3236804811-V6MOxfbox4jVylx6pDjkR9UrEpuPzZyOwkaVIWp")
                .setOAuthAccessTokenSecret("33HvizelxmGN700a7pLa6YBKv0l2uTMLoJK593MlXellg");
        ConfigurationBuilder cb2 = new ConfigurationBuilder();
        cb2.setDebugEnabled(false)
                .setOAuthConsumerKey("RaTG5hw5OwhQQugghtLthG0ug")
                .setOAuthConsumerSecret("brJ0vcrdGnCHMTxDCmKoThPpJubD6e2xol5WjdO9bBa19nzkXp")
                .setOAuthAccessToken("3236804811-V6MOxfbox4jVylx6pDjkR9UrEpuPzZyOwkaVIWp")
                .setOAuthAccessTokenSecret("33HvizelxmGN700a7pLa6YBKv0l2uTMLoJK593MlXellg");
        TwitterFactory tf = new TwitterFactory(cb1.build());
        TwitterStreamFactory sf = new TwitterStreamFactory(cb2.build());

        trace("Connecting to Twitter STREAM API...");
        tws = sf.getInstance();
        tws.addListener(this);
        tws.user();
        trace("Connecting to Twitter REST API...");
        tw = tf.getInstance();
        debug("Contructor fi");
        /*----------------NEEDED WORK TO DO-------------------*/

           // integrityCheckSuperAdmin();

        
    }

    private void integrityCheckSuperAdmin() {
        try {
            User superAdmin = tw.showUser(p.getValueByKey(TweetMyHomeProperties.Key.twitterSuperUser));
            long dbSuperAdminId = db.getSuperAdminId();
            if (dbSuperAdminId != TweetMyHomeDatabase.NOT_EXIST) {
                long twitterIdSuperAdmin = superAdmin.getId();
                if (twitterIdSuperAdmin != dbSuperAdminId) {
                    db.setSuperUserId(dbSuperAdminId);
                    debug("Updated Super Admin id from twitter....");
                } else {
                    debug("Super Admin integrity is correct...");
                }
                db.setSuperUserId(LEVEL_NONE);
            } else {
                warn("Super User id in twitter does't found...");
            }
        } catch (TwitterException ex) {
            warn("Super user acount does't exist. verify your config file or check twitter conection", ex);
        }
    }


    private void analyzeAndRespond(User user, TweetStringAnalizer tsa,boolean directMessage) 
            throws TwitterException, TweetMyHomeException {
        String _user = "@"+user.getScreenName();
        TweetFlag.Flag flag = tsa.getFlagTweetFlag().getFlag();
        TweetFlag.Value value = tsa.getFlagTweetFlag().getValue();
        StringBuilder finalTweetStringResponse = new StringBuilder();
        boolean error = false;
        boolean private_message = false;
        boolean informar_admins = false;//************************************************
        switch (flag) {
            case ALARM:
                if (value.equals(TweetFlag.Value.ON)) {
                    if(!sec.isEnabled()){ // activo la alarma
                        finalTweetStringResponse.append(_user).append(" ¡Alarma Activada! ").append(TENDENCE);
                        sec.enable();
                        db.add(new HistorySecurity(sec.isEnabled(), Calendar.getInstance().getTimeInMillis(), user.getId()));
                    }else{ // la alarma ya esta activada. envio mensaje de error privado al usuario
                        private_message = true;
                        finalTweetStringResponse.append("Error,la alarma ya esta activa");
                    }
                } else if (value.equals(TweetFlag.Value.OFF)) {
                    if (sec.isEnabled()) { // desactivo la alarma
                        finalTweetStringResponse.append(_user).append(" ¡Alarma Desactivada! ").append(TENDENCE);
                        sec.disable();
                        db.add(new HistorySecurity(sec.isEnabled(), Calendar.getInstance().getTimeInMillis(), user.getId()));
                    } else { // alarma ya desactivada. envio mensaje de error privado al usuario
                        private_message = true;
                        finalTweetStringResponse.append("Error,la alarma ya esta desactivada");
                    }
                } else if (value.equals(TweetFlag.Value.STATUS)) {
                    private_message =true;
                    if(sec.isEnabled()){
                        finalTweetStringResponse.append("Alarma activa ");
                    }else{
                        finalTweetStringResponse.append("Alarma desactivada ");
                    }
                    finalTweetStringResponse.append(TENDENCE);

                } else {
                    error = true;
                    warn("Alarm value does't founded");
                }
                break;
            case COMUNITY:
                if (value.equals(TweetFlag.Value.ON)) {
                    if(!com.isActivated()){ //activo modo comunitario
                        finalTweetStringResponse.append(_user).append(" ¡Modo comunitario activado! " ).append(TENDENCE);
                        com.activateComunityMode();
                        db.add(new HistoryComunityMode(com.isActivated(), Calendar.getInstance().getTimeInMillis(), user.getId()));
                    }else{
                        private_message = true;
                        finalTweetStringResponse.append("Error, el modo comunitario ya esta activado");
                    }                    
                } else if (value.equals(TweetFlag.Value.OFF)) {
                    if (com.isActivated()) {
                        finalTweetStringResponse.append(_user).append(" ¡Modo comunitario desactivado! ").append(TENDENCE);
                        com.desactivateComunityMode();
                        db.add(new HistoryComunityMode(com.isActivated(), Calendar.getInstance().getTimeInMillis(), user.getId()));
                    } else {
                        private_message = true;
                        finalTweetStringResponse.append( "Error, el modo comunitario ya esta desactivado");
                    }
                } else {
                    error = true;
                    warn("Comunity value does't founded");
                }
                break;
            case USER:
                private_message = true;
                if (value.equals(TweetFlag.Value.ADD)) { // verificar si usuario existe*****************************
                    tsa.getTweetVariableList().forEach(v -> {
                        debug("  Tweet Variables founded");
                        debug("\t" + "[" + v.getVaribleIdentifier() + "]" + v.getVariable());
                    });
                    String newAddUserName = tsa.getTweetVariableList().get(0).getVariable();
                    User twitterUser = tw.showUser(newAddUserName);
                    db.add(new TwitterUser(twitterUser.getId(),
                                    TwitterUserUtil.getTwitterUser(newAddUserName),
                                    UserRol.user,
                                    true));
                    finalTweetStringResponse.append(_user).append(" ¡Usuario Añadido! ").append(TENDENCE);
                } else if (value.equals(TweetFlag.Value.DEL)) {
                    String newDelUser  = tsa.getTweetVariableList().get(0).getVariable(); // solo deberia existir 1 elemento
                    User twitterUser = tw.showUser(newDelUser);
                    db.desactivateUserById(twitterUser.getId());
                    tw.destroyFriendship(twitterUser.getId()); // ver este metodo en accion.. la idea es dejar de seguir*************
                    finalTweetStringResponse.append(_user).append(" ¡Usuario Eliminado! ").append(TENDENCE);
                } else if (value.equals(TweetFlag.Value.MOD)) { // farta*******************************************
                    //diferente
                    finalTweetStringResponse.append(_user).append(" ¡Usuario Modificado! " ).append(TENDENCE);

                } else {
                    error = true;
                    warn("User value does't founded");
                }
                break;
            case NULL:
            default:
                error = true;
                warn("Flag value does't founded");
        }
        debug("analizeAndRespond[finalTweetStringResponse]=" + finalTweetStringResponse.toString());
        if (!error) { // el usuario es la casa misma plix
            finalTweetStringResponse.append(" ").append(tweetCount.getHexHashTagCount());
            if (directMessage) { // respondo  solo al usuario ya que es un mensaje directo
                sendDm(user.getId(),finalTweetStringResponse.toString());
            } else if (private_message) { // es de el tipo privado ej:usuario modificado
                sendDm(user.getId(),finalTweetStringResponse.toString());
            } else if (com.isActivated()) { // en caso de estar modo comunitario lo envio a todos
                sendT(finalTweetStringResponse.toString());
            } else {// Esto solo acurre cuand otengo que informar algo importante pero no esta el modo comunitario activado 
                // envio mensajes privados a todos los usuarios "amigos". a partir de la bd para menor conjestion
                for (TwitterUser u : db.getAllUsers()) { // a que tipo de usuario se lo envio !?!?!??!---------
                    if (u.isActivado()) {
                        sendDm(u.getIdTwitterUser(),finalTweetStringResponse.toString());
                    }
                }
            }
        } else {
            throw new TweetMyHomeException("Error Flag or Values does not match or are invalid");
        }

    }

    private void sendDm(long idDestiny, String text) {
        try {
            long superUserId = db.getSuperAdminId();
            DirectMessage dm = tw.sendDirectMessage(idDestiny, text);
            db.add(new SimpleDirectMessage(this, dm.getId(), superUserId,null, text, Calendar.getInstance().getTimeInMillis()));
            tweetCount.incementCount();
        } catch (TwitterException ex) {
            error(ex.toString(),ex);
        }
    }

    private void sendT(String text) {
        try {
            long superUserId = db.getSuperAdminId();
            Status updateStatus = tw.updateStatus(text);
            SimpleMention sm = new SimpleMention(this, updateStatus.getId(), superUserId,
                    null, text, Calendar.getInstance().getTimeInMillis());
            db.add(sm);
            tweetCount.incementCount();
        } catch (TwitterException ex) {
            error(ex.toString(),ex);
        }
    }

    /**
     * IO DEVICE (HARDWARE) MESSAGE
     * @param device
     */
    @Override
    public void recivedIOEvent(DeviceSensor device) {
        TweetMyHomeDevices.Sensor sensorFired = null;
        for(TweetMyHomeDevices.Sensor s : tmh_device_xml.getSensor()){
            if(s.getAttachedPin().intValue() == device.getPin() ){ // se busca por pin
                sensorFired = s;
                break; // ya que no pueden existir otros sensores
            }
        }
        if(sensorFired != null){
            debug("Evend catched ["+sensorFired.getName()+"] in pin ["+sensorFired.getAttachedPin()+"] activated ["+device.isActivated()+"]");
            int sensorIdByPin = db.getSensorIdByPin(sensorFired.getAttachedPin().longValue());
            if (sensorIdByPin != TweetMyHomeDatabase.NOT_EXIST) {
                HistorySensor historySensor
                        = new HistorySensor(sensorIdByPin, Calendar.getInstance().getTimeInMillis(), device.isActivated() ? 1 : 0, db.getSuperAdminId());
                db.add(historySensor);
                debug(historySensor.toString());
            } else {
                warn("Sensor pin [" + sensorFired.getAttachedPin().longValue() + "] does't founded in DB. event not register");
            }            
            if(sec.isEnabled()){
                sect.estimulate();
                info("Sensor fire ["+sensorFired.getName()+","+sensorFired.getLocation()+"], thread level : " + sect.getRiskByEstimulation().name());
                if(sect.isEstimulationOverThreshold()){ // WARNING
                    warn("Estimulation over threshold !!");
                    if(com.isActivated()){
                        String warnPublicMessage = "Hogar en riesgo ["+sect.getRiskByEstimulation().name()+"] " + TENDENCE + " " + tweetCount.getHexHashTagCount();
                        info("Comunity mode is enabled , Tweeting public message");
                        try {
                            tw.updateStatus(warnPublicMessage);
                            db.add(new SimpleMention(this, sensorIdByPin, sensorIdByPin, TENDENCE, TENDENCE, Calendar.getInstance().getTimeInMillis()));
                        } catch (TwitterException ex) {
                            error(ex.toString(),ex);
                        }
                    }
                    info("Sending private message to all activated users");
                    db.getAllUsers().forEach(u -> {
                        String warnPrivateMessage = "Hogar en riesgo ["+sect.getRiskByEstimulation().name()+"]" + " " + tweetCount.getHexHashTagCount();;
                        if (u.isActivado() && (u.getRol() != UserRol.super_admin) ) {                            
                            try {
                                tw.sendDirectMessage(u.getIdTwitterUser(), warnPrivateMessage);
                            } catch (TwitterException ex) {
                                error(ex.toString(),ex);
                            }
                        }
                    });
                }
            }
            //**************************************************************************************

        } else {
            error("Event sensor [" + device.toString() + "] not founded in Tweetmyhome devices");
        }
    }
    /**
     * funcion se dispara cuando te mencionan sin ser amigo
     * funcion se dipara cuando alguien que sigues habla cualquier wea
     * funcion se dispara cuando la propia casa twittea
     * @param status 
     */
    @Override
    public void onStatus(Status status) {
        User user = status.getUser();
        debug("onStatus @" + status.getUser().getScreenName() + " : " + status.getText());
        if (TwitterUserUtil.equals(status.getUser().getScreenName(), p.getValueByKey(Key.twitterSuperUser))) {
            debug("Own tweet detected... EXITING function");
            return;
        }
        TweetStringAnalizer tsa;
        try {
            tsa = new TweetStringAnalizer(status.getText(),tweetDictionary);            
        } catch (TweetStringException ex) {
            error(ex.toString(),ex);
            return;
        }
        Map<Integer, String> mencionedUsers = tsa.getMencionedUsers();
        if(mencionedUsers != null){
            boolean isHomeMencioned = false;
            for(Map.Entry<Integer, String> entry : mencionedUsers.entrySet()){
                if (TwitterUserUtil.equals(entry.getValue(), p.getValueByKey(Key.twitterSuperUser))) {
                    isHomeMencioned = true;
                    break;
                }
            }
            if (isHomeMencioned) {
                if (!tsa.isErrorFounded()) {
                    try {
                        db.add(new SimpleMention(this, status.getId(),
                                status.getUser().getId(), null, status.getText(), Calendar.getInstance().getTimeInMillis()));
                        analyzeAndRespond(user, tsa, false);
                    } catch (TwitterException | TweetMyHomeException ex) {
                        error(ex.toString(), ex);
                    }
                } else {
                    error(tsa.getErrorString().toString());
                }
            } else{
                debug("Users were mencioned, but not this home...");
            }        
        }else{
            debug("None user Mencioned...");
        }
    }
    
    /**
     * Si me llegan mensajes significa que lo estoy siguiendo...
     * @param directMessage 
     */
    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        String text = directMessage.getText();
        debug("onDirectMessage text: @" + directMessage.getSender().getScreenName() + " : " + text);
        if (TwitterUserUtil.equals(directMessage.getSender().getScreenName(),
                p.getValueByKey(Key.twitterSuperUser))) {
            debug("Own tweet detected...");
            return;
        }

        User user = directMessage.getSender();        
        TweetStringAnalizer tsa;
        try {
            tsa = new TweetStringAnalizer(text, tweetDictionary);
            if (!tsa.isErrorFounded()) {
                db.add(new SimpleDirectMessage(this, directMessage.getId(),
                        directMessage.getSenderId(), null, text, Calendar.getInstance().getTimeInMillis()));
                analyzeAndRespond(user, tsa, true);
            } else { // pasa si no reconose el mensaje
                error(tsa.getErrorString().toString());
            }            
        } catch (TweetStringException | TwitterException | TweetMyHomeException ex) {
            error(ex.toString(), ex);
        }
    }
        /**
     * Se ejecuta al inicio del stream (parece D=)
     * @param friendIds 
     */
    @Override
    public void onFriendList(long[] friendIds) {
        debug("onFriendList");
        try {
            db.processTwiterUsersFromTwitterIds(friendIds, tw);
            //integrityCheckSuperAdmin();
        } catch (TwitterException ex) {
            error(ex.toString(),ex);
        }
    }



    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        debug("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {
        debug("Got a direct message deletion notice id:" + directMessageId);
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        debug("Got a track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        debug("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        debug("Got stall warning:" + warning);
    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        debug("onFavorite source:@"
                + source.getScreenName() + " target:@"
                + target.getScreenName() + " @"
                + favoritedStatus.getUser().getScreenName() + " - "
                + favoritedStatus.getText());
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        debug("onUnFavorite source:@"
                + source.getScreenName() + " target:@"
                + target.getScreenName() + " @"
                + unfavoritedStatus.getUser().getScreenName()
                + " - " + unfavoritedStatus.getText());
    }

    @Override
    public void onFollow(User source, User followedUser) {
        debug("onFollow source:@"
                + source.getScreenName() + " target:@"
                + followedUser.getScreenName());
    }

    @Override
    public void onUnfollow(User source, User followedUser) {
        debug("onUnfollowsource:@"
                + source.getScreenName() + " target:@"
                + followedUser.getScreenName());
    }
    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
        debug("onUserListMemberAddition added member:@"
                + addedMember.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
        debug("onUserListMemberDeleted deleted member:@"
                + deletedMember.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
        debug("onUserListSubscribed subscriber:@"
                + subscriber.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
        debug("onUserListUnsubscribed subscriber:@"
                + subscriber.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserListCreation(User listOwner, UserList list) {
        debug("onUserListCreated  listOwner:@"
                + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserListUpdate(User listOwner, UserList list) {
        debug("onUserListUpdated  listOwner:@"
                + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserListDeletion(User listOwner, UserList list) {
        debug("onUserListDestroyed  listOwner:@"
                + listOwner.getScreenName()
                + " list:" + list.getName());
    }
    @Override
    public void onUserProfileUpdate(User updatedUser) {
        debug("onUserProfileUpdated user:@" + updatedUser.getScreenName());
    }
    @Override
    public void onUserDeletion(long deletedUser) {
        debug("onUserDeletion user:@" + deletedUser);
    }
    @Override
    public void onUserSuspension(long suspendedUser) {
        debug("onUserSuspension user:@" + suspendedUser);
    }
    @Override
    public void onBlock(User source, User blockedUser) {
        debug("onBlock source:@" + source.getScreenName()
                + " target:@" + blockedUser.getScreenName());
    }
    @Override
    public void onUnblock(User source, User unblockedUser) {
        debug("onUnblock source:@" + source.getScreenName()
                + " target:@" + unblockedUser.getScreenName());
    }
    @Override
    public void onException(Exception ex) {
        error("onException:" + ex.getMessage(),ex);
    }
    
    /**
     * Only enter in testing mode
     * @param args
     */
    public static void main(String[] args) {
        try {
            new TweetMyHome();
        } catch (TweetMyHomeException | IOException | TweetStringException ex) {
            error("Exceptions :c",ex);
        }
    }



}
