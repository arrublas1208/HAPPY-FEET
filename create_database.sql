-- Script para crear la base de datos Happy Feet Veterinaria
-- Ejecutar este script en MySQL antes de usar la aplicación

CREATE DATABASE IF NOT EXISTS happy_feet_veterinaria;
USE happy_feet_veterinaria;

-- Tabla de especies
CREATE TABLE IF NOT EXISTS especies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

-- Tabla de razas
CREATE TABLE IF NOT EXISTS razas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    especie_id INT NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (especie_id) REFERENCES especies(id)
);

-- Tabla de sexo
CREATE TABLE IF NOT EXISTS sexos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

-- Tabla de dueños
CREATE TABLE IF NOT EXISTS duenos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(200) NOT NULL,
    documento_identidad VARCHAR(50) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100) NOT NULL,
    direccion TEXT,
    contacto_emergencia VARCHAR(200),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de mascotas
CREATE TABLE IF NOT EXISTS mascotas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dueno_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    raza_id INT,
    sexo_id INT,
    microchip VARCHAR(50) UNIQUE,
    fecha_nacimiento DATE,
    peso DECIMAL(5,2),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dueno_id) REFERENCES duenos(id),
    FOREIGN KEY (raza_id) REFERENCES razas(id),
    FOREIGN KEY (sexo_id) REFERENCES sexos(id)
);

-- Tabla de veterinarios
CREATE TABLE IF NOT EXISTS veterinarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(200) NOT NULL,
    licencia_profesional VARCHAR(100) NOT NULL UNIQUE,
    especialidad VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de servicios
CREATE TABLE IF NOT EXISTS servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    duracion_minutos INT DEFAULT 60,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla de citas
CREATE TABLE IF NOT EXISTS citas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    veterinario_id INT NOT NULL,
    mascota_id INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME,
    estado ENUM('PROGRAMADA', 'CONFIRMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA') DEFAULT 'PROGRAMADA',
    motivo TEXT,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id),
    FOREIGN KEY (mascota_id) REFERENCES mascotas(id)
);

-- Tabla de inventario
CREATE TABLE IF NOT EXISTS inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_producto VARCHAR(50) NOT NULL UNIQUE,
    nombre_producto VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(100),
    precio_unitario DECIMAL(10,2) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    fecha_vencimiento DATE,
    proveedor VARCHAR(200),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de facturas
CREATE TABLE IF NOT EXISTS facturas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    dueno_id INT NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE,
    subtotal DECIMAL(10,2) NOT NULL,
    impuestos DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADA', 'VENCIDA', 'CANCELADA') DEFAULT 'PENDIENTE',
    forma_pago VARCHAR(50),
    fecha_pago DATE,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dueno_id) REFERENCES duenos(id)
);

-- Tabla de items de factura
CREATE TABLE IF NOT EXISTS items_factura (
    id INT PRIMARY KEY AUTO_INCREMENT,
    factura_id INT NOT NULL,
    producto_id INT NULL,
    servicio_id INT NULL,
    descripcion VARCHAR(500) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (factura_id) REFERENCES facturas(id),
    FOREIGN KEY (producto_id) REFERENCES inventario(id),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id)
);

-- Insertar datos básicos
INSERT INTO especies (nombre, descripcion) VALUES
('Canino', 'Perros domésticos'),
('Felino', 'Gatos domésticos'),
('Ave', 'Aves domésticas'),
('Reptil', 'Reptiles domésticos')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

INSERT INTO razas (nombre, especie_id, descripcion) VALUES
('Mestizo', 1, 'Raza mixta canina'),
('Golden Retriever', 1, 'Raza canina grande'),
('Labrador', 1, 'Raza canina mediana a grande'),
('Persa', 2, 'Raza felina de pelo largo'),
('Siamés', 2, 'Raza felina de origen asiático'),
('Mestizo', 2, 'Raza mixta felina')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

INSERT INTO sexos (nombre, descripcion) VALUES
('Macho', 'Sexo masculino'),
('Hembra', 'Sexo femenino'),
('Sin determinar', 'Sexo no determinado')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

INSERT INTO veterinarios (nombre_completo, licencia_profesional, especialidad, telefono, email) VALUES
('Dr. Juan Pérez', 'VET-12345', 'Medicina General', '555-0101', 'juan.perez@happyfeet.com'),
('Dra. María García', 'VET-67890', 'Cirugía', '555-0102', 'maria.garcia@happyfeet.com'),
('Dr. Carlos López', 'VET-11111', 'Dermatología', '555-0103', 'carlos.lopez@happyfeet.com')
ON DUPLICATE KEY UPDATE especialidad = VALUES(especialidad);

INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos) VALUES
('Consulta General', 'Consulta médica general', 50000, 30),
('Vacunación', 'Aplicación de vacunas', 30000, 15),
('Cirugía Menor', 'Procedimientos quirúrgicos menores', 150000, 60),
('Baño y Peluquería', 'Servicio de aseo y estética', 25000, 45),
('Radiografía', 'Examen radiológico', 80000, 20)
ON DUPLICATE KEY UPDATE precio = VALUES(precio);

-- Crear índices para optimizar consultas
CREATE INDEX idx_duenos_documento ON duenos(documento_identidad);
CREATE INDEX idx_mascotas_dueno ON mascotas(dueno_id);
CREATE INDEX idx_citas_fecha ON citas(fecha_inicio);
CREATE INDEX idx_citas_veterinario ON citas(veterinario_id);
CREATE INDEX idx_facturas_dueno ON facturas(dueno_id);
CREATE INDEX idx_facturas_fecha ON facturas(fecha_emision);

COMMIT;