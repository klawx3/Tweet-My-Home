/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

import java.util.Date;

/**
 *
 * @author Klaw Strife
 */
public class AbstractHistory {
    
    private Integer idHistory;
    private long date;
    private long twitterUserId;

    public AbstractHistory(Integer idHistory, long date, long twitterUserId) {
        this.idHistory = idHistory;
        this.date = date;
        this.twitterUserId = twitterUserId;
    }

    
    public Integer getIdHistory() {
        return idHistory;
    }

    public long getDate() {
        return date;
    }

    public long getTwitterUserId() {
        return twitterUserId;
    }
    
    
    
    
}
