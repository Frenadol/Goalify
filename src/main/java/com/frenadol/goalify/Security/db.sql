CREATE schema IF NOT EXISTS goalify;
USE goalify;
CREATE TABLE IF NOT EXISTS usuario (
                         id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                         preferences JSON,
                         fechas_rangos_conseguidos JSON,
                         nombre VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         contrasena VARCHAR(255) NOT NULL,
                         fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         foto_perfil TEXT,
                         puntos_totales INT NOT NULL DEFAULT 0,
                         puntos_record INT NOT NULL DEFAULT 0,
                         nivel INT NOT NULL DEFAULT 1,
                         biografia TEXT,
                         fecha_ultimo_ingreso TIMESTAMP,
                         rango VARCHAR(50) NOT NULL DEFAULT 'NOVATO',
                         ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         es_administrador BOOLEAN NOT NULL DEFAULT FALSE,
                         total_desafios_completados INT NOT NULL DEFAULT 0,
                         total_habitos_completados INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS articulo_tienda (
                                 id_articulo INT AUTO_INCREMENT PRIMARY KEY,
                                 nombre VARCHAR(255) NOT NULL,
                                 descripcion TEXT,
                                 tipo_articulo VARCHAR(50) NOT NULL,
                                 valor_articulo TEXT,
                                 costo_puntos INT NOT NULL DEFAULT 0,
                                 imagen_preview_url TEXT,
                                 activo BOOLEAN DEFAULT 1,
                                 fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario_articulo_tienda (
                                         id_usuario_articulo INT AUTO_INCREMENT PRIMARY KEY,
                                         id_usuario INT NOT NULL,
                                         id_articulo INT NOT NULL,
                                         fecha_adquisicion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
                                         FOREIGN KEY (id_articulo) REFERENCES articulo_tienda (id_articulo) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS desafio (
                         id_desafio INT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(255) NOT NULL,
                         descripcion TEXT,
                         fecha_inicio TIMESTAMP NOT NULL,
                         fecha_fin TIMESTAMP NOT NULL,
                         puntos_recompensa INT DEFAULT 0,
                         estado TEXT DEFAULT 'activo',
                         tipo TEXT DEFAULT 'individual',
                         categoria TEXT NOT NULL,
                         fecha_creacion TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         foto_desafio VARCHAR(2048)
);

CREATE TABLE IF NOT EXISTS usuario_desafio (
                                 id_usuario INT NOT NULL,
                                 id_desafio INT NOT NULL,
                                 fecha_inscripcion TIMESTAMP,
                                 estado_participacion TEXT NOT NULL,
                                 notificado_al_usuario BOOLEAN NOT NULL DEFAULT FALSE,
                                 fecha_completado TIMESTAMP,
                                 PRIMARY KEY (id_usuario, id_desafio),
                                 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
                                 FOREIGN KEY (id_desafio) REFERENCES desafio (id_desafio) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS habito (
                        id_habito INT AUTO_INCREMENT PRIMARY KEY,
                        id_usuario INT NOT NULL,
                        nombre VARCHAR(255) NOT NULL,
                        descripcion TEXT,
                        frecuencia VARCHAR(50) NOT NULL,
                        fecha_ultima_completacion DATE,
                        hora_programada VARCHAR(5),
                        estado VARCHAR(20),
                        puntos_recompensa INT,
                        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS estadistica (
                             id_estadistica INT AUTO_INCREMENT PRIMARY KEY,
                             id_usuario INT NOT NULL,
                             id_habito INT NOT NULL,
                             fecha TIMESTAMP,
                             cantidad_completada INT DEFAULT 0,
                             puntos_obtenidos INT DEFAULT 0,
                             FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
                             FOREIGN KEY (id_habito) REFERENCES habito (id_habito) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS logro (
                       id_logro INT AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(255) NOT NULL,
                       descripcion TEXT,
                       puntos INT DEFAULT 0,
                       requisito TEXT,
                       id_usuario INT NOT NULL,
                       fecha_desbloqueo TIMESTAMP,
                       FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE
);