package modelo.recursos;

public class Salon extends Recurso {
    private int capacidad;
    private Ubicacion ubicacion;

    public Salon(int id, String nombre, int capacidad, Ubicacion ubicacion) {
        super(id, nombre);
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String getTipo() {
        return "Salón";
    }

    @Override
    public String serializar() {
        int idUbic = ubicacion != null ? ubicacion.getId() : -1;
        return getTipo() + "|" + getId() + "|" + getNombre() + "|" + idUbic + "," + capacidad;
    }

    @Override
    public String getDetalle() {
        return "Capacidad: " + capacidad;
    }
}