/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tweetmyhome.db;

import com.tweetmyhome.db.entity.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.esotericsoftware.minlog.Log.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.tweetmyhome.db.entity.HistoryComunityMode;
import com.tweetmyhome.db.entity.TwitterUser.UserRol;
import com.tweetmyhome.util.TweetMyHomeProperties;
import com.tweetmyhome.util.TweetMyHomeProperties.Key;
import com.tweetmyhome.util.TwitterUserUtil;
import generated.TweetMyHomeDevices;
import generated.TweetMyHomeDevices.Sensor;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * GENERAR ROW AFFECTED POR INSERTS............
 * @author Klaw Strife
 */
public class TweetMyHomeDatabase {
    private static final int NOT_EXIST = -1;
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
    private MysqlDataSource d;
    
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
        if (!conected) {
            try {
//                d = new MysqlDataSource();
//                d.setUser(user);
//                d.setPort(Integer.parseInt(port));
//                d.setDatabaseName(database);
//                d.setServerName(ip);
//                d.setPassword(password);
//                con = d.getConnection();
                String url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
                Class.forName(DRIVER);
                con = DriverManager.getConnection(url, user, password);
                conected = true;
                updateSuperUserByFile();
                setPreparedStatements();
                return conected;
            } catch (SQLException | ClassNotFoundException ex) {
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
                con.prepareStatement("INSERT INTO usuario_twitter VALUES(?,?,?,?)");
        
        selectAllMensajeDirecto = 
                con.prepareStatement("SELECT mensaje_directo.id as 'men_id',usuario_twitter.id as 'use_id',usuario_twitter.usuario,mensaje_directo.texto,mensaje_directo.fecha "
                + "FROM mensaje_directo,usuario_twitter "
                + "WHERE mensaje_directo.usuario_twitter_id = usuario_twitter.id");
        selectAllMenciones = 
                con.prepareStatement("SELECT menciones.id as 'men_id',usuario_twitter.id as 'use_id',usuario_twitter.usuario,menciones.texto,menciones.fecha "
                + "FROM menciones,usuario_twitter "
                + "WHERE menciones.usuario_twitter_id = usuario_twitter.id");
        selectAllUsuarios =
                con.prepareStatement("SELECT usuario_twitter.id,usuario_twitter.usuario,rol_usuario.rol,usuario_twitter.activado "
                        + "FROM usuario_twitter,rol_usuario "
                        + "WHERE usuario_twitter.rol_id = rol_usuario.id");

    }  
    //SimpleMention(Object obj,long messageId,long userId, String screenName, String text, Date createdAt)
    public List<SimpleMention> getAllSimpleMentions() {
        try {
            rs = selectAllMenciones.executeQuery();
            List<SimpleMention> list = new ArrayList<>();
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
            List<SimpleDirectMessage> list = new ArrayList<>();
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
            List<TwitterUser> list = Collections.synchronizedList(new ArrayList<>());
            while (rs.next()) {
                list.add(new TwitterUser(rs.getInt("id"),rs.getString("usuario"),
                        UserRol.valueOf(rs.getString("rol")),rs.getBoolean("activado")));
            }
            return list;
        } catch (SQLException ex) {
            error(null,ex);
        }
        return null;
    }
    private boolean existSensorByPin(int pin) throws SQLException {
        st = con.createStatement();
        rs = st.executeQuery("SELECT id FROM  sensor WHERE pin_adjunto = " + pin);
        return rs.next();
    }

    /**
     * 
     * @param location
     * @return El id de la nueva o antigua ubicacion
     * @throws java.lang.Exception
     */
    public int addLocationIfNotExist(String location) throws Exception {
        int locationId = getLocationIdByName(location);
        if (locationId == NOT_EXIST) {//agrego una ubicacion ya que no existe
            try {
                st = con.createStatement();
                st.execute("INSERT INTO ubicacion VALUES(null,'"+location+"')");
                locationId =  getLocationIdByName(location);
            } catch (SQLException ex) {
                Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(locationId == NOT_EXIST) throw new Exception("QUE WEA ELMANO");
        return locationId;
    }
    
    private int getLocationIdByName(String location) {
        try {
            st =  con.createStatement();
            rs = st.executeQuery("SELECT id FROM ubicacion WHERE nombre = '" + location + "'");
            if(rs.next()){
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return NOT_EXIST;
    }
    
    /**
     * Solo agrega el sensor si no detecta el PIN 
     * @param s 
     */
    
    public void addTweetMyHomeDevices(TweetMyHomeDevices tmhd) {
        tmhd.getSensor().forEach(s -> {
            addSensorIfNotExist(s);
        });
    }
    /**
     *     id INT NOT NULL AUTO_INCREMENT,
            pin_adjunto INT NOT NULL,
            ubicacion_id INT NOT NULL,
            nombre VARCHAR(20) NOT NULL,
            pereodico TINYINT NOT NULL,
            descripcion VARCHAR(70),
     * @param s 
     */
    public void addSensorIfNotExist(Sensor s){
        try {
            if(existSensorByPin(s.getAttachedPin().intValue())){
                debug("["+s.getAttachedPin()+","+s.getName()+"] Already exist... ");
            }else{
                int idLocation = addLocationIfNotExist(s.getLocation());
                PreparedStatement ps = con.prepareStatement("INSERT INTO sensor VALUES (null,?,?,?,?,?)");
                ps.setInt(1,s.getAttachedPin().intValue());
                ps.setInt(2, idLocation);
                ps.setString(3, s.getName());
                ps.setInt(4, s.isRepetitive() ? 1 : 0);
                ps.setString(5, s.getDescription());
                ps.execute();
                debug("Added new Sensor ["+s.getAttachedPin()+","+s.getName()+"]");                
            }
        } catch (SQLException ex) {
            error(null,ex);
        } catch (Exception ex) {
            error(null,ex);
        }
    }
    /**
     * "Elimina" o Agrega usuarios segun lo que contenga la base de datos
     * Se utiliza esta funcion para mantener la integridad de los datos respecto a twitter
     * @param ids 
     * @param tw 
     * @throws twitter4j.TwitterException 
     */
    public void processTwiterUsersFromTwitterIds(long[] ids,Twitter tw) throws TwitterException{
        List<TwitterUser> db_users = getAllUsers();
        for (long id : ids) {
            long founded_id = NOT_EXIST;
            Iterator<TwitterUser> iterator_users = db_users.iterator();            
            while (iterator_users.hasNext()) {
                TwitterUser tuser = iterator_users.next();
                if (!tuser.getRol().equals(UserRol.super_admin)) { // si no es super usuario
                    if (tuser.getIdTwitterUser() == id) { // si coincide la id
                        founded_id = id;
                        debug("User founded in DB :" + id);
                        iterator_users.remove();
                        break;
                    }
                } else { // saco al super usuario de la lista
                    iterator_users.remove();
                }
            }
            if (founded_id == NOT_EXIST) {// no encontrado
                add(new TwitterUser(id, TwitterUserUtil.getTwitterUser(tw.showUser(id).getScreenName()), UserRol.user, true));
                debug("Added new user to DB :" + id);
            }
        }
        db_users.forEach(u -> {
            desactivateUserById(u.getIdTwitterUser());
            debug("Desactivated user :" + u.getIdTwitterUser());
        });
    }
    public boolean existTwitterUserById(long id){
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT id FROM usuario_twitter WHERE id = "+id);
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public void add(TwitterUser u){
        try {
            insertUsuarioTwitter.setLong(1, u.getIdTwitterUser());
            insertUsuarioTwitter.setString(2, u.getUser());
            insertUsuarioTwitter.setInt(3, getRolIdByRol(u.getRol()));
            insertUsuarioTwitter.setBoolean(4, u.isActivado());
            insertUsuarioTwitter.execute();
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

    public void desactivateUserById(long idTwitterUser) {
        try {
            st = con.createStatement();
            st.execute("UPDATE usuario_twitter SET activado = 0 WHERE id = "+idTwitterUser);
        } catch (SQLException ex) {
            Logger.getLogger(TweetMyHomeDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delUserById(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
