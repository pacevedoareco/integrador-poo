package gestor;

import java.util.List;
import java.util.ArrayList;
import modelo.Asistente;
import persistencia.Persistencia;

// Gestor centralizado de asistentes globales
public class GestorAsistentes implements GestorEntidad<Asistente> {
    private static GestorAsistentes instancia;
    private List<Asistente> asistentes;

    private GestorAsistentes() {
        this.asistentes = Persistencia.cargarAsistentesGlobales();
    }

    public static GestorAsistentes getInstancia() {
        if (instancia == null) instancia = new GestorAsistentes();
        return instancia;
    }

    public List<Asistente> listarAsistentes() {
        return new ArrayList<>(asistentes);
    }

    public void agregarAsistente(Asistente asistente) {
        asistentes.add(asistente);
        Persistencia.guardarAsistentesGlobales(asistentes);
    }

    public void eliminarAsistente(int id) {
        asistentes.removeIf(a -> a.getId() == id);
        Persistencia.guardarAsistentesGlobales(asistentes);
    }

    public void editarAsistente(Asistente asistenteEditado) {
        for (int i = 0; i < asistentes.size(); i++) {
            if (asistentes.get(i).getId() == asistenteEditado.getId()) {
                asistentes.set(i, asistenteEditado);
                break;
            }
        }
        Persistencia.guardarAsistentesGlobales(asistentes);
    }

    @Override
    public void agregar(Asistente asistente) { agregarAsistente(asistente); }
    @Override
    public void editar(Asistente asistenteEditado) { editarAsistente(asistenteEditado); }
    @Override
    public void eliminar(int id) { eliminarAsistente(id); }
    @Override
    public List<Asistente> listar() { return listarAsistentes(); }
} 