/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

import java.util.Date;
import twitter4j.DirectMessage;

/**
 *
 * @author Klaw Strife
 */
public class SimpleDirectMessage extends AbstractMessage {


    public SimpleDirectMessage(Object obj,long messageId,long userId, String screenName, String text, Date createdAt) {
        super(obj,messageId,userId,screenName,text,createdAt);
    }

    public static SimpleDirectMessage getSimpleMention(Object obj,DirectMessage status) {
        return new SimpleDirectMessage(obj,status.getId(),
                status.getSender().getId(),
                status.getSender().getScreenName(),
                status.getText(),
                status.getCreatedAt()
        );
    }

    

}
