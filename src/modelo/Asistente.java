package modelo;

// Clase que representa a un asistente de un evento
public class Asistente {
    private int id;
    private String nombre;
    private String email;

    public Asistente(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asistente that = (Asistente) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 