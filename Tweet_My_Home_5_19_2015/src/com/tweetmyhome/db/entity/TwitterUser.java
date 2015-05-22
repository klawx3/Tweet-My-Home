/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db.entity;

/**
 *
 * @author Klaw Strife
 */
public class TwitterUser {

    private final int idTwitterUser;

    private final UserRol rol;
    private final String user;

    public static enum UserRol {

        super_admin, admin, user, none
    }

    public TwitterUser(int idTwitterUser, String user, UserRol rol) {
        this.idTwitterUser = idTwitterUser;
        this.rol = rol;
        this.user = user;
    }

    public UserRol getRol() {
        return rol;
    }

    public String getUser() {
        return user;
    }

    public int getIdTwitterUser() {
        return idTwitterUser;
    }

}
