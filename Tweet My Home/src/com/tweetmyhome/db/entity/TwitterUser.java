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

    private final long idTwitterUser;

    private final UserRol rol;
    private final String user;
    private final boolean activado;

    public static enum UserRol {

        super_admin, admin, user, none
    }

    public TwitterUser(long idTwitterUser, String user, UserRol rol,boolean activado) {
        this.idTwitterUser = idTwitterUser;
        this.rol = rol;
        this.user = user;
        this.activado = activado;
    }

    public UserRol getRol() {
        return rol;
    }

    public String getUser() {
        return user;
    }

    public long getIdTwitterUser() {
        return idTwitterUser;
    }
    
    public boolean isActivado(){
        return activado;
    }

}
