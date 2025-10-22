package com.happyfeet;

import com.happyfeet.config.DependencyFactory;
import com.happyfeet.controller.*;
import com.happyfeet.view.ConsoleUtils;
import com.happyfeet.util.FileLogger;

/**
 * Aplicación principal del Sistema Happy Feet Veterinaria.
 * Integra todos los controladores y módulos usando SimpleDependencyFactory.
 */
public class HappyFeetApplication {

    private static final FileLogger LOG = FileLogger.getInstance();

    private final DependencyFactory factory;
    private final DuenoController duenoController;
    private final MascotaController mascotaController;
    private final CitaController citaController;
    private final FacturaController facturaController;
    private final InventarioController inventarioController;
    private final ReporteController reporteController;
    private final ActividadesEspecialesControllerRefactored actividadesController;
    private final MainController mainController;

    public HappyFeetApplication() {
        LOG.info("Inicializando aplicación Happy Feet Veterinaria");

        this.factory = DependencyFactory.getInstance();
        this.duenoController = factory.getDuenoController();
        this.mascotaController = factory.getMascotaController();
        this.citaController = factory.getCitaController();
        this.facturaController = factory.getFacturaController();
        this.inventarioController = factory.getInventarioController();
        this.reporteController = factory.getReporteController();
        this.actividadesController = factory.getActividadesController();
        this.mainController = factory.getMainController();

        LOG.info("Todos los controladores inicializados correctamente");
    }

    public void iniciar() {
        mostrarBienvenida();
        ejecutarMenuPrincipal();
        LOG.info("Aplicación Happy Feet finalizada");
    }

    private void mostrarBienvenida() {
        System.out.println();
        System.out.println("███████████████████████████████████████████████████████████████");
        System.out.println("█                                                             █");
        System.out.println("█                 HAPPY FEET VETERINARIA                     █");
        System.out.println("█                                                             █");
        System.out.println("█              Sistema de Gestión Integral                   █");
        System.out.println("█                    Versión Final                           █");
        System.out.println("█                                                             █");
        System.out.println("███████████████████████████████████████████████████████████████");
        System.out.println();
        System.out.println("Todos los módulos están operativos y listos para usar");
        System.out.println("Sistema completamente funcional con SimpleDependencyFactory");
        System.out.println();
        ConsoleUtils.pause();
    }

    private void ejecutarMenuPrincipal() {
        // Delegamos al menú principal completo ya implementado en MainController
        mainController.run();
    }

    private void menuDuenos() {
        duenoController.run();
    }

    private void menuMascotas() {
        mascotaController.run();
    }

    private void menuCitas() {
        citaController.run();
    }

    private void menuFacturas() {
        facturaController.run();
    }

    private void menuInventario() {
        inventarioController.run();
    }

    private void menuActividades() {
        actividadesController.run();
    }

    private void mostrarInformacionSistema() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    INFORMACIÓN DEL SISTEMA");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("Happy Feet Veterinaria - Sistema de Gestión Integral");
        System.out.println("   Versión: Final 2.0");
        System.out.println("   Arquitectura: Modular con Dependency Injection");
        System.out.println();
        System.out.println("Módulos Implementados:");
        System.out.println("   [OK] Gestión de Dueños - Completamente funcional");
        System.out.println("   [OK] Gestión de Mascotas - Completamente funcional");
        System.out.println("   [OK] Gestión de Citas - Completamente funcional");
        System.out.println("   [OK] Gestión de Facturas - Completamente funcional");
        System.out.println("   [OK] Gestión de Inventario - Completamente funcional");
        System.out.println("   [OK] Reportes Gerenciales - Completamente funcional");
        System.out.println("   [OK] Actividades Especiales - Completamente funcional");
        System.out.println();
        System.out.println("Características Técnicas:");
        System.out.println("   • SimpleDependencyFactory para inyección de dependencias");
        System.out.println("   • Arquitectura en capas (Controller-Service-Repository)");
        System.out.println("   • Manejo robusto de errores y logging integrado");
        System.out.println("   • Validación de datos y interfaz de consola amigable");
        System.out.println("   • Reportes detallados y dashboard ejecutivo");
        System.out.println();
        System.out.println("Estado: Sistema completamente operativo y listo para producción");
        System.out.println();
        ConsoleUtils.pause();
    }

    private void mostrarDespedida() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    ¡GRACIAS POR USAR");
        System.out.println("                 HAPPY FEET VETERINARIA!");
        System.out.println();
        System.out.println("              Cuidando a sus mascotas con amor");
        System.out.println();
        System.out.println("                      Sistema v2.0 Final");
        System.out.println("                   Hasta pronto y que tenga");
        System.out.println("                      un excelente día");
        System.out.println("=".repeat(70));
    }

    /**
     * Punto de entrada principal de la aplicación.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Sistema Happy Feet Veterinaria...");

            HappyFeetApplication app = new HappyFeetApplication();
            app.iniciar();

        } catch (Exception e) {
            System.err.println("Error crítico en la aplicación: " + e.getMessage());
            FileLogger.getInstance().error("Error crítico en main", e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}