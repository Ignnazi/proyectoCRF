-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 06-11-2025 a las 07:50:04
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `ingsof`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `antecedente`
--

CREATE TABLE `antecedente` (
  `id_antec` int(11) NOT NULL,
  `diagnostico` varchar(2) DEFAULT NULL,
  `fecha_diag` date DEFAULT NULL,
  `fam_cg` varchar(2) DEFAULT NULL,
  `fam_otro` varchar(2) DEFAULT NULL,
  `otro_cancer` varchar(100) DEFAULT NULL,
  `otras_enfermedades` varchar(120) DEFAULT NULL,
  `medicamentos` varchar(120) DEFAULT NULL,
  `cirugia` varchar(2) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `antecedente`
--

INSERT INTO `antecedente` (`id_antec`, `diagnostico`, `fecha_diag`, `fam_cg`, `fam_otro`, `otro_cancer`, `otras_enfermedades`, `medicamentos`, `cirugia`, `cod_part`) VALUES
(1, 'Sí', '2022-03-01', 'Sí', 'No', '', 'Gastritis crónica', 'Omeprazol', 'No', 'CS001'),
(2, 'No', NULL, 'No', 'No', '', '', '', 'No', 'CT001'),
(3, 'Sí', '2020-07-10', 'Sí', 'Sí', 'Colon', 'Úlcera péptica', 'Ibuprofeno', 'Sí', 'CS002'),
(4, 'No', NULL, 'No', 'No', '', '', '', 'No', 'CT002'),
(5, 'Sí', '2021-10-25', 'Sí', 'No', '', 'Reflujo', 'Ninguno', 'No', 'CS003'),
(6, 'No', NULL, 'No', 'No', '', '', '', 'No', 'CT003'),
(7, 'Sí', '2023-02-17', 'Sí', 'No', '', 'Gastritis', 'Omeprazol', 'No', 'CS004'),
(8, 'No', NULL, 'No', 'No', '', '', '', 'No', 'CT004'),
(9, 'Sí', '2022-06-11', 'Sí', 'Sí', 'Hígado', 'Úlcera', 'Paracetamol', 'Sí', 'CS005'),
(10, 'No', NULL, 'No', 'No', '', '', '', 'No', 'CT005');

--
-- Disparadores `antecedente`
--
DELIMITER $$
CREATE TRIGGER `validar_antecedente` BEFORE INSERT ON `antecedente` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `antropometria`
--

CREATE TABLE `antropometria` (
  `id_antrop` int(11) NOT NULL,
  `peso` decimal(5,2) DEFAULT NULL,
  `estatura` decimal(4,2) DEFAULT NULL,
  `imc` decimal(4,1) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `antropometria`
--

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `factor`
--

CREATE TABLE `factor` (
  `id_factor` int(11) NOT NULL,
  `carnes` varchar(10) DEFAULT NULL,
  `salados` varchar(2) DEFAULT NULL,
  `frutas` varchar(15) DEFAULT NULL,
  `frituras` varchar(2) DEFAULT NULL,
  `bebidas_calientes` varchar(15) DEFAULT NULL,
  `pesticidas` varchar(2) DEFAULT NULL,
  `quimicos` varchar(2) DEFAULT NULL,
  `detalle_quimicos` varchar(100) DEFAULT NULL,
  `humo_lena` varchar(15) DEFAULT NULL,
  `fuente_agua` varchar(20) DEFAULT NULL,
  `tratamiento_agua` varchar(15) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `factor`
--

INSERT INTO `factor` (`id_factor`, `carnes`, `salados`, `frutas`, `frituras`, `bebidas_calientes`, `pesticidas`, `quimicos`, `detalle_quimicos`, `humo_lena`, `fuente_agua`, `tratamiento_agua`, `cod_part`) VALUES
(1, '≥3/sem', 'Sí', 'Media', 'Sí', 'Frecuentes', 'No', 'No', '', 'Estacional', 'Red', 'Filtro', 'CS001'),
(2, '1–2/sem', 'No', 'Alta', 'No', 'Pocas', 'No', 'No', '', 'Nunca', 'Pozo', 'Hervir', 'CT001'),
(3, '<1/sem', 'Sí', 'Baja', 'Sí', 'Frecuentes', 'Sí', 'Sí', 'Disolventes', 'Diario', 'Camión', 'Ninguno', 'CS002'),
(4, '1–2/sem', 'No', 'Media', 'No', 'Pocas', 'No', 'No', '', 'Nunca', 'Red', 'Cloro', 'CT002'),
(5, '≥3/sem', 'Sí', 'Alta', 'Sí', 'Medias', 'No', 'Sí', 'Limpieza', 'Estacional', 'Red', 'Filtro', 'CS003'),
(6, '1–2/sem', 'No', 'Media', 'No', 'Pocas', 'No', 'No', '', 'Nunca', 'Pozo', 'Hervir', 'CT003'),
(7, '<1/sem', 'Sí', 'Baja', 'Sí', 'Frecuentes', 'Sí', 'Sí', 'Pinturas', 'Diario', 'Camión', 'Ninguno', 'CS004'),
(8, '1–2/sem', 'No', 'Alta', 'No', 'Medias', 'No', 'No', '', 'Nunca', 'Red', 'Filtro', 'CT004'),
(9, '≥3/sem', 'Sí', 'Media', 'Sí', 'Frecuentes', 'No', 'Sí', 'Fertilizantes', 'Estacional', 'Red', 'Cloro', 'CS005'),
(10, '1–2/sem', 'No', 'Alta', 'No', 'Pocas', 'No', 'No', '', 'Nunca', 'Red', 'Filtro', 'CT005');

-- --------------------------------------------------------
-- OJO: genotipo ELIMINADO (estructura + inserts + índices + FK)
-- --------------------------------------------------------

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habito`
--

CREATE TABLE `habito` (
  `id_habit` int(11) NOT NULL,
  `tipo` varchar(20) DEFAULT NULL,
  `estado` varchar(10) DEFAULT NULL,
  `frecuencia` varchar(25) DEFAULT NULL,
  `cantidad` varchar(25) DEFAULT NULL,
  `anios_consumo` varchar(20) DEFAULT NULL,
  `tiempo_dejado` varchar(20) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL,
  `edad_inicio` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `habito`
--

INSERT INTO `habito` (`id_habit`, `tipo`, `estado`, `frecuencia`, `cantidad`, `anios_consumo`, `tiempo_dejado`, `cod_part`, `edad_inicio`) VALUES
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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `helicobacter`
-- (REEMPLAZADA por la versión extendida)
--

CREATE TABLE `helicobacter` (
  `id_helic` int(11) NOT NULL,
  `resultado_exam` varchar(12) NOT NULL,              -- Positivo / Negativo / Desconocido

  `pasado_positivo` varchar(12) DEFAULT NULL,         -- Sí / No / No recuerda
  `pasado_detalle` varchar(150) DEFAULT NULL,         -- "año aprox / tipo examen"

  `tratamiento` varchar(12) DEFAULT NULL,             -- Sí / No / No recuerda
  `tratamiento_detalle` varchar(150) DEFAULT NULL,    -- "año / esquema"

  `tipo_examen` varchar(255) DEFAULT NULL,            -- múltiple: "A|B|C"
  `otro_examen` varchar(80) DEFAULT NULL,             -- si incluye "Otro"

  `antiguedad` varchar(12) DEFAULT NULL,              -- <1 año / 1–5 años / >5 años

  `uso_ibp_abx` varchar(12) DEFAULT NULL,             -- Sí / No / No recuerda

  `repetido` varchar(5) DEFAULT NULL,                 -- Sí / No
  `repetido_fecha` date DEFAULT NULL,
  `repetido_resultado` varchar(12) DEFAULT NULL,      -- Positivo / Negativo / Desconocido

  `cod_part` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `helicobacter`
-- (nuevos datos coherentes con la tabla extendida)
--

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
(1, 'Positivo',
NULL, NULL,
 NULL, NULL,
 'Test de aliento (urea-C13/C14)', NULL,
 '<1 año',
 'No',
 'No', NULL, NULL,
 'CS001'),

(2, 'Negativo',
 'No', NULL,
 'No', NULL,
 'Antígeno en deposiciones', NULL,
 '1–5 años',
 'No',
 'No', NULL, NULL,
 'CT001'),

(3, 'Positivo',
 NULL, NULL,
 NULL, NULL,
 'Histología / Biopsia', NULL,
 '>5 años',
 'No recuerda',
 'Sí', '2024-09-10', 'Negativo',
 'CS002'),

(4, 'Negativo',
 'No recuerda', NULL,
 'No', NULL,
 'Antígeno en deposiciones', NULL,
 '1–5 años',
 'Sí',
 'No', NULL, NULL,
 'CT002'),

(5, 'Positivo',
 NULL, NULL,
 NULL, NULL,
 'Test de aliento (urea-C13/C14)', NULL,
 '<1 año',
 'No',
 'Sí', '2025-01-15', 'Positivo',
 'CS003'),

(6, 'Negativo',
 'No', NULL,
 'No', NULL,
 'Antígeno en deposiciones', NULL,
 '1–5 años',
 'No',
 'No', NULL, NULL,
 'CT003'),

(7, 'Positivo',
 NULL, NULL,
 NULL, NULL,
 'Histología / Biopsia|Test rápido de ureasa', NULL,
 '>5 años',
 'No',
 'No', NULL, NULL,
 'CS004'),

(8, 'Negativo',
 'Sí', '2019 / test de aliento',
 'Sí', '2020 / triple terapia (según recuerda)',
 'Test de aliento (urea-C13/C14)', NULL,
 '1–5 años',
 'No',
 'Sí', '2024-11-02', 'Negativo',
 'CT004'),

(9, 'Positivo',
 NULL, NULL,
 NULL, NULL,
 'Otro', 'Endoscopía/biopsia',
 '<1 año',
 'No',
 'No', NULL, NULL,
 'CS005'),

(10, 'Negativo',
 'No', NULL,
 'No', NULL,
 'Antígeno en deposiciones', NULL,
 '1–5 años',
 'No',
 'No', NULL, NULL,
 'CT005');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `histopatologia`
--

CREATE TABLE `histopatologia` (
  `id_histo` int(11) NOT NULL,
  `tipo` varchar(20) DEFAULT NULL,
  `localizacion` varchar(20) DEFAULT NULL,
  `estadio` varchar(20) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `histopatologia`
--

INSERT INTO `histopatologia` (`id_histo`, `tipo`, `localizacion`, `estadio`, `cod_part`) VALUES
(1, 'Intestinal', 'Antro', 'IIA', 'CS001'),
(3, 'Mixto', 'Cardias', 'IIB', 'CS002'),
(5, 'Difuso', 'Difuso', 'III', 'CS003'),
(7, 'Mixto', 'Cuerpo', 'IIA', 'CS004'),
(9, 'Intestinal', 'Difuso', 'IIIA', 'CS005');

--
-- Disparadores `histopatologia`
--
DELIMITER $$
CREATE TRIGGER `validar_histopatologia` BEFORE INSERT ON `histopatologia` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `participantecrf`
--

CREATE TABLE `participantecrf` (
  `cod_part` varchar(5) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `grupo` varchar(10) DEFAULT NULL,
  `fecha_inclusion` timestamp NOT NULL DEFAULT current_timestamp(),
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `participantecrf`
--

INSERT INTO `participantecrf` (`cod_part`, `nombre`, `grupo`, `fecha_inclusion`, `id_user`) VALUES
('CS001', 'Juan Pérez', 'Caso', '2025-10-24 03:02:22', 1),
('CS002', 'Pedro Ramírez', 'Caso', '2025-10-24 03:02:22', 3),
('CS003', 'José Herrera', 'Caso', '2025-10-24 03:02:22', 5),
('CS004', 'Sofía Torres', 'Caso', '2025-10-24 03:02:22', 7),
('CS005', 'Lucía Martínez', 'Caso', '2025-10-24 03:02:22', 9),
('CT001', 'María González', 'Control', '2025-10-24 03:02:22', 2),
('CT002', 'Laura Díaz', 'Control', '2025-10-24 03:02:22', 4),
('CT003', 'Carolina Vega', 'Control', '2025-10-24 03:02:22', 6),
('CT004', 'Diego Castro', 'Control', '2025-10-24 03:02:22', 8),
('CT005', 'Andrés Fuentes', 'Control', '2025-10-24 03:02:22', 10);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sociodemo`
--

CREATE TABLE `sociodemo` (
  `id_socdemo` int(11) NOT NULL,
  `edad` int(11) DEFAULT NULL,
  `sexo` varchar(10) DEFAULT NULL,
  `nacionalidad` varchar(45) DEFAULT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `zona` varchar(10) DEFAULT NULL,
  `anios_res` varchar(10) DEFAULT NULL,
  `educacion` varchar(15) DEFAULT NULL,
  `ocupacion` varchar(60) DEFAULT NULL,
  `cod_part` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `sociodemo`
--

INSERT INTO `sociodemo` (`id_socdemo`, `edad`, `sexo`, `nacionalidad`, `direccion`, `zona`, `anios_res`, `educacion`, `ocupacion`, `cod_part`) VALUES
(1, 45, 'Hombre', 'Chilena', 'Av. Libertad 123', 'Urbana', '>10', 'Superior', 'Profesor', 'CS001'),
(2, 38, 'Mujer', 'Chilena', 'Los Robles 22', 'Rural', '5–10', 'Medio', 'Comerciante', 'CT001'),
(3, 60, 'Hombre', 'Chilena', 'San Martín 44', 'Urbana', '>10', 'Básico', 'Jubilado', 'CS002'),
(4, 27, 'Mujer', 'Chilena', 'Las Rosas 77', 'Urbana', '<5', 'Superior', 'Estudiante', 'CT002'),
(5, 51, 'Hombre', 'Chilena', 'Los Álamos 12', 'Rural', '>10', 'Medio', 'Agricultor', 'CS003'),
(6, 33, 'Mujer', 'Chilena', 'Los Pinos 88', 'Urbana', '5–10', 'Medio', 'Secretaria', 'CT003'),
(7, 48, 'Mujer', 'Chilena', 'Av. Central 55', 'Rural', '>10', 'Básico', 'Dueña de casa', 'CS004'),
(8, 39, 'Hombre', 'Chilena', 'Calle Norte 14', 'Urbana', '5–10', 'Superior', 'Ingeniero', 'CT004'),
(9, 42, 'Mujer', 'Chilena', 'Calle Sur 9', 'Rural', '>10', 'Básico', 'Temporera', 'CS005'),
(10, 29, 'Hombre', 'Chilena', 'Las Flores 5', 'Urbana', '<5', 'Superior', 'Analista', 'CT005');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_user` int(11) NOT NULL,
  `nombre` varchar(60) DEFAULT NULL,
  `rol` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_user`, `nombre`, `rol`) VALUES
(1, 'Ana Morales', 'Entrevistadora'),
(2, 'Luis Soto', 'Entrevistador'),
(3, 'Paula Reyes', 'Supervisor'),
(4, 'Miguel Torres', 'Entrevistador'),
(5, 'Camila Vargas', 'Digitadora'),
(6, 'Carlos López', 'Entrevistador'),
(7, 'Andrea Díaz', 'Supervisor'),
(8, 'Pedro Fuentes', 'Entrevistador'),
(9, 'María Rojas', 'Entrevistadora'),
(10, 'Javier Peña', 'Coordinador'),
(11, 'Ignacia Herrera', 'Entrevistadora'),
(12, 'Roberto', 'Profesor'),
(13, 'Alucard', 'Colaborador'),
(14, 'Joaquin', 'Medico');

-- --------------------------------------------------------

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `antecedente`
--
ALTER TABLE `antecedente`
  ADD PRIMARY KEY (`id_antec`),
  ADD UNIQUE KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `antropometria`
--
ALTER TABLE `antropometria`
  ADD PRIMARY KEY (`id_antrop`),
  ADD UNIQUE KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `factor`
--
ALTER TABLE `factor`
  ADD PRIMARY KEY (`id_factor`),
  ADD UNIQUE KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `habito`
--
ALTER TABLE `habito`
  ADD PRIMARY KEY (`id_habit`),
  ADD KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `helicobacter`
--
ALTER TABLE `helicobacter`
  ADD PRIMARY KEY (`id_helic`),
  ADD UNIQUE KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `histopatologia`
--
ALTER TABLE `histopatologia`
  ADD PRIMARY KEY (`id_histo`),
  ADD UNIQUE KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `participantecrf`
--
ALTER TABLE `participantecrf`
  ADD PRIMARY KEY (`cod_part`),
  ADD KEY `id_user` (`id_user`);

--
-- Indices de la tabla `sociodemo`
--
ALTER TABLE `sociodemo`
  ADD PRIMARY KEY (`id_socdemo`),
  ADD UNIQUE KEY `cod_part` (`cod_part`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_user`);

-- --------------------------------------------------------

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `antecedente`
--
ALTER TABLE `antecedente`
  MODIFY `id_antec` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `antropometria`
--
ALTER TABLE `antropometria`
  MODIFY `id_antrop` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `factor`
--
ALTER TABLE `factor`
  MODIFY `id_factor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `habito`
--
ALTER TABLE `habito`
  MODIFY `id_habit` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `helicobacter`
--
ALTER TABLE `helicobacter`
  MODIFY `id_helic` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `histopatologia`
--
ALTER TABLE `histopatologia`
  MODIFY `id_histo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `sociodemo`
--
ALTER TABLE `sociodemo`
  MODIFY `id_socdemo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

-- --------------------------------------------------------

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `antecedente`
--
ALTER TABLE `antecedente`
  ADD CONSTRAINT `fk_antecedente_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `antropometria`
--
ALTER TABLE `antropometria`
  ADD CONSTRAINT `fk_antropometria_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `factor`
--
ALTER TABLE `factor`
  ADD CONSTRAINT `fk_factor_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `habito`
--
ALTER TABLE `habito`
  ADD CONSTRAINT `fk_habito_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `helicobacter`
--
ALTER TABLE `helicobacter`
  ADD CONSTRAINT `fk_helicobacter_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `histopatologia`
--
ALTER TABLE `histopatologia`
  ADD CONSTRAINT `fk_histopatologia_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `participantecrf`
--
ALTER TABLE `participantecrf`
  ADD CONSTRAINT `fk_participante_usuario` FOREIGN KEY (`id_user`) REFERENCES `usuario` (`id_user`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `sociodemo`
--
ALTER TABLE `sociodemo`
  ADD CONSTRAINT `fk_sociodemo_part` FOREIGN KEY (`cod_part`) REFERENCES `participantecrf` (`cod_part`) ON DELETE CASCADE ON UPDATE CASCADE;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
