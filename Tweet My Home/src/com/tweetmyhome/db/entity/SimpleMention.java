/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

import java.util.Date;
import java.util.Objects;
import twitter4j.Status;

/**
 *
 * @author Klaw Strife
 */
public class SimpleMention extends AbstractMessage {

    public SimpleMention(Object obj,long messageId,long userId, String screenName, String text, long createdAt) {
        super(obj,messageId,userId, screenName, text, createdAt);
    }

//    public static SimpleMention getSimpleMention(Object obj,Status status) {
//        return new SimpleMention(obj,status.getId(),
//                status.getUser().getId(),
//                status.getUser().getScreenName(),
//                status.getText(),
//                status.getCreatedAt().
//        );
//    }

}
