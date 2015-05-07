SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

CREATE DATABASE IF NOT EXISTS `BizkaiaTransportesApp` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `BizkaiaTransportesApp`;

CREATE TABLE IF NOT EXISTS `favoritos` (
  `usuario` varchar(50) NOT NULL DEFAULT '',
  `linea` varchar(10) NOT NULL DEFAULT '',
  PRIMARY KEY (`usuario`,`linea`),
  KEY `linea` (`linea`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `favoritos` (`usuario`, `linea`) VALUES('egoitzpuerta1992@gmail.com', '1');
INSERT INTO `favoritos` (`usuario`, `linea`) VALUES('maitechupuerta@gmail.com', '1');
INSERT INTO `favoritos` (`usuario`, `linea`) VALUES('maitechupuerta@gmail.com', '2');
INSERT INTO `favoritos` (`usuario`, `linea`) VALUES('egoitzpuerta1992@gmail.com', '3517A');
INSERT INTO `favoritos` (`usuario`, `linea`) VALUES('maitechupuerta@gmail.com', '3517A');

CREATE TABLE IF NOT EXISTS `Lineas` (
  `tipo` varchar(10) DEFAULT NULL,
  `numero` varchar(10) NOT NULL,
  `Descripcion` varchar(100) DEFAULT NULL,
  `frecuencia` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`numero`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Lineas` (`tipo`, `numero`, `Descripcion`, `frecuencia`) VALUES('M', '1', 'Plentzia-Etxebarri', '30');
INSERT INTO `Lineas` (`tipo`, `numero`, `Descripcion`, `frecuencia`) VALUES('M', '1-a', 'Sopelana-Etxebarri', '30');
INSERT INTO `Lineas` (`tipo`, `numero`, `Descripcion`, `frecuencia`) VALUES('M', '1-b', 'Urduliz-Etxebarri', '30');
INSERT INTO `Lineas` (`tipo`, `numero`, `Descripcion`, `frecuencia`) VALUES('M', '2', 'Santurtzi-Basauri', '10');
INSERT INTO `Lineas` (`tipo`, `numero`, `Descripcion`, `frecuencia`) VALUES('B', '3517A', 'Bilbao-Mungia', '30');
INSERT INTO `Lineas` (`tipo`, `numero`, `Descripcion`, `frecuencia`) VALUES('B', '3517B', 'Mungia-Bilbao', '30');

CREATE TABLE IF NOT EXISTS `paradas` (
  `id` varchar(10) NOT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `lat` varchar(20) DEFAULT NULL,
  `lon` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('1', 'Moyua', '43.26322', '-2.9345675');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('2', 'Basauri', '43.2364438', '-2.8932898');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('3', 'Santurtzi', '43.3285809', '-3.0295063');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('4', 'Plentzia', '43.4013224', '-2.9462969');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('5', 'Urduliz', '43.3788615', '-2.9585855');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('6', 'Sopelana', '43.3802353', '-2.9789949');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('7', 'Derio', '43.2917562', '-2.888678');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('8', 'Mungia', '43.3449177', '-2.8542201');
INSERT INTO `paradas` (`id`, `direccion`, `lat`, `lon`) VALUES('9', 'San Ignacio', '43.2806636', '-2.9618617');

CREATE TABLE IF NOT EXISTS `recorridos` (
  `Lnum` varchar(10) NOT NULL DEFAULT '',
  `Pid` varchar(10) NOT NULL DEFAULT '',
  `numParLinea` int(11) DEFAULT NULL,
  `tiempo` int(11) DEFAULT NULL,
  PRIMARY KEY (`Lnum`,`Pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1', '1', 2, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1', '2', 1, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1', '4', 6, 0);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1', '5', 4, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1', '6', 5, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1', '9', 3, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-a', '1', 2, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-a', '2', 1, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-a', '5', 3, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-a', '6', 4, 0);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-b', '1', 2, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-b', '2', 1, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('1-b', '5', 3, 0);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('2', '1', 2, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('2', '2', 1, 5);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('2', '3', 3, 0);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('3517A', '1', 1, 15);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('3517A', '7', 2, 15);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('3517A', '8', 3, 0);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('3517B', '1', 3, 0);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('3517B', '7', 2, 15);
INSERT INTO `recorridos` (`Lnum`, `Pid`, `numParLinea`, `tiempo`) VALUES('3517B', '8', 1, 15);

CREATE TABLE IF NOT EXISTS `usuarios` (
  `usuario` varchar(50) NOT NULL DEFAULT '',
  `idGCM` longtext,
  PRIMARY KEY (`usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `usuarios` (`usuario`, `idGCM`) VALUES('egoitzpuerta1992@gmail.com', 'APA91bEiud8lBKuDZ_T_hYtaDFFQiqUrmRttBLqDrXg3lzVcpZeQkDZWi8AROE55Mo-CeGaGI1w2nQh1GCBRUJLXKMNbn7VogFA6uDEkSgqQdorY24tW_PPeX5Ksdnd_56kqaAFlK91mSKo4Cpwcvm9U3K0N05ui_ItwXDWiKEsqhakp-hsa4Dc');
INSERT INTO `usuarios` (`usuario`, `idGCM`) VALUES('maitechupuerta@gmail.com', 'APA91bHQZ4fnffwrIss38ay7RziNvMyHewcEvm3ydCs1yPjuX4B2OkcPQxZ_7WrdyVwiRyIyRAfB4gvXmNwq2CWo8mANF__05xfTcz-mfBqi3lVCg3gfhpBo7euGg8zluWlJd6Nh5mgf9NU-O14MqLzruncktVIgRgVrxNZxZpNxXUqyCs-ZqEg');


ALTER TABLE `favoritos`
  ADD CONSTRAINT `favoritos_ibfk_1` FOREIGN KEY (`usuario`) REFERENCES `usuarios` (`usuario`),
  ADD CONSTRAINT `favoritos_ibfk_2` FOREIGN KEY (`linea`) REFERENCES `Lineas` (`numero`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
