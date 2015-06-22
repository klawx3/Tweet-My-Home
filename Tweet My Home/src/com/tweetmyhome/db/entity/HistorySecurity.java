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
public class HistorySecurity extends AbstractHistory{

    private final boolean active;
    
    public HistorySecurity(Integer idHistory,boolean active, long date, long twitterUserId) {
        super(idHistory, date, twitterUserId);
        this.active = active;
    }
    public HistorySecurity(boolean active, long date, long twitterUserId) {
        super(null, date, twitterUserId);
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
    
    
    
}
