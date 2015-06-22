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
public class HistoryComunityMode extends HistorySecurity{

    public HistoryComunityMode(Integer idHistory, boolean active, long date, long twitterUserId) {
        super(idHistory, active, date, twitterUserId);
    }
       public HistoryComunityMode(boolean active, long date, long twitterUserId) {
        super(null, active, date, twitterUserId);
    }
    
}
