/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.util;

/**
 *
 * @author Klaw Strife
 */
public class TwitterUserUtil {

    public static final String TWITTER_USER_CHAR_CONST = "@";

    public static String getTwitterUser(String user) {
        if (!user.startsWith(TWITTER_USER_CHAR_CONST)) {
            return TWITTER_USER_CHAR_CONST + user;
        }
        return user;
    }

    public static boolean equals(String user1, String user2) {
        return getTwitterUser(user1).equalsIgnoreCase(getTwitterUser(user2));
    }

}
