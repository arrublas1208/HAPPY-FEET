# 🏥 HappyFeet Veterinaria - Sistema de Gestión Integral

Sistema completo de gestión veterinaria desarrollado en Java con arquitectura MVC robusta, diseñado para optimizar la administración de clínicas veterinarias.

## 📋 Características Principales

### 🐾 **Módulos Implementados**

#### **1. Gestión de Pacientes**
- Registro completo de mascotas con ficha médica detallada
- Gestión de dueños con validaciones de datos únicos
- Sistema de transferencia de propiedad
- Historial médico integrado con consultas

#### **2. Servicios Médicos y Citas**
- Agenda médica con estados inteligentes (Programada → Confirmada → En Curso → Finalizada)
- Consultas médicas completas con signos vitales
- Descuento automático de inventario durante consultas
- Verificación de disponibilidad de veterinarios

#### **3. Inventario y Farmacia**
- Control de stock con alertas inteligentes
- Gestión de medicamentos, vacunas y material médico
- Sistema de proveedores con tipos especializados
- Restricción automática de productos vencidos

#### **4. Facturación y Reportes**
- Generación de facturas en texto plano con formato profesional
- Cálculo automático de IVA (19%) y descuentos
- Múltiples formas de pago (Efectivo, Tarjetas, Transferencia)
- Reportes ejecutivos y dashboard gerencial

#### **5. Actividades Especiales**
- **Sistema de Adopciones**: Registro de mascotas disponibles con contratos automáticos
- **Jornadas de Vacunación**: Gestión de eventos masivos con precios especiales
- **Club de Puntos**: Sistema de lealtad con 4 niveles (Bronce → Platino)

### 🏗️ **Arquitectura Técnica**

#### **Patrones de Diseño Implementados**
- **MVC (Model-View-Controller)**: Separación clara de responsabilidades
- **Builder Pattern**: Construcción fluida de entidades complejas
- **Factory Pattern**: Inyección de dependencias personalizada
- **Singleton Pattern**: Conexión a base de datos y logging
- **Observer Pattern**: Sistema de alertas de inventario
- **Repository Pattern**: Acceso a datos desacoplado

#### **Principios SOLID**
- **S**ingle Responsibility: Cada clase tiene una responsabilidad única
- **O**pen/Closed: Extensible mediante interfaces
- **L**iskov Substitution: Implementaciones sustituibles
- **I**nterface Segregation: Interfaces específicas por dominio
- **D**ependency Inversion: Dependencias por abstracción

## 🚀 Instalación y Configuración

### **Requisitos del Sistema**
- **Java 17** o superior
- **MySQL 8.0** o superior
- **Maven 3.6+** (opcional, incluye dependencias locales)

### **Configuración Rápida**

#### **1. Base de Datos**
```bash
# Ejecutar el script de creación
mysql -u root -p < create_database.sql
```

#### **2. Configuración de Conexión**
Editar `database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/happyfeet_veterinaria
db.username=tu_usuario
db.password=tu_password
db.driver=com.mysql.cj.jdbc.Driver
```

#### **3. Compilación y Ejecución**

**Windows:**
```cmd
compile.bat
run.bat
```

**Linux/Mac:**
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

**Manual:**
```bash
# Compilar
javac -cp "lib/*" -d target/classes -sourcepath src/main/java src/main/java/com/happyfeet/**/*.java

# Ejecutar
java -cp "lib/*:target/classes" com.happyfeet.view.MenuPrincipal
```

## 🛠️ Configuración en IDEs

### **IntelliJ IDEA**
1. Importar como proyecto existente
2. Marcar `src/main/java` como Sources Root
3. Marcar `src/test/java` como Test Sources Root
4. Agregar librerías desde `lib/` al classpath
5. Configurar SDK a Java 17

### **Eclipse**
1. Import → Existing Projects into Workspace
2. Configurar Java Build Path → Sources
3. Agregar JARs externos desde `lib/`

### **VS Code**
1. Abrir carpeta del proyecto
2. Instalar "Extension Pack for Java"
3. Configuración automática del classpath

## 📊 Base de Datos

### **Esquema Normalizado**
- **20+ tablas** con integridad referencial
- **Índices optimizados** para consultas frecuentes
- **Constraints** para validación de datos
- **Triggers** para auditoría automática

### **Tablas Principales**
- `duenos`, `mascotas`, `veterinarios`
- `citas`, `historial_medico`, `tratamientos`
- `inventario`, `proveedores`, `movimientos_inventario`
- `facturas`, `items_factura`, `puntos_cliente`

## 🧪 Testing

### **Pruebas Incluidas**
```bash
# Ejecutar tests
java -cp "lib/*:target/classes:target/test-classes" org.junit.platform.launcher.LauncherMain --select-package com.happyfeet
```

**Tests Implementados:**
- `CitaServiceImplTest`: Flujo completo de citas
- `DatabaseConnectionTest`: Conectividad y pool
- `FacturaStateIdempotentTest`: Estados de factura

## 📈 Funcionalidades Destacadas

### **🎯 Sistema de Estados Avanzado**
Las citas manejan transiciones controladas con validaciones de negocio:
```
PROGRAMADA → CONFIRMADA → EN_CURSO → FINALIZADA
     ↓            ↓           ↓
  CANCELADA   CANCELADA   CANCELADA
```

### **📦 Integración Automática**
- Al realizar consultas médicas, el inventario se descuenta automáticamente
- Las facturas se generan con cálculo automático de impuestos
- Los puntos se acumulan automáticamente en el club de clientes

### **🔔 Sistema de Alertas**
- Stock bajo automático por configuración de mínimos
- Productos próximos a vencer (configurable)
- Facturas vencidas con alertas

## 📁 Estructura del Proyecto

```
HappyFeet_Veterinaria/
├── src/main/java/com/happyfeet/
│   ├── config/                 # Factory e inyección de dependencias
│   ├── controller/             # 18 controladores especializados
│   ├── model/entities/         # 20+ entidades de dominio
│   ├── model/entities/enums/   # Enums ricos con lógica de negocio
│   ├── repository/             # Interfaces y implementaciones JDBC
│   ├── service/                # Lógica de negocio
│   ├── util/                   # Utilidades (DB, logging)
│   └── view/                   # Interfaces de usuario
├── src/test/java/              # Tests unitarios e integración
├── database/                   # Scripts SQL
├── lib/                        # Dependencias (MySQL Connector)
├── target/                     # Archivos compilados
└── scripts/                    # Utilidades de despliegue
```

## 🎮 Uso del Sistema

### **Menú Principal**
El sistema cuenta con una interfaz de consola intuitiva:

```
╔════════════════════════════════════════╗
║        HAPPYFEET VETERINARIA           ║
║     Sistema de Gestión Integral       ║
╠════════════════════════════════════════╣
║  1. 🐾 Gestión de Mascotas            ║
║  2. 👥 Gestión de Dueños              ║
║  3. 📅 Agenda de Citas                ║
║  4. 📦 Inventario y Farmacia          ║
║  5. 💰 Facturación                    ║
║  6. 📊 Reportes Ejecutivos            ║
║  7. 🎯 Actividades Especiales         ║
║  8. ⚙️  Configuración                 ║
║  0. 🚪 Salir                          ║
╚════════════════════════════════════════╝
```

### **Navegación Intuitiva**
- **Menús jerárquicos** con navegación numérica
- **Confirmaciones** para operaciones críticas
- **Validaciones en tiempo real** de datos
- **Mensajes descriptivos** de error y éxito

## 📝 Logging y Auditoría

### **Sistema de Logging**
- **FileLogger** personalizado con niveles (INFO, WARN, ERROR)
- **Archivos rotables** con timestamp
- **Auditoría completa** de operaciones críticas

### **Trazabilidad**
- Movimientos de inventario con usuario y timestamp
- Cambios de estado de citas registrados
- Historial de puntos del club de clientes

## 🔧 Mantenimiento

### **Scripts de Utilidad**
- `compile.bat/sh`: Compilación automática
- `run.bat/sh`: Ejecución con classpath configurado
- `create_database.sql`: Creación completa del esquema

### **Configuración Avanzada**
- Pool de conexiones configurable en `DatabaseConnection`
- Logging level ajustable en `FileLogger`
- Alertas de inventario personalizables

## 📈 Métricas del Proyecto

- **+21,000 líneas de código** Java
- **338 archivos** en el proyecto
- **58 clases** principales (controllers, services, repositories)
- **7 patrones de diseño** implementados
- **100% funcionalidad** según especificaciones

## 🏆 Características Profesionales

### **Código de Calidad**
- **Programación funcional** con Streams y Lambdas
- **Manejo robusto de errores** con excepciones personalizadas
- **Validaciones exhaustivas** en capas de servicio
- **Documentación técnica** completa

### **Arquitectura Escalable**
- **Desacoplamiento** mediante interfaces
- **Inyección de dependencias** personalizada
- **Separación de responsabilidades** estricta
- **Preparado para crecimiento** futuro

## 🤝 Contribuciones

### **Estándares de Código**
- Seguir principios SOLID
- Documentar métodos públicos
- Mantener cobertura de tests
- Validar entrada de usuarios

### **Proceso de Desarrollo**
- Feature branches para nuevas funcionalidades
- Code review obligatorio
- Tests unitarios para lógica crítica
- Documentación actualizada

---

