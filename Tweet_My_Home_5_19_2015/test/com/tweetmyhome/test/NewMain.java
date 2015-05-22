/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.test;

import java.util.Map;
import twitter4j.AccountSettings;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Category;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.OEmbed;
import twitter4j.PagableResponseList;
import twitter4j.Place;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.api.HelpResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;



/**
 *
 * @author Klaw Strife
 */
public class NewMain{

    public String hola = "";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AsyncTwitterFactory factory = new AsyncTwitterFactory();
        AsyncTwitter asyncTwitter = factory.getInstance();
        

    }


    
}
