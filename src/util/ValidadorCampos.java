package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidadorCampos {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static boolean esCampoObligatorio(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    public static boolean esFechaValida(String fecha) {
        try {
            LocalDate.parse(fecha, FORMATO_FECHA);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public static String normalizarFecha(String fechaIngresada) {
        if (fechaIngresada.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return fechaIngresada.replace('-', '/');
        } else if (fechaIngresada.matches("\\d{2}-\\d{2}-\\d{2}")) {
            String[] partes = fechaIngresada.replace('-', '/').split("/");
            int anio = Integer.parseInt(partes[2]);
            if (anio <= 69) anio += 2000;
            else anio += 1900;
            return partes[0] + "/" + partes[1] + "/" + anio;
        } else if (fechaIngresada.matches("\\d{2}/\\d{2}/\\d{2}")) {
            String[] partes = fechaIngresada.split("/");
            int anio = Integer.parseInt(partes[2]);
            if (anio <= 69) anio += 2000;
            else anio += 1900;
            return partes[0] + "/" + partes[1] + "/" + anio;
        }
        return fechaIngresada;
    }
} 