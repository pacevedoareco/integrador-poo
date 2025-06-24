package modelo.recursos;

import java.util.List;

public abstract class Recurso {
    protected int id;
    protected String nombre;

    public Recurso(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    public abstract String getTipo();

    public abstract String serializar();
    public abstract String getDetalle();

    public static Recurso deserializar(String tipo, int id, String nombre, String atributoExtra, List<Ubicacion> ubicaciones) {
        switch (tipo) {
            case "Salón":
                String[] datos = atributoExtra.split(",");
                int idUbic = datos.length > 1 ? Integer.parseInt(datos[0]) : -1;
                int capacidad = datos.length > 1 ? Integer.parseInt(datos[1]) : Integer.parseInt(atributoExtra);
                Ubicacion ubic = ubicaciones.stream().filter(u -> u.getId() == idUbic).findFirst().orElse(null);
                return new Salon(id, nombre, capacidad, ubic);
            case "Catering":
                return new Catering(id, nombre, atributoExtra);
            case "Equipo Audiovisual":
                return new EquipoAudiovisual(id, nombre, atributoExtra);
            case "Ubicación":
                return new Ubicacion(id, nombre);
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recurso recurso = (Recurso) o;
        return id == recurso.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 