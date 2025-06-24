package gestor;

import java.util.List;
import java.util.ArrayList;
import modelo.recursos.Ubicacion;
import persistencia.Persistencia;

public class GestorUbicaciones {
    private static GestorUbicaciones instancia;
    private List<Ubicacion> ubicaciones;

    private GestorUbicaciones() {
        this.ubicaciones = Persistencia.cargarUbicacionesGlobales();
    }

    public static GestorUbicaciones getInstancia() {
        if (instancia == null) instancia = new GestorUbicaciones();
        return instancia;
    }

    public List<Ubicacion> listarUbicaciones() {
        return new ArrayList<>(ubicaciones);
    }

    public void agregarUbicacion(Ubicacion ubicacion) {
        ubicaciones.add(ubicacion);
        // Persistir luego
    }

    public void eliminarUbicacion(int id) {
        ubicaciones.removeIf(u -> u.getId() == id);
        // Persistir luego
    }

    public void editarUbicacion(Ubicacion ubicacionEditada) {
        for (int i = 0; i < ubicaciones.size(); i++) {
            if (ubicaciones.get(i).getId() == ubicacionEditada.getId()) {
                ubicaciones.set(i, ubicacionEditada);
                break;
            }
        }
        // Persistir luego
    }
} 