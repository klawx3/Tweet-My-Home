/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.tweetmyhome.db.entity.SimpleDirectMessage;
import com.tweetmyhome.db.entity.AbstractMessage;
import com.tweetmyhome.db.entity.SimpleMention;
import com.tweetmyhome.db.TweetMyHomeDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 *
 * @author Klaw Strife
 */
public final class TwitterStatus implements Runnable {

    private final List<SimpleMention> simpleMentionCache;
    private final List<SimpleDirectMessage> simpleDirectMessageCache;
    private TweetMyHomeDatabase db;
    private final Twitter twitter;
    private final long updateRateMillis;
    private List<TwitterStatusChangedListener> listeners;

    public TwitterStatus(TweetMyHomeDatabase db,long updateRateMillis) {
        this.updateRateMillis = updateRateMillis;
        twitter = TwitterFactory.getSingleton();
        //ver si se puede algun tipo de "cantidad" de entradas limite de CACHE
        simpleMentionCache = db.getAllSimpleMentions();
        simpleDirectMessageCache = db.getAllSimpleDirectMessages();
        updateCacheFromInternet();
    }
    
    public List<SimpleMention> getSimpleMentionCache() {
        return simpleMentionCache;
    }

    public List<SimpleDirectMessage> getSimpleDirectMessageCache() {
        return simpleDirectMessageCache;
    }

    /**
     * Funcion que busca la differencia de mensajes locales y externos (net)
     * y arroja la diferencia como nuevos mensajes atraves de listeners
     */
    public synchronized void updateCacheFromInternet() {
        try {

            twitter.getMentionsTimeline().forEach(m -> {
                SimpleMention simpleMention = SimpleMention.getSimpleMention(this,m);
                if (!simpleMentionCache.contains(m)) { //aqui tengo que ver si existe
                    simpleMentionCache.add(simpleMention);
                    db.add(simpleMention);
                    fireTwitterStatusChangedListeners(simpleMention);
                }
            });

            twitter.getDirectMessages().forEach(dm -> {
                SimpleDirectMessage simpleDirectMessage = SimpleDirectMessage.getSimpleMention(this,dm);
                if (!simpleDirectMessageCache.contains(dm)) {
                    simpleDirectMessageCache.add(simpleDirectMessage);
                    db.add(simpleDirectMessage);
                    fireTwitterStatusChangedListeners(simpleDirectMessage);
                }

            });
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterStatus.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        try {
            updateCacheFromInternet();
            Thread.sleep(updateRateMillis);
        } catch (InterruptedException ex) {
            Logger.getLogger(TwitterStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fireTwitterStatusChangedListeners(AbstractMessage obj){
        listeners.forEach(l -> {
            l.recivedAbstractMessage(obj);
        });
    }
    
    public void addTwitterStatusChangedListener(TwitterStatusChangedListener listener){
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }



    

}
