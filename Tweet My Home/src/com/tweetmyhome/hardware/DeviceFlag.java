/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.hardware;

/**
 *
 * @author Klaw Strife
 */
public class DeviceFlag {

    public static final int GET_ALL_SENSORS = 0;
    private final int flag;

    public DeviceFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

}
