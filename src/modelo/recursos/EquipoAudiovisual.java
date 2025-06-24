package modelo.recursos;

// Clase que representa un equipo audiovisual como recurso
public class EquipoAudiovisual extends Recurso {
    private String tipoEquipo;

    public EquipoAudiovisual(int id, String nombre, String tipoEquipo) {
        super(id, nombre);
        this.tipoEquipo = tipoEquipo;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    @Override
    public String getTipo() {
        return "Equipo Audiovisual";
    }

    @Override
    public String serializar() {
        return getTipo() + "|" + getId() + "|" + getNombre() + "|" + tipoEquipo;
    }

    @Override
    public String getDetalle() {
        return "Equipo: " + tipoEquipo;
    }
}