-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: happy_feet_veterinaria
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cita_estados`
--

DROP TABLE IF EXISTS `cita_estados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cita_estados` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` text,
  `es_activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cita_estados`
--

LOCK TABLES `cita_estados` WRITE;
/*!40000 ALTER TABLE `cita_estados` DISABLE KEYS */;
INSERT INTO `cita_estados` VALUES (1,'Pendiente','Cita programada pendiente de atención',1,'2025-09-26 08:20:03'),(2,'Atendida','Cita completada',1,'2025-09-26 08:20:03'),(3,'Cancelada','Cita anulada por el cliente o veterinario',0,'2025-09-26 08:20:03');
/*!40000 ALTER TABLE `cita_estados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `citas`
--

DROP TABLE IF EXISTS `citas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `citas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) NOT NULL,
  `mascota_id` int NOT NULL,
  `servicio_id` int NOT NULL,
  `veterinario_id` int NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `motivo_consulta` text NOT NULL,
  `estado_id` int NOT NULL DEFAULT '1',
  `urgencia` enum('Baja','Media','Alta') DEFAULT 'Baja',
  `notas_previas` text,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  UNIQUE KEY `unique_cita_veterinario` (`veterinario_id`,`fecha_hora`),
  KEY `servicio_id` (`servicio_id`),
  KEY `idx_fecha` (`fecha_hora`),
  KEY `idx_estado` (`estado_id`),
  KEY `idx_veterinario` (`veterinario_id`),
  KEY `idx_mascota` (`mascota_id`),
  CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`mascota_id`) REFERENCES `mascotas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`),
  CONSTRAINT `citas_ibfk_3` FOREIGN KEY (`veterinario_id`) REFERENCES `veterinarios` (`id`),
  CONSTRAINT `citas_ibfk_4` FOREIGN KEY (`estado_id`) REFERENCES `cita_estados` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `citas`
--

LOCK TABLES `citas` WRITE;
/*!40000 ALTER TABLE `citas` DISABLE KEYS */;
INSERT INTO `citas` VALUES (1,'CIT-1758997534955',3,1,2,'2025-09-27 21:40:00','se corto',1,'Baja',NULL,'2025-09-27 18:25:34','2025-09-27 18:25:34'),(2,'CIT-1759017242130',3,1,1,'2025-09-28 15:20:00','checqueo de rutina',1,'Baja',NULL,'2025-09-27 23:54:02','2025-09-27 23:54:02'),(5,'CIT-1759075973863',2,1,2,'2025-09-30 15:10:00','prueba',1,'Baja',NULL,'2025-09-28 16:12:53','2025-09-28 16:12:53'),(7,'CIT-1759141421343',2,1,2,'2025-10-10 15:40:00','prueba',1,'Baja',NULL,'2025-09-29 10:23:41','2025-09-29 10:23:41');
/*!40000 ALTER TABLE `citas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `duenos`
--

DROP TABLE IF EXISTS `duenos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `duenos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(255) NOT NULL,
  `documento_identidad` varchar(20) NOT NULL,
  `direccion` text,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `contacto_emergencia` varchar(20) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `tipo_sangre` varchar(5) DEFAULT NULL,
  `alergias` text,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `documento_identidad` (`documento_identidad`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_documento` (`documento_identidad`),
  KEY `idx_email` (`email`),
  KEY `idx_nombre` (`nombre_completo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duenos`
--

LOCK TABLES `duenos` WRITE;
/*!40000 ALTER TABLE `duenos` DISABLE KEYS */;
INSERT INTO `duenos` VALUES (1,'carlos cisneros','1098765',NULL,'3133609617','cisneroscarlos@gmail.com','31550014789',NULL,NULL,NULL,'2025-09-26 08:27:13','2025-09-26 08:27:13'),(2,'Juan','1098765432','calle11','310859999','juan@gmail.com','315099876678',NULL,NULL,NULL,'2025-09-26 19:15:29','2025-09-26 19:15:29'),(5,'Cliente Prueba BD','DNI-DBTEST','Calle Falsa 123','3000000000','dbtest.dueno@happyfeet.local',NULL,NULL,NULL,NULL,'2025-09-28 02:35:10','2025-09-28 02:35:10');
/*!40000 ALTER TABLE `duenos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `especies`
--

DROP TABLE IF EXISTS `especies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `especies`
--

LOCK TABLES `especies` WRITE;
/*!40000 ALTER TABLE `especies` DISABLE KEYS */;
INSERT INTO `especies` VALUES (1,'Canino','Perros de todas las razas','2025-09-26 08:18:38'),(2,'Felino','Gatos domésticos','2025-09-26 08:18:38'),(3,'Ave','Aves de compañía como loros y canarios','2025-09-26 08:18:38');
/*!40000 ALTER TABLE `especies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evento_tipos`
--

DROP TABLE IF EXISTS `evento_tipos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evento_tipos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `requiere_diagnostico` tinyint(1) DEFAULT '0',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evento_tipos`
--

LOCK TABLES `evento_tipos` WRITE;
/*!40000 ALTER TABLE `evento_tipos` DISABLE KEYS */;
INSERT INTO `evento_tipos` VALUES (1,'Consulta general','Revisión médica general',1,'2025-09-26 08:19:37'),(2,'Vacunación','Aplicación de vacunas',0,'2025-09-26 08:19:37'),(3,'Desparasitación','Control de parásitos internos y externos',0,'2025-09-26 08:19:37');
/*!40000 ALTER TABLE `evento_tipos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facturas`
--

DROP TABLE IF EXISTS `facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_factura` varchar(20) NOT NULL,
  `dueno_id` int NOT NULL,
  `fecha_emision` datetime NOT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `impuestos` decimal(10,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `estado` enum('Pendiente','Pagada','Cancelada','Vencida') DEFAULT 'Pendiente',
  `forma_pago` enum('Efectivo','Tarjeta Débito','Tarjeta Crédito','Transferencia') DEFAULT 'Efectivo',
  `observaciones` text,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_factura` (`numero_factura`),
  KEY `idx_dueno` (`dueno_id`),
  KEY `idx_fecha` (`fecha_emision`),
  KEY `idx_estado` (`estado`),
  KEY `idx_numero` (`numero_factura`),
  CONSTRAINT `facturas_ibfk_1` FOREIGN KEY (`dueno_id`) REFERENCES `duenos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturas`
--

LOCK TABLES `facturas` WRITE;
/*!40000 ALTER TABLE `facturas` DISABLE KEYS */;
INSERT INTO `facturas` VALUES (1,'FAC001',1,'2025-09-20 10:00:00','2025-10-20',75.00,7.50,0.00,82.50,'Pagada','Tarjeta Crédito','Pago exitoso en línea','2025-09-27 14:49:10','2025-09-27 14:49:10'),(2,'FAC002',2,'2025-09-22 15:30:00','2025-10-22',200.00,20.00,10.00,210.00,'Pagada','Efectivo','Cliente pagará en próxima visita','2025-09-27 14:49:10','2025-09-29 19:56:12'),(4,'FACT-1759026910262',5,'2025-09-28 02:35:10','2025-10-27',0.00,0.00,0.00,0.00,'Pendiente','Efectivo',NULL,'2025-09-28 02:35:10','2025-09-28 02:35:10'),(5,'FACT-1759027577018',5,'2025-09-28 02:46:17','2025-10-27',54000.00,10260.00,0.00,64260.00,'Pagada','Efectivo',NULL,'2025-09-28 02:46:17','2025-09-28 02:46:17'),(6,'FACT-1759027969411-1',1,'2025-09-28 02:52:49','2025-10-27',265000.00,45600.00,0.00,310600.00,'Cancelada','Efectivo',NULL,'2025-09-28 02:53:32','2025-09-28 16:20:51'),(7,'FACT-1759028148263-1',1,'2025-09-28 02:55:48','2025-10-27',0.00,0.00,0.00,0.00,'Cancelada','Efectivo',NULL,'2025-09-28 02:56:09','2025-09-28 16:18:32'),(8,'FACT-1759173215331-1',1,'2025-09-29 19:13:35','2025-10-29',50000.00,0.00,0.00,50000.00,'Pendiente','Efectivo',NULL,'2025-09-29 19:13:50','2025-09-29 19:13:50');
/*!40000 ALTER TABLE `facturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_medico`
--

DROP TABLE IF EXISTS `historial_medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_medico` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mascota_id` int NOT NULL,
  `cita_id` int DEFAULT NULL,
  `evento_tipo_id` int NOT NULL,
  `fecha_evento` datetime NOT NULL,
  `veterinario_id` int NOT NULL,
  `temperatura` decimal(4,2) DEFAULT NULL,
  `frecuencia_cardiaca` int DEFAULT NULL,
  `frecuencia_respiratoria` int DEFAULT NULL,
  `peso` decimal(5,2) DEFAULT NULL,
  `sintomas` text,
  `diagnostico` text,
  `tratamiento_prescrito` text,
  `medicamentos_recetados` text,
  `observaciones` text,
  `recomendaciones` text,
  `fecha_proximo_control` date DEFAULT NULL,
  `requiere_seguimiento` tinyint(1) DEFAULT '0',
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cita_id` (`cita_id`),
  KEY `evento_tipo_id` (`evento_tipo_id`),
  KEY `idx_mascota` (`mascota_id`),
  KEY `idx_fecha` (`fecha_evento`),
  KEY `idx_veterinario` (`veterinario_id`),
  CONSTRAINT `historial_medico_ibfk_1` FOREIGN KEY (`mascota_id`) REFERENCES `mascotas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `historial_medico_ibfk_2` FOREIGN KEY (`cita_id`) REFERENCES `citas` (`id`),
  CONSTRAINT `historial_medico_ibfk_3` FOREIGN KEY (`evento_tipo_id`) REFERENCES `evento_tipos` (`id`),
  CONSTRAINT `historial_medico_ibfk_4` FOREIGN KEY (`veterinario_id`) REFERENCES `veterinarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_medico`
--

LOCK TABLES `historial_medico` WRITE;
/*!40000 ALTER TABLE `historial_medico` DISABLE KEYS */;
/*!40000 ALTER TABLE `historial_medico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) NOT NULL,
  `nombre_producto` varchar(255) NOT NULL,
  `producto_tipo_id` int NOT NULL,
  `proveedor_id` int DEFAULT NULL,
  `descripcion` text,
  `fabricante` varchar(100) DEFAULT NULL,
  `lote` varchar(50) DEFAULT NULL,
  `ubicacion` varchar(100) DEFAULT NULL,
  `cantidad_stock` int NOT NULL DEFAULT '0',
  `stock_minimo` int NOT NULL DEFAULT '5',
  `stock_maximo` int DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `precio_compra` decimal(10,2) DEFAULT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `requiere_refrigeracion` tinyint(1) DEFAULT '0',
  `controlado` tinyint(1) DEFAULT '0',
  `activo` tinyint(1) DEFAULT '1',
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `producto_tipo_id` (`producto_tipo_id`),
  KEY `proveedor_id` (`proveedor_id`),
  KEY `idx_nombre` (`nombre_producto`),
  KEY `idx_vencimiento` (`fecha_vencimiento`),
  KEY `idx_stock` (`cantidad_stock`),
  KEY `idx_activo` (`activo`),
  CONSTRAINT `inventario_ibfk_1` FOREIGN KEY (`producto_tipo_id`) REFERENCES `producto_tipos` (`id`),
  CONSTRAINT `inventario_ibfk_2` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
INSERT INTO `inventario` VALUES (2,'PROD002','Alimento premium para gatos',2,2,'Bolsa de 10kg para gatos adultos','PetFood Co.','L002','Bodega B',47,5,100,'2026-02-10',90.00,175.00,0,0,1,'2025-09-27 14:50:17','2025-09-28 15:43:24'),(3,'PROD004','hibermentina',1,NULL,NULL,NULL,NULL,NULL,400,5,NULL,NULL,NULL,30.00,0,0,1,'2025-09-28 01:47:30','2025-09-28 01:50:14'),(6,'PRD-1759026910133','Amoxicilina 500mg',1,NULL,NULL,NULL,NULL,NULL,50,5,NULL,'2026-03-27',NULL,12000.00,0,0,1,'2025-09-28 02:35:10','2025-09-28 02:35:10'),(7,'PRD-1759027576906','Amoxicilina 500mg',1,NULL,NULL,NULL,NULL,NULL,30,5,NULL,'2026-03-27',NULL,12000.00,0,0,1,'2025-09-28 02:46:16','2025-09-28 02:53:10'),(8,'PRD-1759139305173','SMOKE-Test-Producto',1,NULL,NULL,NULL,NULL,NULL,10,5,NULL,'2025-10-09',NULL,9999.00,0,0,1,'2025-09-29 09:48:25','2025-09-29 09:48:25'),(9,'PRD-1759139305570','SMOKE-Test-Producto',1,NULL,NULL,NULL,NULL,NULL,10,5,NULL,'2025-10-09',NULL,9999.00,0,0,1,'2025-09-29 09:48:25','2025-09-29 09:48:25'),(10,'PROD10010','PRUEBA',1,NULL,NULL,NULL,NULL,NULL,300,5,NULL,NULL,NULL,20.00,0,0,1,'2025-09-29 12:48:51','2025-09-29 12:48:51');
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items_factura`
--

DROP TABLE IF EXISTS `items_factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items_factura` (
  `id` int NOT NULL AUTO_INCREMENT,
  `factura_id` int NOT NULL,
  `tipo_item` enum('servicio','producto') NOT NULL,
  `servicio_id` int DEFAULT NULL,
  `producto_id` int DEFAULT NULL,
  `descripcion` varchar(500) NOT NULL,
  `cantidad` decimal(10,3) NOT NULL DEFAULT '1.000',
  `precio_unitario` decimal(10,2) NOT NULL,
  `descuento_item` decimal(10,2) DEFAULT '0.00',
  `subtotal` decimal(10,2) NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `servicio_id` (`servicio_id`),
  KEY `producto_id` (`producto_id`),
  KEY `idx_factura` (`factura_id`),
  CONSTRAINT `items_factura_ibfk_1` FOREIGN KEY (`factura_id`) REFERENCES `facturas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `items_factura_ibfk_2` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`),
  CONSTRAINT `items_factura_ibfk_3` FOREIGN KEY (`producto_id`) REFERENCES `inventario` (`id`),
  CONSTRAINT `items_factura_chk_1` CHECK ((((`tipo_item` = _utf8mb4'servicio') and (`servicio_id` is not null)) or ((`tipo_item` = _utf8mb4'producto') and (`producto_id` is not null))))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items_factura`
--

LOCK TABLES `items_factura` WRITE;
/*!40000 ALTER TABLE `items_factura` DISABLE KEYS */;
INSERT INTO `items_factura` VALUES (3,5,'servicio',6,NULL,'Consulta general',1.000,30000.00,0.00,30000.00,'2025-09-28 02:46:17'),(4,5,'producto',NULL,7,'Venta producto inventario',2.000,12000.00,0.00,24000.00,'2025-09-28 02:46:17');
/*!40000 ALTER TABLE `items_factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mascotas`
--

DROP TABLE IF EXISTS `mascotas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mascotas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dueno_id` int NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `raza_id` int NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `sexo` enum('Macho','Hembra') NOT NULL,
  `color` varchar(100) DEFAULT NULL,
  `senias_particulares` text,
  `url_foto` varchar(500) DEFAULT NULL,
  `alergias` text,
  `condiciones_preexistentes` text,
  `peso_actual` decimal(5,2) DEFAULT NULL,
  `microchip` varchar(50) DEFAULT NULL,
  `fecha_implantacion_microchip` date DEFAULT NULL,
  `agresivo` tinyint(1) DEFAULT '0',
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `microchip` (`microchip`),
  KEY `raza_id` (`raza_id`),
  KEY `idx_dueno` (`dueno_id`),
  KEY `idx_nombre` (`nombre`),
  KEY `idx_microchip` (`microchip`),
  CONSTRAINT `mascotas_ibfk_1` FOREIGN KEY (`dueno_id`) REFERENCES `duenos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mascotas_ibfk_2` FOREIGN KEY (`raza_id`) REFERENCES `razas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mascotas`
--

LOCK TABLES `mascotas` WRITE;
/*!40000 ALTER TABLE `mascotas` DISABLE KEYS */;
INSERT INTO `mascotas` VALUES (2,1,'lobo',1,NULL,'Macho',NULL,NULL,NULL,NULL,NULL,NULL,'98788',NULL,NULL,'2025-09-27 13:14:21','2025-09-27 17:57:05'),(3,1,'Luna',1,NULL,'Hembra',NULL,NULL,NULL,NULL,NULL,NULL,'12212',NULL,NULL,'2025-09-27 13:59:07','2025-09-27 13:59:07'),(5,1,'petra',1,NULL,'Hembra',NULL,NULL,NULL,NULL,NULL,NULL,'11211',NULL,NULL,'2025-09-27 17:56:01','2025-09-27 17:56:01'),(6,2,'firulais',1,NULL,'Macho',NULL,NULL,NULL,NULL,NULL,NULL,'22222',NULL,NULL,'2025-09-28 15:39:31','2025-09-28 15:39:31');
/*!40000 ALTER TABLE `mascotas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos`
--

DROP TABLE IF EXISTS `pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `factura_id` int NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha_pago` datetime DEFAULT CURRENT_TIMESTAMP,
  `metodo_pago` enum('Efectivo','Tarjeta Débito','Tarjeta Crédito','Transferencia') NOT NULL,
  `referencia_pago` varchar(100) DEFAULT NULL,
  `estado` enum('Completado','Pendiente','Fallido') DEFAULT 'Completado',
  `observaciones` text,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_factura` (`factura_id`),
  KEY `idx_fecha` (`fecha_pago`),
  CONSTRAINT `pagos_ibfk_1` FOREIGN KEY (`factura_id`) REFERENCES `facturas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagos`
--

LOCK TABLES `pagos` WRITE;
/*!40000 ALTER TABLE `pagos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto_tipos`
--

DROP TABLE IF EXISTS `producto_tipos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_tipos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_tipos`
--

LOCK TABLES `producto_tipos` WRITE;
/*!40000 ALTER TABLE `producto_tipos` DISABLE KEYS */;
INSERT INTO `producto_tipos` VALUES (1,'Medicamento','Productos farmacéuticos para mascotas','2025-09-26 08:19:15'),(2,'Alimento','Comida balanceada para mascotas','2025-09-26 08:19:15'),(3,'Accesorio','Juguetes, collares y otros accesorios','2025-09-26 08:19:15');
/*!40000 ALTER TABLE `producto_tipos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `ruc` varchar(20) DEFAULT NULL,
  `contacto` varchar(255) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `direccion` text,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ruc` (`ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
INSERT INTO `proveedores` VALUES (1,'Distribuidora VetCare','1234567890','Carlos Ramírez','3001234567','contacto@vetcare.com','Calle 123 #45-67',1,'2025-09-26 08:20:20','2025-09-26 08:20:20'),(2,'Mascotas & Cia','9876543210','Ana López','3019876543','ventas@mascotascia.com','Carrera 45 #89-10',1,'2025-09-26 08:20:20','2025-09-26 08:20:20');
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puntos_cliente`
--

DROP TABLE IF EXISTS `puntos_cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puntos_cliente` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dueno_id` int NOT NULL,
  `puntos_acumulados` int NOT NULL DEFAULT '0',
  `puntos_redimidos` int NOT NULL DEFAULT '0',
  `nivel` enum('Bronce','Plata','Oro','Platino') DEFAULT 'Bronce',
  `fecha_ultima_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dueno_id` (`dueno_id`),
  KEY `idx_dueno` (`dueno_id`),
  KEY `idx_nivel` (`nivel`),
  CONSTRAINT `puntos_cliente_ibfk_1` FOREIGN KEY (`dueno_id`) REFERENCES `duenos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puntos_cliente`
--

LOCK TABLES `puntos_cliente` WRITE;
/*!40000 ALTER TABLE `puntos_cliente` DISABLE KEYS */;
/*!40000 ALTER TABLE `puntos_cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `razas`
--

DROP TABLE IF EXISTS `razas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `razas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `especie_id` int NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `caracteristicas` text,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_raza_especie` (`especie_id`,`nombre`),
  CONSTRAINT `razas_ibfk_1` FOREIGN KEY (`especie_id`) REFERENCES `especies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `razas`
--

LOCK TABLES `razas` WRITE;
/*!40000 ALTER TABLE `razas` DISABLE KEYS */;
INSERT INTO `razas` VALUES (1,1,'Labrador Retriever','Amigable, activo y sociable','2025-09-26 08:18:56'),(2,1,'Bulldog','Calmado y cariñoso','2025-09-26 08:18:56'),(3,2,'Persa','Pelo largo, temperamento tranquilo','2025-09-26 08:18:56'),(4,2,'Siames','Activo, vocal y curioso','2025-09-26 08:18:56'),(5,3,'Canario','Ave pequeña y colorida','2025-09-26 08:18:56');
/*!40000 ALTER TABLE `razas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recordatorios`
--

DROP TABLE IF EXISTS `recordatorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recordatorios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo` enum('vacuna','desparasitacion','control','cita','pago') NOT NULL,
  `mascota_id` int DEFAULT NULL,
  `dueno_id` int DEFAULT NULL,
  `cita_id` int DEFAULT NULL,
  `fecha_recordatorio` datetime NOT NULL,
  `mensaje` text NOT NULL,
  `enviado` tinyint(1) DEFAULT '0',
  `fecha_envio` timestamp NULL DEFAULT NULL,
  `metodo_envio` enum('email','sms','sistema') DEFAULT 'sistema',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `mascota_id` (`mascota_id`),
  KEY `dueno_id` (`dueno_id`),
  KEY `cita_id` (`cita_id`),
  KEY `idx_fecha` (`fecha_recordatorio`),
  KEY `idx_tipo` (`tipo`),
  KEY `idx_enviado` (`enviado`),
  CONSTRAINT `recordatorios_ibfk_1` FOREIGN KEY (`mascota_id`) REFERENCES `mascotas` (`id`),
  CONSTRAINT `recordatorios_ibfk_2` FOREIGN KEY (`dueno_id`) REFERENCES `duenos` (`id`),
  CONSTRAINT `recordatorios_ibfk_3` FOREIGN KEY (`cita_id`) REFERENCES `citas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recordatorios`
--

LOCK TABLES `recordatorios` WRITE;
/*!40000 ALTER TABLE `recordatorios` DISABLE KEYS */;
/*!40000 ALTER TABLE `recordatorios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text,
  `precio` decimal(10,2) NOT NULL,
  `duracion_estimada` int DEFAULT '30' COMMENT 'minutos',
  `categoria` enum('Consulta','Procedimiento','Cirugía','Estética') DEFAULT 'Consulta',
  `requiere_equipo_especial` tinyint(1) DEFAULT '0',
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios`
--

LOCK TABLES `servicios` WRITE;
/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
INSERT INTO `servicios` VALUES (1,'CONS001','Consulta general','Consulta básica de revisión',50.00,30,'Consulta',0,1,'2025-09-26 08:21:01','2025-09-26 08:21:01'),(2,'VAC001','Vacunación antirrábica','Aplicación de vacuna contra la rabia',25.00,15,'Procedimiento',0,1,'2025-09-26 08:21:01','2025-09-26 08:21:01'),(3,'CIR001','Cirugía menor','Procedimientos quirúrgicos básicos',200.00,120,'Cirugía',1,1,'2025-09-26 08:21:01','2025-09-26 08:21:01'),(6,'CONS-GEN','Consulta general','Consulta bÃ¡sica de revisiÃ³n',30000.00,30,'Consulta',0,1,'2025-09-28 02:35:10','2025-09-28 02:35:10');
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesiones`
--

DROP TABLE IF EXISTS `sesiones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sesiones` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `token_sesion` varchar(255) NOT NULL,
  `fecha_inicio` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_expiracion` timestamp NOT NULL,
  `direccion_ip` varchar(45) DEFAULT NULL,
  `user_agent` text,
  `activa` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_sesion` (`token_sesion`),
  KEY `idx_token` (`token_sesion`),
  KEY `idx_usuario` (`usuario_id`),
  CONSTRAINT `sesiones_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesiones`
--

LOCK TABLES `sesiones` WRITE;
/*!40000 ALTER TABLE `sesiones` DISABLE KEYS */;
/*!40000 ALTER TABLE `sesiones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--



--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veterinarios`
--

DROP TABLE IF EXISTS `veterinarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veterinarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(255) NOT NULL,
  `documento_identidad` varchar(20) NOT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `direccion` text,
  `fecha_contratacion` date DEFAULT NULL,
  `salario` decimal(10,2) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `documento_identidad` (`documento_identidad`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veterinarios`
--

LOCK TABLES `veterinarios` WRITE;
/*!40000 ALTER TABLE `veterinarios` DISABLE KEYS */;
INSERT INTO `veterinarios` VALUES (1,'Dr. Juan Pérez','100200300','Medicina interna','3124567890','juanperez@vet.com','Calle 50 #20-10','2020-05-01',3500.00,1,'2025-09-26 08:20:33','2025-09-26 08:20:33'),(2,'Dra. María Gómez','200300400','Cirugía','3109876543','mariagomez@vet.com','Av. Siempre Viva 742','2021-07-15',4200.00,1,'2025-09-26 08:20:33','2025-09-26 08:20:33');
/*!40000 ALTER TABLE `veterinarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'happy_feet_veterinaria'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-29 15:03:32
