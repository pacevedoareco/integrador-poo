package modelo.recursos;

public class Catering extends Recurso {
    private String tipoComida;

    public Catering(int id, String nombre, String tipoComida) {
        super(id, nombre);
        this.tipoComida = tipoComida;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    @Override
    public String getTipo() {
        return "Catering";
    }

    @Override
    public String serializar() {
        return getTipo() + "|" + getId() + "|" + getNombre() + "|" + tipoComida;
    }

    @Override
    public String getDetalle() {
        return "Comida: " + tipoComida;
    }
} 