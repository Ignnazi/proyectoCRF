-- ingsof_full_fixed_utf8mb4.sql

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

DROP DATABASE IF EXISTS `ingsof`;
CREATE DATABASE `ingsof` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ingsof`;

-- --------------------------------------------------------
-- TABLAS
-- --------------------------------------------------------

CREATE TABLE `usuario` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) DEFAULT NULL,
  `rol` varchar(45) DEFAULT NULL,
  `password` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `participantecrf` (
  `cod_part` varchar(5) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `correo` varchar(120) DEFAULT NULL,
  `grupo` varchar(10) DEFAULT NULL,
  `fecha_inclusion` timestamp NOT NULL DEFAULT current_timestamp(),
  `id_user` int(11) DEFAULT NULL,
  PRIMARY KEY (`cod_part`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `fk_participante_usuario`
    FOREIGN KEY (`id_user`) REFERENCES `usuario` (`id_user`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sociodemo` (
  `id_socdemo` int(11) NOT NULL AUTO_INCREMENT,
  `edad` int(11) DEFAULT NULL,
  `sexo` varchar(10) DEFAULT NULL,
  `nacionalidad` varchar(45) DEFAULT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `comuna` varchar(60) DEFAULT NULL,
  `ciudad` varchar(60) DEFAULT NULL,
  `zona` varchar(10) DEFAULT NULL,
  `vive_mas_5` varchar(2) DEFAULT NULL,
  `educacion` varchar(15) DEFAULT NULL,
  `ocupacion` varchar(60) DEFAULT NULL,
  `prevision_salud` varchar(30) DEFAULT NULL,
  `prevision_otra` varchar(60) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,
  PRIMARY KEY (`id_socdemo`),
  UNIQUE KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_sociodemo_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `antecedente`;

CREATE TABLE `antecedente` (
  `id_antec` int(11) NOT NULL AUTO_INCREMENT,
  `diagnostico` varchar(2) DEFAULT NULL,
  `fecha_diag` date DEFAULT NULL,
  `fam_cg` varchar(2) DEFAULT NULL,
  `fam_otro` varchar(2) DEFAULT NULL,
  `otro_cancer` varchar(100) DEFAULT NULL,
  `otras_enfermedades` varchar(120) DEFAULT NULL,
  `med_gastro` varchar(2) DEFAULT NULL,
  `med_gastro_cual` varchar(120) DEFAULT NULL,
  `cirugia` varchar(2) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,

  PRIMARY KEY (`id_antec`),
  UNIQUE KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_antecedente_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `antropometria` (
  `id_antrop` int(11) NOT NULL AUTO_INCREMENT,
  `peso` decimal(5,2) DEFAULT NULL,
  `estatura` decimal(4,2) DEFAULT NULL,
  `imc` decimal(4,1) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,
  PRIMARY KEY (`id_antrop`),
  UNIQUE KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_antropometria_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `factor` (
  `id_factor` int(11) NOT NULL AUTO_INCREMENT,
  `carnes` varchar(10) DEFAULT NULL,             
  `salados` varchar(2) DEFAULT NULL,             
  `frutas` varchar(20) DEFAULT NULL,              
  `frituras` varchar(2) DEFAULT NULL,             
  `condimentados` varchar(25) DEFAULT NULL,       
  `bebidas_calientes` varchar(15) DEFAULT NULL,   
  `pesticidas` varchar(2) DEFAULT NULL,           
  `quimicos` varchar(2) DEFAULT NULL,             
  `detalle_quimicos` varchar(100) DEFAULT NULL,   
  `humo_lena` varchar(15) DEFAULT NULL,           
  `fuente_agua` varchar(20) DEFAULT NULL,         
  `fuente_agua_otra` varchar(60) DEFAULT NULL,    
  `tratamiento_agua` varchar(15) DEFAULT NULL,    
  `cod_part` varchar(5) NOT NULL,

  PRIMARY KEY (`id_factor`),
  UNIQUE KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_factor_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `habito` (
  `id_habit` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` varchar(20) DEFAULT NULL,
  `estado` varchar(10) DEFAULT NULL,
  `frecuencia` varchar(25) DEFAULT NULL,
  `cantidad` varchar(25) DEFAULT NULL,
  `anios_consumo` varchar(20) DEFAULT NULL,
  `tiempo_dejado` varchar(20) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,
  `edad_inicio` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_habit`),
  KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_habito_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `helicobacter` (
  `id_helic` int(11) NOT NULL AUTO_INCREMENT,
  `resultado_exam` varchar(12) NOT NULL,
  `pasado_positivo` varchar(12) DEFAULT NULL,
  `pasado_detalle` varchar(150) DEFAULT NULL,
  `tratamiento` varchar(12) DEFAULT NULL,
  `tratamiento_detalle` varchar(150) DEFAULT NULL,
  `tipo_examen` varchar(255) DEFAULT NULL,
  `otro_examen` varchar(80) DEFAULT NULL,
  `antiguedad` varchar(12) DEFAULT NULL,
  `uso_ibp_abx` varchar(12) DEFAULT NULL,
  `repetido` varchar(5) DEFAULT NULL,
  `repetido_fecha` date DEFAULT NULL,
  `repetido_resultado` varchar(12) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,
  PRIMARY KEY (`id_helic`),
  UNIQUE KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_helicobacter_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `histopatologia` (
  `id_histo` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` varchar(20) DEFAULT NULL,
  `localizacion` varchar(20) DEFAULT NULL,
  `estadio` varchar(20) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,
  PRIMARY KEY (`id_histo`),
  UNIQUE KEY `cod_part` (`cod_part`),
  CONSTRAINT `fk_histopatologia_part`
    FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- DATOS
-- --------------------------------------------------------

INSERT INTO `usuario` (`id_user`, `nombre`, `rol`, `password`) VALUES
(1, 'Ana Morales', 'Entrevistadora', 'pwd1'),
(2, 'Luis Soto', 'Entrevistador','pwd2'),
(3, 'Paula Reyes', 'Supervisor','pwd3'),
(4, 'Miguel Torres', 'Entrevistador','pwd4'),
(5, 'Camila Vargas', 'Digitadora','pwd5'),
(6, 'Carlos López', 'Entrevistador','pwd6'),
(7, 'Andrea Díaz', 'Supervisor','pwd7'),
(8, 'Pedro Fuentes', 'Entrevistador','pwd8'),
(9, 'María Rojas', 'Entrevistadora','pwd9'),
(10, 'Javier Peña', 'Coordinador','pwd10'),
(11, 'Ignacia Herrera', 'Entrevistadora','pwd11'),
(12, 'Roberto', 'Profesor','pwd12'),
(13, 'Alucard', 'Colaborador','pwd13'),
(14, 'Joaquin', 'Medico','pwd14');

INSERT INTO `participantecrf`
(`cod_part`, `nombre`, `telefono`, `correo`, `grupo`, `fecha_inclusion`, `id_user`) VALUES
('CS001', 'Juan Pérez',      '+56971234501', 'juan.perez@gmail.com',      'Caso',    '2025-10-24 03:02:22', 1),
('CS002', 'Pedro Ramírez',   '+56971234502', 'pedro.ramirez@gmail.com',   'Caso',    '2025-10-24 03:02:22', 3),
('CS003', 'José Herrera',    '+56971234503', 'jose.herrera@gmail.com',    'Caso',    '2025-10-24 03:02:22', 5),
('CS004', 'Sofía Torres',    '+56971234504', 'sofia.torres@gmail.com',    'Caso',    '2025-10-24 03:02:22', 7),
('CS005', 'Lucía Martínez',  '+56971234505', 'lucia.martinez@gmail.com',  'Caso',    '2025-10-24 03:02:22', 9),
('CT001', 'María González',  '+56971234506', 'maria.gonzalez@gmail.com',  'Control', '2025-10-24 03:02:22', 2),
('CT002', 'Laura Díaz',      '+56971234507', 'laura.diaz@gmail.com',      'Control', '2025-10-24 03:02:22', 4),
('CT003', 'Carolina Vega',   '+56971234508', 'carolina.vega@gmail.com',   'Control', '2025-10-24 03:02:22', 6),
('CT004', 'Diego Castro',    '+56971234509', 'diego.castro@gmail.com',    'Control', '2025-10-24 03:02:22', 8),
('CT005', 'Andrés Fuentes',  '+56971234510', 'andres.fuentes@gmail.com',  'Control', '2025-10-24 03:02:22', 10);


INSERT INTO `sociodemo`
(`id_socdemo`,`edad`,`sexo`,`nacionalidad`,`direccion`,`comuna`,`ciudad`,`zona`,`vive_mas_5`,`educacion`,`ocupacion`,`prevision_salud`,`prevision_otra`,`cod_part`) VALUES
(1, 45, 'Hombre', 'Chilena', 'Av. Libertad 123', 'Viña del Mar', 'Valparaíso', 'Urbana', 'Sí', 'Superior', 'Profesor', 'Fonasa', NULL, 'CS001'),
(2, 38, 'Mujer',  'Chilena', 'Los Robles 22',    'San José',    'Curicó',     'Rural',  'Sí', 'Medio',    'Comerciante', 'Isapre', NULL, 'CT001'),
(3, 60, 'Hombre', 'Chilena', 'San Martín 44',   'Santiago',    'Santiago',   'Urbana', 'Sí', 'Básico',   'Jubilado', 'Fonasa', NULL, 'CS002'),
(4, 27, 'Mujer',  'Chilena', 'Las Rosas 77',    'Ñuñoa',       'Santiago',   'Urbana', 'No', 'Superior', 'Estudiante', 'Sin previsión', NULL, 'CT002'),
(5, 51, 'Hombre', 'Chilena', 'Los Álamos 12',   'Penco',       'Concepción', 'Rural',  'Sí', 'Medio',    'Agricultor', 'Capredena/Dipreca', NULL, 'CS003'),
(6, 33, 'Mujer',  'Chilena', 'Los Pinos 88',    'Rancagua',    'Rancagua',   'Urbana', 'Sí', 'Medio',    'Secretaria', 'Fonasa', NULL, 'CT003'),
(7, 48, 'Mujer',  'Chilena', 'Av. Central 55',  'Ovalle',      'Coquimbo',   'Rural',  'Sí', 'Básico',   'Dueña de casa', 'Fonasa', NULL, 'CS004'),
(8, 39, 'Hombre', 'Chilena', 'Calle Norte 14',  'Maipú',       'Santiago',   'Urbana', 'Sí', 'Superior', 'Ingeniero', 'Isapre', NULL, 'CT004'),
(9, 42, 'Mujer',  'Chilena', 'Calle Sur 9',     'Linares',     'Maule',      'Rural',  'Sí', 'Básico',   'Temporera', 'Otra', 'Particular', 'CS005'),
(10,29, 'Hombre', 'Chilena', 'Las Flores 5',    'Temuco',      'Araucanía',  'Urbana', 'No', 'Superior', 'Analista', 'Fonasa', NULL, 'CT005');

INSERT INTO `antecedente`
(`id_antec`, `diagnostico`, `fecha_diag`, `fam_cg`, `fam_otro`, `otro_cancer`, `otras_enfermedades`, `med_gastro`, `med_gastro_cual`, `cirugia`, `cod_part`) VALUES
(1, 'Sí', '2022-03-01', 'Sí', 'No', '', 'Gastritis crónica', 'Sí', 'Omeprazol', 'No', 'CS001'),
(2, 'No', NULL, '', 'No', '', '', 'No', '', 'No', 'CT001'),
(3, 'Sí', '2020-07-10', 'Sí', 'Sí', 'Colon', 'Úlcera péptica', 'Sí', 'Ibuprofeno', 'Sí', 'CS002'),
(4, 'No', NULL, '', 'No', '', '', 'No', '', 'No', 'CT002'),
(5, 'Sí', '2021-10-25', 'Sí', 'No', '', 'Reflujo', 'Sí', 'Ninguno', 'No', 'CS003'),
(6, 'No', NULL, '', 'No', '', '', 'No', '', 'No', 'CT003'),
(7, 'Sí', '2023-02-17', 'Sí', 'No', '', 'Gastritis', 'Sí', 'Omeprazol', 'No', 'CS004'),
(8, 'No', NULL, '', 'No', '', '', 'No', '', 'No', 'CT004'),
(9, 'Sí', '2022-06-11', 'Sí', 'Sí', 'Hígado', 'Úlcera', 'Sí', 'Paracetamol', 'Sí', 'CS005'),
(10, 'No', NULL, '', 'No', '', '', 'No', '', 'No', 'CT005');

INSERT INTO `antropometria` (`id_antrop`, `peso`, `estatura`, `imc`, `cod_part`) VALUES
(1, 70.50, 1.72, 23.8, 'CS001'),
(2, 63.00, 1.65, 23.1, 'CT001'),
(3, 82.00, 1.75, 26.8, 'CS002'),
(4, 55.00, 1.60, 21.5, 'CT002'),
(5, 90.00, 1.80, 27.8, 'CS003'),
(6, 58.00, 1.62, 22.1, 'CT003'),
(7, 68.00, 1.68, 24.1, 'CS004'),
(8, 75.00, 1.70, 25.9, 'CT004'),
(9, 60.00, 1.55, 24.9, 'CS005'),
(10, 85.00, 1.85, 24.8, 'CT005');

INSERT INTO `factor`
(`id_factor`, `carnes`, `salados`, `frutas`, `frituras`, `condimentados`,
 `bebidas_calientes`, `pesticidas`, `quimicos`, `detalle_quimicos`, `humo_lena`,
 `fuente_agua`, `fuente_agua_otra`, `tratamiento_agua`, `cod_part`) VALUES
(1,  '≥3/sem',   'Sí', 'Media', 'Sí', '1 a 2 veces por semana', 'Frecuentes', 'No', 'No', '',           'Estacional', 'Red',    NULL, 'Filtro',   'CS001'),
(2,  '1–2/sem',  'No', 'Alta',  'No', '3 o más veces por semana', 'Pocas',      'No', 'No', '',           'Nunca',     'Pozo',   NULL, 'Hervir',   'CT001'),
(3,  '<1/sem',   'Sí', 'Baja',  'Sí', '1 a 2 veces por semana', 'Frecuentes', 'Sí', 'Sí', 'Disolventes','Diario',    'Camión', NULL, 'Ninguno',  'CS002'),
(4,  '1–2/sem',  'No', 'Media', 'No', 'Casi nunca / Rara vez', 'Pocas',      'No', 'No', '',           'Nunca',     'Red',    NULL, 'Cloro',    'CT002'),
(5,  '≥3/sem',   'Sí', 'Alta',  'Sí', '1 a 2 veces por semana', 'Medias',     'No', 'Sí', 'Limpieza',   'Estacional','Red',    NULL, 'Filtro',   'CS003'),
(6,  '1–2/sem',  'No', 'Media', 'No', 'Casi nunca / Rara vez', 'Pocas',      'No', 'No', '',           'Nunca',     'Pozo',   NULL, 'Hervir',   'CT003'),
(7,  '<1/sem',   'Sí', 'Baja',  'Sí', '1 a 2 veces por semana', 'Frecuentes', 'Sí', 'Sí', 'Pinturas',   'Diario',    'Camión', NULL, 'Ninguno',  'CS004'),
(8,  '1–2/sem',  'No', 'Alta',  'No', 'Casi nunca / Rara vez', 'Medias',     'No', 'No', '',           'Nunca',     'Red',    NULL, 'Filtro',   'CT004'),
(9,  '≥3/sem',   'Sí', 'Media', 'Sí', '1 a 2 veces por semana', 'Frecuentes', 'No', 'Sí', 'Fertilizantes','Estacional','Red',  NULL, 'Cloro',    'CS005'),
(10, '1–2/sem',  'No', 'Alta',  'No', '3 o más veces por semana', 'Pocas',      'No', 'No', '',           'Nunca',     'Red',    NULL, 'Filtro',   'CT005');


INSERT INTO `habito`
(`id_habit`, `tipo`, `estado`, `frecuencia`, `cantidad`, `anios_consumo`, `tiempo_dejado`, `cod_part`, `edad_inicio`) VALUES
(1, 'Fumar', 'Ex', NULL, 'moderado', '15', '5 años', 'CS001', 17),
(2, 'Beber', 'Actual', 'Regular', 'mucho', '10', '0', 'CT001', NULL),
(3, 'Fumar', 'Nunca', NULL, NULL, NULL, NULL, 'CS002', 22),
(4, 'Beber', 'Ex', 'Ocasional', 'moderado', '5', '2 años', 'CT002', NULL),
(5, 'Beber', 'Actual', 'Regular', 'poco', '10', '0', 'CS003', NULL),
(6, 'Fumar', 'Nunca', NULL, NULL, NULL, NULL, 'CT003', NULL),
(7, 'Fumar', 'Actual', NULL, 'mucho', '20', '0', 'CS004', 17),
(8, 'Beber', 'Actual', 'Regular', 'poco', '5', '0', 'CT004', NULL),
(9, 'Beber', 'Ex', 'Regular', 'moderado', '8', '1 año', 'CS005', NULL),
(10, 'Fumar', 'Nunca', NULL, NULL, NULL, NULL, 'CT005', 22),
(11, 'Beber', 'Actual', 'Regular', 'mucho', '10', '0', 'CS001', NULL);

INSERT INTO `helicobacter` (
  `id_helic`, `resultado_exam`,
  `pasado_positivo`, `pasado_detalle`,
  `tratamiento`, `tratamiento_detalle`,
  `tipo_examen`, `otro_examen`,
  `antiguedad`,
  `uso_ibp_abx`,
  `repetido`, `repetido_fecha`, `repetido_resultado`,
  `cod_part`
) VALUES
(1, 'Positivo', NULL, NULL, NULL, NULL, 'Test de aliento (urea-C13/C14)', NULL, '<1 año', 'No', 'No', NULL, NULL, 'CS001'),
(2, 'Negativo', 'No', NULL, 'No', NULL, 'Antígeno en deposiciones', NULL, '1–5 años', 'No', 'No', NULL, NULL, 'CT001'),
(3, 'Positivo', NULL, NULL, NULL, NULL, 'Histología / Biopsia', NULL, '>5 años', 'No recuerda', 'Sí', '2024-09-10', 'Negativo', 'CS002'),
(4, 'Negativo', 'No recuerda', NULL, 'No', NULL, 'Antígeno en deposiciones', NULL, '1–5 años', 'Sí', 'No', NULL, NULL, 'CT002'),
(5, 'Positivo', NULL, NULL, NULL, NULL, 'Test de aliento (urea-C13/C14)', NULL, '<1 año', 'No', 'Sí', '2025-01-15', 'Positivo', 'CS003'),
(6, 'Negativo', 'No', NULL, 'No', NULL, 'Antígeno en deposiciones', NULL, '1–5 años', 'No', 'No', NULL, NULL, 'CT003'),
(7, 'Positivo', NULL, NULL, NULL, NULL, 'Histología / Biopsia|Test rápido de ureasa', NULL, '>5 años', 'No', 'No', NULL, NULL, 'CS004'),
(8, 'Negativo', 'Sí', '2019 / test de aliento', 'Sí', '2020 / triple terapia (según recuerda)', 'Test de aliento (urea-C13/C14)', NULL, '1–5 años', 'No', 'Sí', '2024-11-02', 'Negativo', 'CT004'),
(9, 'Positivo', NULL, NULL, NULL, NULL, 'Otro', 'Endoscopía/biopsia', '<1 año', 'No', 'No', NULL, NULL, 'CS005'),
(10, 'Negativo', 'No', NULL, 'No', NULL, 'Antígeno en deposiciones', NULL, '1–5 años', 'No', 'No', NULL, NULL, 'CT005');

INSERT INTO `histopatologia` (`id_histo`, `tipo`, `localizacion`, `estadio`, `cod_part`) VALUES
(1, 'Intestinal', 'Antro', 'IIA', 'CS001'),
(3, 'Mixto', 'Cardias', 'IIB', 'CS002'),
(5, 'Difuso', 'Difuso', 'III', 'CS003'),
(7, 'Mixto', 'Cuerpo', 'IIA', 'CS004'),
(9, 'Intestinal', 'Difuso', 'IIIA', 'CS005');

-- --------------------------------------------------------
-- TRIGGERS (después de tener participantecrf creado)
-- --------------------------------------------------------

DELIMITER $$

CREATE TRIGGER `validar_antecedente`
BEFORE INSERT ON `antecedente`
FOR EACH ROW
BEGIN
  DECLARE grupo_part VARCHAR(20);

  SELECT grupo INTO grupo_part
  FROM participantecrf
  WHERE cod_part = NEW.cod_part;

  IF grupo_part <> 'Caso' AND NEW.diagnostico = 'Sí' THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Solo los casos pueden tener diagnóstico histológico.';
  END IF;

  IF grupo_part <> 'Caso' AND NEW.fecha_diag IS NOT NULL THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Solo los casos pueden tener fecha de diagnóstico.';
  END IF;
END$$

CREATE TRIGGER `validar_histopatologia`
BEFORE INSERT ON `histopatologia`
FOR EACH ROW
BEGIN
  DECLARE grupo_part VARCHAR(20);

  SELECT grupo INTO grupo_part
  FROM participantecrf
  WHERE cod_part = NEW.cod_part;

  IF grupo_part IS NULL THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Participante no encontrado en participantecrf.';
  ELSEIF grupo_part <> 'Caso' THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Solo se permiten registros de casos en histopatología.';
  END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------
-- ARREGLO GENERAL: asegurar utf8mb4 en TODO + reparar mojibake SOLO si detecta patrones raros
-- --------------------------------------------------------

SET FOREIGN_KEY_CHECKS=0;

ALTER DATABASE `ingsof` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE `usuario`          CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `participantecrf`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `sociodemo`        CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `antecedente`      CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `antropometria`    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `factor`           CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `habito`           CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `helicobacter`     CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `histopatologia`   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SET SESSION group_concat_max_len = 1000000;

DROP PROCEDURE IF EXISTS fix_mojibake_all;
DELIMITER $$
CREATE PROCEDURE fix_mojibake_all()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE t VARCHAR(255);

  DECLARE cur CURSOR FOR
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'ingsof' AND table_type='BASE TABLE';

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO t;
    IF done = 1 THEN LEAVE read_loop; END IF;

    SELECT GROUP_CONCAT(
      CONCAT('`', column_name, '` = CONVERT(BINARY CONVERT(`', column_name, '` USING latin1) USING utf8mb4)')
      SEPARATOR ', '
    )
    INTO @setlist
    FROM information_schema.columns
    WHERE table_schema='ingsof'
      AND table_name=t
      AND data_type IN ('char','varchar','tinytext','text','mediumtext','longtext','enum','set');

    SELECT GROUP_CONCAT(CONCAT('`', column_name, '`') SEPARATOR ', ')
    INTO @cols
    FROM information_schema.columns
    WHERE table_schema='ingsof'
      AND table_name=t
      AND data_type IN ('char','varchar','tinytext','text','mediumtext','longtext','enum','set');

    IF @setlist IS NOT NULL AND LENGTH(@setlist) > 0 THEN
      SET @sql = CONCAT(
        'UPDATE `ingsof`.`', t, '` SET ', @setlist,
        ' WHERE CONCAT_WS('' '', ', @cols, ') REGEXP ''Ã|Â|â€“|â€”|�'''
      );
      PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
    END IF;
  END LOOP;

  CLOSE cur;
END$$
DELIMITER ;

CALL fix_mojibake_all();
DROP PROCEDURE fix_mojibake_all;

SET FOREIGN_KEY_CHECKS=1;

COMMIT;
