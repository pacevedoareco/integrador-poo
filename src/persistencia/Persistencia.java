package persistencia;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import modelo.Evento;
import modelo.recursos.Recurso;
import modelo.Asistente;
import java.io.IOException;

public class Persistencia {
    private static final String ARCHIVO_EVENTOS = "datos_eventos.txt";
    private static int ultimoIdEvento = 0;
    private static int ultimoIdAsistente = 0;
    private static int ultimoIdRecurso = 0;

    // Guarda todos los eventos, asistentes y recursos en un solo archivo, incluyendo los últimos IDs
    public static void guardarTodo(List<Evento> eventos) {
        // Leer líneas existentes para conservar asistentes, recursos y ubicaciones globales
        List<String> lineas = leerTodasLasLineas();
        List<String> asistentesGlobales = new ArrayList<>();
        List<String> recursosGlobales = new ArrayList<>();
        List<String> ubicacionesGlobales = new ArrayList<>();
        int idx = 0;
        for (; idx < lineas.size() && idx < 3; idx++); // salteo los IDs, si existen.
        for (; idx < lineas.size(); idx++) {
            String l = lineas.get(idx);
            if (l.startsWith("ASISTENTE_GLOBAL|")) asistentesGlobales.add(l);
            else if (l.startsWith("RECURSO_GLOBAL|")) recursosGlobales.add(l);
            else if (l.startsWith("UBICACION_GLOBAL|")) ubicacionesGlobales.add(l);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            writer.write("ID_EVENTO|" + ultimoIdEvento); writer.newLine();
            writer.write("ID_ASISTENTE|" + ultimoIdAsistente); writer.newLine();
            writer.write("ID_RECURSO|" + ultimoIdRecurso); writer.newLine();
            for (String l : asistentesGlobales) { writer.write(l); writer.newLine(); }
            for (String l : recursosGlobales) { writer.write(l); writer.newLine(); }
            for (String l : ubicacionesGlobales) { writer.write(l); writer.newLine(); }
            for (Evento evento : eventos) {
                int idUbicEvento = evento.getUbicacion() != null ? evento.getUbicacion().getId() : -1;
                String lineaEvento = "EVENTO|" + evento.getId() + "|" + evento.getNombre() + "|" + evento.getDescripcion() + "|" + evento.getFecha() + "|" + idUbicEvento;
                writer.write(lineaEvento); writer.newLine();
                for (Asistente asistente : evento.getAsistentes()) {
                    String lineaAsistente = "ASISTENTE|" + evento.getId() + "|" + asistente.getId() + "|" + asistente.getNombre() + "|" + asistente.getEmail();
                    writer.write(lineaAsistente); writer.newLine();
                }
                for (Recurso recurso : evento.getRecursos()) {
                    String[] partesSerializadas = recurso.serializar().split("\\|", 4);
                    String tipo = partesSerializadas[0];
                    int id = recurso.getId();
                    String nombre = recurso.getNombre();
                    String atributoExtra = partesSerializadas.length > 3 ? partesSerializadas[3] : "";
                    String lineaRecurso = "RECURSO|" + evento.getId() + "|" + id + "|" + tipo + "|" + nombre + "|" + atributoExtra;
                    writer.write(lineaRecurso); writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    // Carga todos los eventos, asistentes y recursos desde el archivo, y los últimos IDs
    public static List<Evento> cargarTodo() {
        Map<Integer, Evento> mapaEventos = new LinkedHashMap<>();
        File archivo = new File(ARCHIVO_EVENTOS);
        // Resetear IDs por defecto
        ultimoIdEvento = 0;
        ultimoIdAsistente = 0;
        ultimoIdRecurso = 0;
        // Cargar ubicaciones globales primero
        List<modelo.recursos.Ubicacion> ubicaciones = cargarUbicacionesGlobales();
        if (!archivo.exists()) return new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 0) continue;
                switch (partes[0]) {
                    case "ID_EVENTO":
                        if (partes.length >= 2) {
                            ultimoIdEvento = Integer.parseInt(partes[1]);
                        }
                        break;
                    case "ID_ASISTENTE":
                        if (partes.length >= 2) {
                            ultimoIdAsistente = Integer.parseInt(partes[1]);
                        }
                        break;
                    case "ID_RECURSO":
                        if (partes.length >= 2) {
                            ultimoIdRecurso = Integer.parseInt(partes[1]);
                        }
                        break;
                    case "EVENTO":
                        if (partes.length >= 6) {
                            int id = Integer.parseInt(partes[1]);
                            String nombre = partes[2];
                            String descripcion = partes[3];
                            LocalDate fecha = LocalDate.parse(partes[4]);
                            int idUbic = Integer.parseInt(partes[5]);
                            modelo.recursos.Ubicacion ubic = ubicaciones.stream().filter(u -> u.getId() == idUbic).findFirst().orElse(null);
                            mapaEventos.put(id, new Evento(id, nombre, descripcion, fecha, ubic));
                        }
                        break;
                    case "ASISTENTE":
                        if (partes.length >= 5) {
                            int eventoId = Integer.parseInt(partes[1]);
                            int id = Integer.parseInt(partes[2]);
                            String nombre = partes[3];
                            String email = partes[4];
                            Evento evento = mapaEventos.get(eventoId);
                            if (evento != null) {
                                evento.agregarAsistente(new Asistente(id, nombre, email));
                            }
                        }
                        break;
                    case "RECURSO":
                        if (partes.length >= 6) {
                            int eventoId = Integer.parseInt(partes[1]);
                            int id = Integer.parseInt(partes[2]);
                            String tipo = partes[3];
                            String nombre = partes[4];
                            String atributoExtra = partes[5];
                            Evento evento = mapaEventos.get(eventoId);
                            if (evento != null) {
                                Recurso recurso = Recurso.deserializar(tipo, id, nombre, atributoExtra, ubicaciones);
                                if (recurso != null) {
                                    evento.agregarRecurso(recurso);
                                }
                            }
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar los datos: " + e.getMessage());
        }
        return new ArrayList<>(mapaEventos.values());
    }

    // Métodos para obtener y actualizar los últimos IDs
    public static int getSiguienteIdEvento() {
        return ++ultimoIdEvento;
    }

    public static int getSiguienteIdAsistente() {
        return ++ultimoIdAsistente;
    }

    public static int getSiguienteIdRecurso() {
        return ++ultimoIdRecurso;
    }

    // --- Métodos utilitarios para bloques globales e IDs ---
    private static void guardarBloqueGlobal(String prefijo, List<String> lineasBloque) {
        List<String> lineas = leerTodasLasLineas();
        List<String> nuevasLineas = new ArrayList<>();
        int idx = 0;
        // Copiar los IDs (primeras 3 líneas)
        for (; idx < lineas.size() && idx < 3; idx++) {
            nuevasLineas.add(lineas.get(idx));
        }
        // Saltar líneas del bloque viejo
        while (idx < lineas.size() && lineas.get(idx).startsWith(prefijo)) {
            idx++;
        }
        // Agregar el bloque nuevo
        nuevasLineas.addAll(lineasBloque);
        // Agregar el resto del archivo
        for (; idx < lineas.size(); idx++) {
            nuevasLineas.add(lineas.get(idx));
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            for (String l : nuevasLineas) writer.write(l + "\n");
        } catch (IOException e) {
            System.out.println("Error al guardar bloque global: " + e.getMessage());
        }
    }

    private static List<String[]> cargarBloqueGlobal(String prefijo, int camposEsperados) {
        List<String[]> resultado = new ArrayList<>();
        File archivo = new File(ARCHIVO_EVENTOS);
        if (!archivo.exists()) return resultado;
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith(prefijo)) {
                    String[] partes = linea.split("\\|", camposEsperados);
                    if (partes.length >= camposEsperados) {
                        resultado.add(partes);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar bloque global: " + e.getMessage());
        }
        return resultado;
    }

    private static void actualizarIdEnArchivo(String idKey, int nuevoValor) {
        List<String> lineas = leerTodasLasLineas();
        List<String> nuevasLineas = new ArrayList<>();
        boolean actualizado = false;
        for (String l : lineas) {
            if (l.startsWith(idKey + "|")) {
                nuevasLineas.add(idKey + "|" + nuevoValor);
                actualizado = true;
            } else {
                nuevasLineas.add(l);
            }
        }
        if (!actualizado) {
            nuevasLineas.add(0, idKey + "|" + nuevoValor);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            for (String l : nuevasLineas) writer.write(l + "\n");
        } catch (IOException e) {
            System.out.println("Error al actualizar ID en archivo: " + e.getMessage());
        }
    }

    // --- Recursos globales ---
    public static void guardarRecursosGlobales(List<Recurso> recursos) {
        List<String> lineasBloque = new ArrayList<>();
        for (Recurso recurso : recursos) {
            String[] partesSerializadas = recurso.serializar().split("\\|", 4);
            String tipo = partesSerializadas[0];
            int id = recurso.getId();
            String nombre = recurso.getNombre();
            String atributoExtra = partesSerializadas.length > 3 ? partesSerializadas[3] : "";
            String linea = "RECURSO_GLOBAL|" + id + "|" + tipo + "|" + nombre + "|" + atributoExtra;
            lineasBloque.add(linea);
        }
        guardarBloqueGlobal("RECURSO_GLOBAL|", lineasBloque);
        actualizarIdEnArchivo("ID_RECURSO", ultimoIdRecurso);
    }

    public static List<Recurso> cargarRecursosGlobales() {
        List<Recurso> recursos = new ArrayList<>();
        List<modelo.recursos.Ubicacion> ubicaciones = cargarUbicacionesGlobales();
        for (String[] partes : cargarBloqueGlobal("RECURSO_GLOBAL|", 5)) {
            int id = Integer.parseInt(partes[1]);
            String tipo = partes[2];
            String nombre = partes[3];
            String atributoExtra = partes[4];
            Recurso recurso = Recurso.deserializar(tipo, id, nombre, atributoExtra, ubicaciones);
            if (recurso != null) recursos.add(recurso);
        }
        return recursos;
    }

    // --- Asistentes globales ---
    public static void guardarAsistentesGlobales(List<Asistente> asistentes) {
        List<String> lineasBloque = new ArrayList<>();
        for (Asistente asistente : asistentes) {
            String linea = "ASISTENTE_GLOBAL|" + asistente.getId() + "|" + asistente.getNombre() + "|" + asistente.getEmail();
            lineasBloque.add(linea);
        }
        guardarBloqueGlobal("ASISTENTE_GLOBAL|", lineasBloque);
        actualizarIdEnArchivo("ID_ASISTENTE", ultimoIdAsistente);
    }

    public static List<Asistente> cargarAsistentesGlobales() {
        List<Asistente> asistentes = new ArrayList<>();
        for (String[] partes : cargarBloqueGlobal("ASISTENTE_GLOBAL|", 4)) {
            int id = Integer.parseInt(partes[1]);
            String nombre = partes[2];
            String email = partes[3];
            asistentes.add(new Asistente(id, nombre, email));
        }
        return asistentes;
    }

    // --- Ubicaciones globales ---
    public static void guardarUbicacionesGlobales(List<modelo.recursos.Ubicacion> ubicaciones) {
        List<String> lineasBloque = new ArrayList<>();
        for (modelo.recursos.Ubicacion ubicacion : ubicaciones) {
            String linea = "UBICACION_GLOBAL|" + ubicacion.getId() + "|" + ubicacion.getNombre();
            lineasBloque.add(linea);
        }
        guardarBloqueGlobal("UBICACION_GLOBAL|", lineasBloque);
    }

    public static List<modelo.recursos.Ubicacion> cargarUbicacionesGlobales() {
        List<modelo.recursos.Ubicacion> ubicaciones = new ArrayList<>();
        for (String[] partes : cargarBloqueGlobal("UBICACION_GLOBAL|", 3)) {
            int id = Integer.parseInt(partes[1]);
            String nombre = partes[2];
            ubicaciones.add(new modelo.recursos.Ubicacion(id, nombre));
        }
        return ubicaciones;
    }

    // Método utilitario para leer todas las líneas del archivo unificado
    private static List<String> leerTodasLasLineas() {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(ARCHIVO_EVENTOS);
        if (!archivo.exists()) return lineas;
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer líneas: " + e.getMessage());
        }
        return lineas;
    }
} 