-- ======================================================
-- INSERCIONES DE DATOS POR TABLA
-- ======================================================

-- 1. ESPECIES
INSERT INTO especies (nombre, descripcion) VALUES
('Canino', 'Perros de todas las razas'),
('Felino', 'Gatos domésticos'),
('Ave', 'Aves de compañía como loros y canarios');

-- 2. RAZAS
INSERT INTO razas (especie_id, nombre, caracteristicas) VALUES
(1, 'Labrador Retriever', 'Amigable, activo y sociable'),
(1, 'Bulldog', 'Calmado y cariñoso'),
(2, 'Persa', 'Pelo largo, temperamento tranquilo'),
(2, 'Siames', 'Activo, vocal y curioso'),
(3, 'Canario', 'Ave pequeña y colorida');

-- 3. PRODUCTO_TIPOS
INSERT INTO producto_tipos (nombre, descripcion) VALUES
('Medicamento', 'Productos farmacéuticos para mascotas'),
('Alimento', 'Comida balanceada para mascotas'),
('Accesorio', 'Juguetes, collares y otros accesorios');

-- 4. EVENTO_TIPOS
INSERT INTO evento_tipos (nombre, descripcion, requiere_diagnostico) VALUES
('Consulta general', 'Revisión médica general', TRUE),
('Vacunación', 'Aplicación de vacunas', FALSE),
('Desparasitación', 'Control de parásitos internos y externos', FALSE);

-- 5. CITA_ESTADOS
INSERT INTO cita_estados (nombre, descripcion, es_activo) VALUES
('Pendiente', 'Cita programada pendiente de atención', TRUE),
('Atendida', 'Cita completada', TRUE),
('Cancelada', 'Cita anulada por el cliente o veterinario', FALSE);

-- 6. PROVEEDORES
INSERT INTO proveedores (nombre, ruc, contacto, telefono, email, direccion) VALUES
('Distribuidora VetCare', '1234567890', 'Carlos Ramírez', '3001234567', 'contacto@vetcare.com', 'Calle 123 #45-67'),
('Mascotas & Cia', '9876543210', 'Ana López', '3019876543', 'ventas@mascotascia.com', 'Carrera 45 #89-10');

-- 7. VETERINARIOS
INSERT INTO veterinarios (nombre_completo, documento_identidad, especialidad, telefono, email, direccion, fecha_contratacion, salario) VALUES
('Dr. Juan Pérez', '100200300', 'Medicina interna', '3124567890', 'juanperez@vet.com', 'Calle 50 #20-10', '2020-05-01', 3500.00),
('Dra. María Gómez', '200300400', 'Cirugía', '3109876543', 'mariagomez@vet.com', 'Av. Siempre Viva 742', '2021-07-15', 4200.00);

-- 8. SERVICIOS
INSERT INTO servicios (codigo, nombre, descripcion, precio, duracion_estimada, categoria, requiere_equipo_especial) VALUES
('CONS001', 'Consulta general', 'Consulta básica de revisión', 50.00, 30, 'Consulta', FALSE),
('VAC001', 'Vacunación antirrábica', 'Aplicación de vacuna contra la rabia', 25.00, 15, 'Procedimiento', FALSE),
('CIR001', 'Cirugía menor', 'Procedimientos quirúrgicos básicos', 200.00, 120, 'Cirugía', TRUE);