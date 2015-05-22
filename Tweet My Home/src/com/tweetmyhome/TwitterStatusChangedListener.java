/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome;

import com.tweetmyhome.db.entity.AbstractMessage;
import java.util.EventListener;
import java.util.EventObject;

/**
 *
 * @author Klaw Strife
 */

public interface TwitterStatusChangedListener extends EventListener{
    public void recivedAbstractMessage(AbstractMessage am);
}
