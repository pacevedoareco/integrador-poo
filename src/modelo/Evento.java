package modelo;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import modelo.recursos.Recurso;
import modelo.recursos.Ubicacion;

// Clase que representa un evento
public class Evento {
    private int id;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private Ubicacion ubicacion;
    private List<Asistente> asistentes;
    private List<Recurso> recursos;

    public Evento(int id, String nombre, String descripcion, LocalDate fecha, Ubicacion ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.asistentes = new ArrayList<>();
        this.recursos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Asistente> getAsistentes() {
        return asistentes;
    }

    public void agregarAsistente(Asistente asistente) {
        asistentes.add(asistente);
    }

    public void quitarAsistente(Asistente asistente) {
        asistentes.remove(asistente);
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void agregarRecurso(Recurso recurso) {
        recursos.add(recurso);
    }

    public void quitarRecurso(Recurso recurso) {
        recursos.remove(recurso);
    }

    public boolean esFuturo() {
        return fecha.isAfter(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id == evento.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 