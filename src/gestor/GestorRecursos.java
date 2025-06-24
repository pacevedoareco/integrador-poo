package gestor;

import java.util.List;
import java.util.ArrayList;
import modelo.recursos.Recurso;
import persistencia.Persistencia;

public class GestorRecursos implements GestorEntidad<Recurso> {
    private static GestorRecursos instancia;
    private List<Recurso> recursos;

    private GestorRecursos() {
        this.recursos = Persistencia.cargarRecursosGlobales();
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) instancia = new GestorRecursos();
        return instancia;
    }

    public List<Recurso> listarRecursos() {
        return new ArrayList<>(recursos);
    }

    public void agregarRecurso(Recurso recurso) {
        recursos.add(recurso);
        Persistencia.guardarRecursosGlobales(recursos);
    }

    public void eliminarRecurso(int id) {
        recursos.removeIf(r -> r.getId() == id);
        Persistencia.guardarRecursosGlobales(recursos);
    }

    public void editarRecurso(Recurso recursoEditado) {
        recursos.stream()
        .filter(r -> r.getId() == recursoEditado.getId())
        .findFirst()
        .ifPresent(r -> recursos.set(recursos.indexOf(r), recursoEditado));    
        Persistencia.guardarRecursosGlobales(recursos);
    }

    @Override
    public void agregar(Recurso recurso) { agregarRecurso(recurso); }
    @Override
    public void editar(Recurso recursoEditado) { editarRecurso(recursoEditado); }
    @Override
    public void eliminar(int id) { eliminarRecurso(id); }
    @Override
    public List<Recurso> listar() { return listarRecursos(); }
} 