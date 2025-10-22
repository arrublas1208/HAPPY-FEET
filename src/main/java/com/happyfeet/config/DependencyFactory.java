package com.happyfeet.config;

import com.happyfeet.controller.*;
import com.happyfeet.repository.*;
import com.happyfeet.repository.impl.*;
import com.happyfeet.service.*;
import com.happyfeet.service.impl.*;
import com.happyfeet.view.*;
import com.happyfeet.util.DatabaseConnection;

public class DependencyFactory {

    private static DependencyFactory instance;

    // Repositories
    private CitaRepository citaRepository;
    private CitaEstadoRepository citaEstadoRepository;
    private DuenoRepository duenoRepository;
    private MascotaRepository mascotaRepository;
    private FacturaRepository facturaRepository;
    private InventarioRepository inventarioRepository;
    private ProveedorRepository proveedorRepository;
    private HistorialMedicoRepository historialMedicoRepository;
    private MascotaAdopcionRepository mascotaAdopcionRepository;
    private JornadaVacunacionRepository jornadaVacunacionRepository;
    private RegistroVacunacionRepository registroVacunacionRepository;
    private PuntosClienteRepository puntosClienteRepository;
    private MovimientoPuntosRepository movimientoPuntosRepository;
    private CompraClubFrecuenteRepository compraClubFrecuenteRepository;

    // Services
    private CitaService citaService;
    private CitaEstadoService citaEstadoService;
    private DuenoService duenoService;
    private MascotaService mascotaService;
    private FacturaService facturaService;
    private InventarioService inventarioService;
    private ReporteService reporteService;
    private VeterinarioService veterinarioService;
    private ProveedorService proveedorService;
    private HistorialMedicoService historialMedicoService;
    private MascotaAdopcionService mascotaAdopcionService;
    private JornadaVacunacionService jornadaVacunacionService;

    // Views
    private CitaView citaView;
    private DuenoView duenoView;
    private MascotaView mascotaView;

    // Controllers
    private CitaController citaController;
    private DuenoController duenoController;
    private MascotaController mascotaController;
    private FacturaController facturaController;
    private InventarioController inventarioController;
    private ReporteController reporteController;
    private ActividadesEspecialesControllerRefactored actividadesController;
    private ProveedorController proveedorController;
    private MainController mainController;

    private DependencyFactory() {
        initializeComponents();
    }

    public static DependencyFactory getInstance() {
        if (instance == null) {
            instance = new DependencyFactory();
        }
        return instance;
    }

    private void initializeComponents() {
        // Initialize database connection
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        // Mostrar configuración efectiva (sin exponer password) para facilitar diagnóstico
        java.util.Properties cfg = dbConnection.getCurrentConfig();
        String pwdSet = (cfg.getProperty("db.password") != null && !cfg.getProperty("db.password").isEmpty()) ? "SI" : "NO";
        System.out.println("[BD] Config efectiva -> URL: " + cfg.getProperty("db.url") + ", Usuario: " + cfg.getProperty("db.username") + ", Password definido: " + pwdSet);

        // Validar conectividad al iniciar para dar feedback inmediato
        try {
            dbConnection.getConnection();
            System.out.println("[BD] Conexión establecida correctamente: " + dbConnection.getConnectionInfo());
        } catch (RuntimeException ex) {
            System.err.println("[BD] Error al conectar con la base de datos: " + ex.getMessage());
            System.err.println("[BD] Verifica archivo database.properties, variables del sistema (db.url, db.username, db.password) o variables de entorno (DB_URL, DB_USERNAME, DB_PASSWORD).");
            throw ex; // Abortamos el arranque: nunca intentar operar sin credenciales/conexión válidas
        }

        // Initialize repositories
        citaRepository = new CitaRepositoryImpl();
        citaEstadoRepository = new CitaEstadoRepositoryImpl();
        duenoRepository = new DuenoRepositoryImpl();
        mascotaRepository = new MascotaRepositoryImpl();
        facturaRepository = new FacturaRepositoryImpl(dbConnection);
        inventarioRepository = new InventarioRepositoryImpl(dbConnection);
        proveedorRepository = new ProveedorRepositoryImpl();
        historialMedicoRepository = new HistorialMedicoRepositoryImpl();
        VeterinarioRepository veterinarioRepository = new VeterinarioRepositoryImpl();

        // Initialize new repositories for Actividades Especiales
        mascotaAdopcionRepository = new MascotaAdopcionRepositoryImpl();
        jornadaVacunacionRepository = new JornadaVacunacionRepositoryImpl();
        registroVacunacionRepository = new RegistroVacunacionRepositoryImpl();
        puntosClienteRepository = new PuntosClienteRepositoryImpl();
        movimientoPuntosRepository = new MovimientoPuntosRepositoryImpl();
        compraClubFrecuenteRepository = new CompraClubFrecuenteRepositoryImpl();

        // Initialize services
        duenoService = new DuenoServiceImpl(duenoRepository);
        mascotaService = new MascotaServiceImpl(mascotaRepository);
        citaEstadoService = new CitaEstadoServiceImpl(citaEstadoRepository);
        citaService = new CitaServiceImpl(citaRepository);
        inventarioService = new InventarioServiceImpl(inventarioRepository);
        reporteService = new ReporteServiceImpl(new LoggerServiceImpl(),
                                               historialMedicoRepository,
                                               facturaRepository,
                                               inventarioRepository);
        proveedorService = new ProveedorServiceImpl(proveedorRepository);
        veterinarioService = new VeterinarioServiceImpl(veterinarioRepository);
        historialMedicoService = new HistorialMedicoServiceImpl(historialMedicoRepository, inventarioService);
        facturaService = new FacturaServiceImpl(facturaRepository, historialMedicoService);

        // Initialize new services for Actividades Especiales
        LoggerManager loggerService = new LoggerServiceImpl();
        mascotaAdopcionService = new MascotaAdopcionServiceImpl(mascotaAdopcionRepository, loggerService);
        jornadaVacunacionService = new JornadaVacunacionServiceImpl(jornadaVacunacionRepository, registroVacunacionRepository, loggerService);

        // Initialize views
        citaView = new CitaView();
        duenoView = new DuenoView();
        mascotaView = new MascotaView();

        // Initialize controllers with dependencies
        citaController = new CitaController(citaService, mascotaService, veterinarioService, inventarioService, citaView);
        duenoController = new DuenoController(duenoService, mascotaService, duenoView);
        mascotaController = new MascotaController(mascotaService, duenoService, mascotaView);
        facturaController = new FacturaController(facturaService, duenoService, inventarioService, inventarioRepository);
        inventarioController = new InventarioController(inventarioService, inventarioRepository, proveedorService);
        reporteController = new ReporteController(facturaService, mascotaService, duenoService, citaService, inventarioRepository);

        // Initialize ActividadesEspecialesController with database repositories and services
        actividadesController = new ActividadesEspecialesControllerRefactored(
                mascotaAdopcionService,
                jornadaVacunacionService,
                duenoService,
                puntosClienteRepository,
                movimientoPuntosRepository,
                compraClubFrecuenteRepository
        );

        proveedorController = new ProveedorController(proveedorService);
        mainController = new MainController();
    }

    // Getters for controllers
    public CitaController getCitaController() {
        return citaController;
    }

    public DuenoController getDuenoController() {
        return duenoController;
    }

    public MascotaController getMascotaController() {
        return mascotaController;
    }

    public FacturaController getFacturaController() {
        return facturaController;
    }

    public InventarioController getInventarioController() {
        return inventarioController;
    }

    public ReporteController getReporteController() {
        return reporteController;
    }

    public ActividadesEspecialesControllerRefactored getActividadesController() {
        return actividadesController;
    }

    public ProveedorController getProveedorController() {
        return proveedorController;
    }

    public MainController getMainController() {
        return mainController;
    }

    // Getters for services (if needed elsewhere)
    public CitaService getCitaService() {
        return citaService;
    }

    public CitaEstadoService getCitaEstadoService() {
        return citaEstadoService;
    }

    public DuenoService getDuenoService() {
        return duenoService;
    }

    public MascotaService getMascotaService() {
        return mascotaService;
    }

    public FacturaService getFacturaService() {
        return facturaService;
    }

    public InventarioService getInventarioService() {
        return inventarioService;
    }

    public ReporteService getReporteService() {
        return reporteService;
    }

    public ProveedorService getProveedorService() {
        return proveedorService;
    }

    public HistorialMedicoService getHistorialMedicoService() {
        return historialMedicoService;
    }
}