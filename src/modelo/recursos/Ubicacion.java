package modelo.recursos;

public class Ubicacion extends Recurso {
    public Ubicacion(int id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String getTipo() {
        return "Ubicación";
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public String serializar() {
        return getTipo() + "|" + getId() + "|" + getNombre();
    }

    @Override
    public String getDetalle() {
        return "";
    }
}