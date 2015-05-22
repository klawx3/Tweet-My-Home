/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db;

import com.tweetmyhome.db.entity.HistorySecurity;
import com.tweetmyhome.db.entity.TwitterUser;
import com.tweetmyhome.db.entity.SimpleMention;
import com.tweetmyhome.db.entity.HistorySensor;
import com.tweetmyhome.db.entity.SimpleDirectMessage;
import com.tweetmyhome.TweetMyHome;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.esotericsoftware.minlog.Log.*;
import com.tweetmyhome.db.entity.HistoryComunityMode;
import com.tweetmyhome.db.entity.TwitterUser.UserRol;
import com.tweetmyhome.util.TweetMyHomeProperties;
import com.tweetmyhome.util.TweetMyHomeProperties.Key;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

/**
 * GENERAR ROW AFFECTED POR INSERTS............
 * @author Klaw Strife
 */
public class TweetMyHomeDatabase {
    private static final String DRIVER = "com.mysql.jdbc.Driver";    
    private Connection con;
    private Statement st;
    private ResultSet rs;
    
    private final String ip;
    private final String user;
    private final String password;
    private final String database;
    private final String port;
    private final TweetMyHomeProperties prop;
    
    private boolean conected;    
    public TweetMyHomeDatabase(TweetMyHomeProperties prop){
        this.prop = prop;
        ip = prop.getValueByKey(TweetMyHomeProperties.Key.databaseIp);
        user = prop.getValueByKey(TweetMyHomeProperties.Key.databaseUser);
        password = prop.getValueByKey(TweetMyHomeProperties.Key.databasePassword);
        database = prop.getValueByKey(TweetMyHomeProperties.Key.databaseName);        
        port = prop.getValueByKey(TweetMyHomeProperties.Key.databasePort);
        conected = false;
    }
    public boolean connect() {
        trace("Trying to connect at DBMS...");
        if (!conected) {
            try {
                String url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
                Class.forName(DRIVER);
                con = DriverManager.getConnection(url, user, password);
                trace("Connected to DBMS");
                conected = true;
                updateSuperUserByFile();
                setPreparedStatements();
                return conected;
            } catch (ClassNotFoundException | SQLException ex) {
                error(ex.toString(),ex);
            }
        }else{
            warn("Already Connected to DBMS");
        }
        return conected;
    }
    public void disconnect() {
        if (conected) {
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                conected = false;
            }
        }
    } 
    public boolean isConnected(){
        return conected;
    }
    private PreparedStatement insertMenciones;
    private PreparedStatement insertMensajeDirecto;
    private PreparedStatement selectAllMenciones;
    private PreparedStatement selectAllMensajeDirecto;
    private PreparedStatement selectAllUsuarios;
    private PreparedStatement insertHistorialSensor;
    private PreparedStatement insertHistorialSeguridad;
    private PreparedStatement insertHistorialComunitario;
    private PreparedStatement insertUsuarioTwitter;
    private void setPreparedStatements() throws SQLException {
        insertMenciones = 
                con.prepareStatement("INSERT INTO menciones VALUES(?,?,?,?)");
        insertHistorialSensor = 
                con.prepareStatement("INSERT INTO historial_sensor VALUES(null,?,?,?,?)");
        insertMensajeDirecto = 
                con.prepareStatement("INSERT INTO mensaje_directo VALUES(?,?,?,?)");
        insertHistorialSeguridad=
                con.prepareStatement("INSERT INTO historial_seguridad VALUES(null,?,?,?)");
        insertHistorialComunitario =
                con.prepareStatement("INSERT INTO historial_comunitario VALUES(null,?,?,?)");
        insertUsuarioTwitter =
                con.prepareStatement("INSERT INTO usuario_twitter VALUES(?,?,?)");
        
        selectAllMensajeDirecto = 
                con.prepareStatement("SELECT mensaje_directo.id as 'men_id',usuario_twitter.id as 'use_id',usuario_twitter.usuario,mensaje_directo.texto,mensaje_directo.fecha "
                + "FROM mensaje_directo,usuario_twitter "
                + "WHERE mensaje_directo.usuario_twitter_id = usuario_twitter.id");
        selectAllMenciones = 
                con.prepareStatement("SELECT menciones.id as 'men_id',usuario_twitter.id as 'use_id',usuario_twitter.usuario,menciones.texto,menciones.fecha "
                + "FROM menciones,usuario_twitter "
                + "WHERE menciones.usuario_twitter_id = usuario_twitter.id");
        selectAllUsuarios =
                con.prepareStatement("SELECT usuario_twitter.id,usuario_twitter.usuario,rol_usuario.rol "
                        + "FROM usuario_twitter,rol_usuario "
                        + "WHERE usuario_twitter.rol_id = rol_usuario.rol");

    }  
    //SimpleMention(Object obj,long messageId,long userId, String screenName, String text, Date createdAt)
    public List<SimpleMention> getAllSimpleMentions() {
        try {
            rs = selectAllMenciones.executeQuery();
            List<SimpleMention> list = new ArrayList();
            while(rs.next()){
                int mencion_id = rs.getInt("men_id");
                int user_id = rs.getInt("use_id");
                String usuario_twitter = rs.getString("usuario");
                String texto = rs.getString("texto");
                Date fecha = rs.getDate("fecha");                
                list.add(new SimpleMention(this,mencion_id,user_id,usuario_twitter,texto,fecha));
            }
            return list;           
        } catch (SQLException ex) {
            error("",ex);
        }
         return null;
    }
    public List<SimpleDirectMessage> getAllSimpleDirectMessages() {
        try {
            rs = selectAllMensajeDirecto.executeQuery();
            List<SimpleDirectMessage> list = new ArrayList();
            while(rs.next()){
                int mencion_id = rs.getInt("men_id");
                int user_id = rs.getInt("use_id");
                String usuario_twitter = rs.getString("usuario");
                String texto = rs.getString("texto");
                Date fecha = rs.getDate("fecha");
                SimpleDirectMessage sm = new SimpleDirectMessage(this,mencion_id,user_id,usuario_twitter,texto,fecha);
                list.add(sm);
            }
            return list;           
        } catch (SQLException ex) {
            error("",ex);
        }
         return null;
    }
    public List<TwitterUser> getAllUsers() {
        try {
            rs = selectAllUsuarios.executeQuery();
            List<TwitterUser> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new TwitterUser(rs.getInt("id"),rs.getString("usuario"),
                        UserRol.valueOf(rs.getString("rol"))));
            }
            return list;
        } catch (SQLException ex) {
            error(null,ex);
        }
        return null;
    }
    
    public void add(TwitterUser u){
        try {
            insertUsuarioTwitter.setInt(1, u.getIdTwitterUser());
            insertUsuarioTwitter.setString(2, u.getUser());
            insertUsuarioTwitter.setInt(3, getRolIdByRol(u.getRol()));
        } catch (SQLException ex) {
            Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void add(HistoryComunityMode h){
            try {
            insertHistorialComunitario.setInt(1, h.isActive() ? 1 : 0 );
            insertHistorialComunitario.setDate(2, (java.sql.Date) h.getDate());
            insertHistorialComunitario.setInt(3, h.getTwitterUserId());
            insertHistorialComunitario.execute();
        } catch (SQLException ex) {
            Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public void add(HistorySecurity hs){
        try {
            insertHistorialSeguridad.setInt(1, hs.isActive() ? 1 : 0 );
            insertHistorialSeguridad.setDate(2, (java.sql.Date) hs.getDate());
            insertHistorialSeguridad.setInt(3, hs.getTwitterUserId());
            insertHistorialSeguridad.execute();
        } catch (SQLException ex) {
            Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void add(SimpleMention m) {
        try {
            insertMenciones.setLong(1, m.getMessageId());
            insertMenciones.setString(2, m.getText());
            insertMenciones.setLong(3, m.getUserId());
            insertMenciones.setDate(4, (java.sql.Date) m.getCreatedAt());
            insertMenciones.execute();
        } catch (SQLException ex) {
            error(null, ex);
        }
    }
    public void add(SimpleDirectMessage dm) {
        try {
            insertMensajeDirecto.setLong(1, dm.getMessageId());
            insertMensajeDirecto.setString(2, dm.getText());
            insertMensajeDirecto.setLong(3, dm.getUserId());
            insertMensajeDirecto.setDate(4, (java.sql.Date) dm.getCreatedAt());
            insertMensajeDirecto.execute();
        } catch (SQLException ex) {
            error(null, ex);
        }
    }
    public void add(HistorySensor hs) {

        try {
            insertHistorialSensor.setInt(1, hs.getIdSensor());
            insertHistorialSensor.setDate(2, (java.sql.Date) hs.getDate());
            insertHistorialSensor.setInt(3, hs.getIdSensor());
            insertHistorialSensor.setInt(4, hs.getIdSensor());
            insertHistorialSensor.execute();
        } catch (SQLException ex) {
            error(null, ex);
        }
    }
    private boolean execute(String sql) {
        if (conected) {
            try {
                st = con.createStatement();
                return st.execute(sql);
            } catch (SQLException ex) {
                error(ex.toString(), ex);
            }

        } else {
            error("Not conected... Query fail:" + sql);
        }
        return false;
    }
    private void updateSuperUserByFile() {
        String su = prop.getValueByKey(Key.twitterSuperUser);
        execute("call setSuperUser('" + su + "')");
        debug("Updated super_user by file...");
    }

    private int getRolIdByRol(UserRol rol) {
        String query = "SELECT rol_usuario.id FROM rol_usuario "
                + "WHERE rol = '" + rol.name() + "'";
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("id");
            }
            warn("Rol [" + rol.name() + "] not found");
        } catch (SQLException ex) {
            error(null, ex);
        }
        return -1;
    }

}
