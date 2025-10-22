package com.happyfeet.tools;

import com.happyfeet.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Herramienta para verificar que la base de datos tiene todas las tablas necesarias
 */
public class VerificarBaseDatos {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("VERIFICACIÓN DE BASE DE DATOS - HAPPY FEET VETERINARIA");
        System.out.println("=".repeat(70));
        System.out.println();

        DatabaseConnection db = DatabaseConnection.getInstance();

        try (Connection conn = db.getConnection()) {
            System.out.println("✅ Conexión a base de datos exitosa");
            System.out.println("   " + db.getConnectionInfo());
            System.out.println();

            // Verificar tablas nuevas
            String[] tablasNuevas = {
                "mascotas_adopcion",
                "jornadas_vacunacion",
                "registros_vacunacion_jornada",
                "movimientos_puntos",
                "compras_club_frecuentes"
            };

            System.out.println("VERIFICANDO TABLAS NUEVAS:");
            System.out.println("-".repeat(70));

            try (Statement stmt = conn.createStatement()) {
                for (String tabla : tablasNuevas) {
                    String sql = "SELECT COUNT(*) as total FROM " + tabla;
                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        if (rs.next()) {
                            int total = rs.getInt("total");
                            System.out.println("✅ " + tabla + " - " + total + " registros");
                        }
                    } catch (Exception e) {
                        System.out.println("❌ " + tabla + " - NO EXISTE");
                        System.out.println("   Error: " + e.getMessage());
                    }
                }
            }

            System.out.println();
            System.out.println("=".repeat(70));
            System.out.println("Si ves ❌ arriba, ejecuta el script:");
            System.out.println("  database/newtables.sql");
            System.out.println("=".repeat(70));

        } catch (Exception e) {
            System.err.println("❌ Error al conectar a la base de datos:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
    }
}