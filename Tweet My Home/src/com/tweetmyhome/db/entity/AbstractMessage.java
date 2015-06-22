/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

import java.util.Date;
import java.util.Objects;
import java.util.EventObject;

/**
 *
 * @author Klaw Strife
 */
public class AbstractMessage extends EventObject {
    
    private final long messageId;
    private final long userId;
    private final String screenName;
    private final String text;
    private final long createdAt;

    public AbstractMessage(Object source,long messageId,long userId, String screenName, String text, long createdAt) {
        super(source);
        this.messageId = messageId;
        this.userId = userId;
        this.screenName = screenName;
        this.text = text;
        this.createdAt = createdAt;
    }
    
    public long getMessageId(){
        return messageId;
    }

    public long getUserId() {
        return userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getText() {
        return text;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (this.userId ^ (this.userId >>> 32));
        hash = 11 * hash + Objects.hashCode(this.screenName);
        hash = 11 * hash + Objects.hashCode(this.text);
        hash = 11 * hash + Objects.hashCode(this.createdAt);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractMessage other = (AbstractMessage) obj;
        if (this.userId != other.userId) {
            return false;
        }
        if (!Objects.equals(this.screenName, other.screenName)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.createdAt, other.createdAt)) {
            return false;
        }
        return true;
    }
}
