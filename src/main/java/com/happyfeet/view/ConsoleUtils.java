package com.happyfeet.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleUtils {
    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ConsoleUtils(){}

    public static void pause() {
        System.out.println("\nPresione ENTER para continuar...");
        SC.nextLine();
    }

    public static String readNonEmpty(String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = SC.nextLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("Valor requerido. Intenta de nuevo.");
        }
    }

    public static String readOptional(String label) {
        System.out.print(label + " (opcional): ");
        String s = SC.nextLine();
        return s == null ? "" : s.trim();
    }

    public static int readInt(String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = SC.nextLine();
            try {
                return Integer.parseInt(s.trim());
            } catch (Exception e) {
                System.out.println("Debe ser un número entero.");
            }
        }
    }

    public static long readLong(String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = SC.nextLine();
            try {
                return Long.parseLong(s.trim());
            } catch (Exception e) {
                System.out.println("Debe ser un número entero largo.");
            }
        }
    }

    public static double readDouble(String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = SC.nextLine();
            try {
                return Double.parseDouble(s.trim());
            } catch (Exception e) {
                System.out.println("Debe ser un número (usa punto como decimal).");
            }
        }
    }

    public static LocalDate readDate(String label) {
        while (true) {
            System.out.print(label + " (yyyy-MM-dd): ");
            String s = SC.nextLine();
            try {
                return LocalDate.parse(s.trim(), DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Ejemplo: 2025-09-26");
            }
        }
    }

    public static LocalDateTime readDateTime(String label) {
        while (true) {
            System.out.print(label + " (yyyy-MM-dd HH:mm): ");
            String s = SC.nextLine();
            try {
                return LocalDateTime.parse(s.trim(), DATETIME_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Ejemplo: 2025-09-26 14:30");
            }
        }
    }

    public static boolean confirm(String label) {
        while (true) {
            System.out.print(label + " (s/n): ");
            String s = SC.nextLine();
            if (s == null) continue;
            s = s.trim().toLowerCase();
            if (s.equals("s") || s.equals("si") || s.equals("sí")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("Responde 's' o 'n'.");
        }
    }

    public static int menu(String titulo, String... opciones) {
        System.out.println("\n==== " + titulo + " ====");
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]);
        }
        System.out.println("0. Volver/Salir");
        while (true) {
            System.out.print("Selecciona una opción: ");
            String s = SC.nextLine();
            try {
                int op = Integer.parseInt(s.trim());
                if (op >= 0 && op <= opciones.length) return op;
            } catch (NumberFormatException ignored) {}
            System.out.println("Opción inválida.");
        }
    }
}
