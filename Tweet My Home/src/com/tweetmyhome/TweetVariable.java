/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

/**
 *
 * @author Klaw Strife
 */
public class TweetVariable {

    private final String identifier;
    private final String variable;
    //private final TweetFlag Flag;


    public TweetVariable(String identifier, String variable) {
        this.identifier = identifier;
        this.variable = variable;
        
        //GENERAR FLAG !!
    }

    public String getVaribleIdentifier() {
        return identifier;
    }

    public String getVariable() {
        return variable;
    }

//    public TweetFlag getFlag() {
//        return Flag;
//    }


}
