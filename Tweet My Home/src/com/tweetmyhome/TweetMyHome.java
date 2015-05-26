/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.esotericsoftware.minlog.Log;
import com.tweetmyhome.db.TweetMyHomeDatabase;
import com.tweetmyhome.hardware.Arduino;
import com.tweetmyhome.hardware.IOBridge;
import com.tweetmyhome.util.NetUtil;
import java.util.concurrent.ExecutorService;
import com.tweetmyhome.hardware.ArduinoConfig;
import com.tweetmyhome.util.TweetMyHomeProperties;
import com.tweetmyhome.util.TweetMyHomeProperties.Key;
import java.io.File;
import java.io.IOException;
import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.db.entity.TwitterUser;
import com.tweetmyhome.db.entity.TwitterUser.UserRol;
import com.tweetmyhome.exceptions.TweetMyHomeException;
import com.tweetmyhome.exceptions.TweetStringException;
import com.tweetmyhome.hardware.DeviceSensor;
import com.tweetmyhome.hardware.IODeviceEventListener;
import com.tweetmyhome.logger.MyCustomLogger;
import com.tweetmyhome.util.TwitterUserUtil;
import com.tweetmyhome.xml.XMLFilesManager;
import generated.TweetMyHomeDevices;
import java.util.List;
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


public final class TweetMyHome implements IODeviceEventListener,UserStreamListener{


    private final static String TENDENCE = "#TMH";
    private final static boolean INTERNET_REQUIRED_DEV = true;
    
    private TweetMyHomeDatabase db;
    private TwitterStream tws;
    private Twitter tw ;
    private TweetMyHomeProperties p;
    private IOBridge iob;
    private Security sec;
    private Comunity com;
    private TweetMyHomeDevices tmhd;

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
        tmhd = XMLFilesManager.getTweetMyHomeDevices();
        if (tmhd == null) {
            throw new TweetMyHomeException("Fail to read [" + XMLFilesManager.TWEET_MY_HOME_DEVICES_XML_FILE + "] file");
        }
        trace("XML File Readed...");
        if (!NetUtil.isConnectedToInternet() && INTERNET_REQUIRED_DEV) {
            throw new TweetMyHomeException("Not Connected to Internet");
        }
        trace("Internet conecction detected...");
        db = new TweetMyHomeDatabase(p);
        if (!db.connect()) {
            throw new TweetMyHomeException("Not Connected to DBMS");
        }
        trace("Connected to DBMS");
        db.addTweetMyHomeDevices(tmhd);

        start();

    }

    public void start() throws IOException {
        sec = new Security(false);
        com = new Comunity(false);
        if (p.getValueByKey(Key.arduinoIOBridge).equalsIgnoreCase("true")) {
            iob = new Arduino(new ArduinoConfig(p.getValueByKey(Key.arduinoPort),
                    p.getValueByKey(Key.arduinoEofChar).charAt(0)));
        } else {
            //iob = new RaspberryPiGPIO();
        }
        if (iob != null) {
            iob.addIODeviceListener(this);
            iob.connect();
        }
        trace("Connecting to Twitter STREAM API...");
        tws = new TwitterStreamFactory().getInstance();
        tws.addListener(this);
        tws.user();
        trace("Connecting to Twitter REST API...");
        tw = new TwitterFactory().getInstance();
        debug("'startApp()' function already executed...");
    }

    private void analizeAndRespond(User user, TweetStringAnalizer tsa,boolean directMessage) throws TwitterException {
        String _user = "@"+user.getScreenName();
        TweetFlag.Flag flag = tsa.getFlagTweetFlag().getFlag();
        TweetFlag.Value value = tsa.getFlagTweetFlag().getValue();
        String updateTweetString = null;
        boolean error = false;
        boolean private_message = false;
        switch (flag) {
            case ALARM:
                if (value.equals(TweetFlag.Value.ON)) {
                    updateTweetString = _user + " ¡Alarma Activada! " + TENDENCE;
                } else if (value.equals(TweetFlag.Value.OFF)) {
                    updateTweetString = _user + " ¡Alarma Desactivada! " + TENDENCE;
                } else {
                    error = true;
                }
                break;
            case COMUNITY:
                if (value.equals(TweetFlag.Value.ON)) {
                    updateTweetString = _user + " ¡Modo comunitario activado! " + TENDENCE;
                } else if (value.equals(TweetFlag.Value.OFF)) {
                    updateTweetString = _user + " ¡Modo comunitario desactivado! " + TENDENCE;
                } else {
                    error = true;
                }
                break;
            case USER:
                private_message = true;
                if (value.equals(TweetFlag.Value.ADD)) {
                    String newAddUserName  = tsa.getTweetVariableList().get(0).getVariable();
                    User twitterUser = tw.showUser(newAddUserName);
                    TwitterUser dbts = new TwitterUser(twitterUser.getId(),
                                    TwitterUserUtil.getTwitterUser(newAddUserName),
                                    UserRol.user,
                                    true);
                    db.add(dbts);
                    updateTweetString = _user + " ¡Usuario Añadido! " + TENDENCE;

                } else if (value.equals(TweetFlag.Value.DEL)) {
                    String newDelUser  = tsa.getTweetVariableList().get(0).getVariable();
                    User twitterUser = tw.showUser(newDelUser);
                    db.desactivateUserById(twitterUser.getId());
                    tw.destroyFriendship(twitterUser.getId()); // ver este metodo en accion.. la idea es dejar de seguir--------
                    updateTweetString = _user + " ¡Usuario Eliminado! " + TENDENCE;
                } else if (value.equals(TweetFlag.Value.MOD)) {
                    //diferente
                    
                    updateTweetString = _user + " ¡Usuario Modificado! " + TENDENCE;
                } else {
                    error = true;
                }
                break;
            case NULL:
            default:
                error = true;
        }
        if (!error) {

            if (directMessage) { // respondo al usuario
                tw.sendDirectMessage(user.getId(), updateTweetString);
            } else { // wea
                if (private_message) {
                    tw.sendDirectMessage(user.getId(), updateTweetString);
                } else {
                    tw.updateStatus(updateTweetString);
                }
            }

        }

    }

    /**
     * IO DEVICE (HARDWARE) MESSAGE
     * @param device
     */
    @Override
    public void recivedIOEvent(DeviceSensor device) {
        debug(device.toString());
    }
    /**
     * funcion se dispara cuando te mencionana sin ser amigo
     * funcion se dipara cuando alguien que sigues habla cualquier wea
     * funcion se dispara cuando la propia casa twittea
     * @param status 
     */
    @Override
    public void onStatus(Status status) {
        User user = status.getUser();
        debug("onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
        if (TwitterUserUtil.equals(status.getUser().getScreenName(), p.getValueByKey(Key.twitterSuperUser))) {
            debug("Own tweet detected... EXITING function");
            return;
        }
        TweetStringAnalizer tsa;
        try {
            tsa = new TweetStringAnalizer(status.getText());            
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
            if(isHomeMencioned){
                if(!tsa.isErrorFounded()){
                    try {                    
                        analizeAndRespond(user,tsa,false);
                    } catch (TwitterException ex) {
                        error(ex.toString(),ex);
                    }
                } else {
                    error(tsa.getErrorString().toString());
                }
            }else{
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
        User user = directMessage.getSender();
        debug("onDirectMessage text:" + text);
        TweetStringAnalizer tsa;
        try {
            tsa = new TweetStringAnalizer(text);
            analizeAndRespond(user, tsa, true);
        } catch (TweetStringException | TwitterException ex) {
            error(ex.toString(), ex);
        }
    }
        /**
     * Se ejecuta al inicio del stream [parece D=]
     * @param friendIds 
     */
    @Override
    public void onFriendList(long[] friendIds) {
        debug("onFriendList");
        try {
            db.processTwiterUsersFromTwitterIds(friendIds, tw);
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
        debug("onFollow source:@"
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
