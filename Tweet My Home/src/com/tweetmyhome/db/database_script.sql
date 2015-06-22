CREATE TABLE rol_usuario(
    id INT NOT NULL AUTO_INCREMENT,
    rol VARCHAR(20) UNIQUE NOT NULL,
    descripcion VARCHAR(70),
    PRIMARY KEY (id)
);

/*Those inserts should't be change at all*/
INSERT INTO rol_usuario VALUES(null,'super_admin','Super Administrator , actually the house it self');
INSERT INTO rol_usuario VALUES(null,'admin','Administrator,Able to use all administrate commands');
INSERT INTO rol_usuario VALUES(null,'user','User , Only a passive viewer');
INSERT INTO rol_usuario VALUES(null,'none','Unregistred member');

CREATE TABLE ubicacion(
    id INT NOT NULL AUTO_INCREMENT,
    nombre varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE usuario_twitter(
    id BIGINT NOT NULL,/*twitter id*/
    usuario varchar(20) NOT NULL, /*verificar con lo que soporta twitter*/
    rol_id INT NOT NULL,
    activado TINYINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (usuario),
    INDEX (rol_id),
    FOREIGN KEY (rol_id) REFERENCES rol_usuario(id) /*CASCADE WEA ?*/
);

CREATE TABLE sensor(
    id INT NOT NULL AUTO_INCREMENT,
    pin_adjunto INT NOT NULL,
    ubicacion_id INT NOT NULL,
    nombre VARCHAR(20) NOT NULL,
    pereodico TINYINT NOT NULL,
    descripcion VARCHAR(70),
    
    PRIMARY KEY (id),
    UNIQUE KEY (pin_adjunto),

    INDEX (ubicacion_id),
    FOREIGN KEY (ubicacion_id) REFERENCES ubicacion(id)
);


/*wea*/
CREATE TABLE historial_comunitario(
    id INT NOT NULL AUTO_INCREMENT,
    activa TINYINT NOT NULL,
    fecha DATETIME NOT NULL,
    usuario_twitter_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    INDEX (usuario_twitter_id),
    FOREIGN KEY (usuario_twitter_id) REFERENCES usuario_twitter(id)
);

CREATE TABLE historial_seguridad(
    id INT NOT NULL AUTO_INCREMENT,
    activa TINYINT NOT NULL,
    fecha DATETIME NOT NULL, /*show tables*/
    usuario_twitter_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    INDEX (usuario_twitter_id),
    FOREIGN KEY (usuario_twitter_id) REFERENCES usuario_twitter(id)
);

/*
Deve ingresarse el id correspondiente a: el id de la mencion de twitter
*/
CREATE TABLE mensaje_directo(
    id BIGINT NOT NULL,
    texto varchar(70) NOT NULL,
    usuario_twitter_id BIGINT NOT NULL,
    fecha DATETIME,
    PRIMARY KEY (id),

    INDEX(usuario_twitter_id),
    FOREIGN KEY (usuario_twitter_id) REFERENCES usuario_twitter(id)
);

/*
Deve ingresarse el id correspondiente a: el id de la mencion de twitter
*/
CREATE TABLE menciones(
    id BIGINT NOT NULL, 
    texto varchar(70) NOT NULL,
    usuario_twitter_id BIGINT NOT NULL,
    fecha DATETIME,
    PRIMARY KEY (id),

    INDEX(usuario_twitter_id),
    FOREIGN KEY (usuario_twitter_id) REFERENCES usuario_twitter(id)
);

CREATE TABLE historial_sensor(
    id INT NOT NULL AUTO_INCREMENT,
    sensor_id INT NOT NULL,
    fecha DATETIME NOT NULL,
    valor TINYINT NOT NULL,
    usuario_twitter_id BIGINT NOT NULL,
    PRIMARY KEY (id),

    INDEX(sensor_id),
    INDEX(usuario_twitter_id),
    FOREIGN KEY (sensor_id) REFERENCES sensor(id),
    FOREIGN KEY (usuario_twitter_id) REFERENCES usuario_twitter(id)  
);


/*Only 1 super allowed
Example: call setSuperUser('@MyHose');
*/
DELIMITER //
CREATE PROCEDURE setSuperUser(IN __usuario__ VARCHAR(20))
BEGIN
    DECLARE _idsuperadmin BIGINT DEFAULT NULL;
    DECLARE _superuserid BIGINT DEFAULT NULL;
    SET _superuserid := (SELECT rol_usuario.id FROM rol_usuario WHERE rol_usuario.rol = 'super_admin');
    SET _idsuperadmin  := (SELECT usuario_twitter.id FROM usuario_twitter,rol_usuario WHERE usuario_twitter.rol_id = rol_usuario.id AND rol_usuario.rol = 'super_admin');
    IF _idsuperadmin IS NULL THEN
        INSERT INTO usuario_twitter VALUES(0,__usuario__,_superuserid,1);
    ELSE
        UPDATE usuario_twitter SET usuario_twitter.usuario = __usuario__ WHERE usuario_twitter.id = _idsuperadmin;
    END IF;

END //
DELIMITER ;


/*
delete from usuario_twitter

DROP PROCEDURE setSuperUser;
SELECT * FROM usuario_twitter,rol_usuario WHERE usuario_twitter.rol_id = rol_usuario.id;

SELECT usuario_twitter.id FROM usuario_twitter,rol_usuario WHERE usuario_twitter.rol_id = rol_usuario.id AND rol_usuario.rol = 'super_admin'
SELECT rol_usuario.id FROM rol_usuario WHERE rol_usuario.rol = 'super_admin'

select * from rol_usuario

*/

/*DROPING ALL
SET FOREIGN_KEY_CHECKS=0; 
DROP TABLE historial_sensor;
DROP TABLE menciones;
DROP TABLE mensaje_directo;
DROP TABLE historial_seguridad;
DROP TABLE sensor;
DROP TABLE usuario_twitter;
DROP TABLE ubicacion;
DROP TABLE rol_usuario;
DROP TABLE historial_comunitario;
SET FOREIGN_KEY_CHECKS=1; 

SHOW ENGINE INNODB STATUS;
*/

/*TESTING
SELECT * FROM rol_usuario;

select * from usuario_twitter;
call setSuperUser('wea');
*/