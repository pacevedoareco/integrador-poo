package gestor;

import java.util.List;
import modelo.Evento;
import persistencia.Persistencia;

public class GestorEventos implements GestorEntidad<Evento> {
    private static GestorEventos instancia;
    private List<Evento> eventos;

    private GestorEventos() {
        this.eventos = Persistencia.cargarTodo();
    }

    public static GestorEventos getInstancia() {
        if (instancia == null) instancia = new GestorEventos();
        return instancia;
    }

    public void crearEvento(Evento evento) {
        eventos.add(evento);
        guardar();
    }

    public void editarEvento(int id, Evento eventoEditado) {
        eventos.stream()
            .filter(e -> e.getId() == id)
            .findFirst()
            .ifPresent(e -> eventos.set(eventos.indexOf(e), eventoEditado));
        guardar();
    }

    public void eliminarEvento(int id) {
        eventos.removeIf(e -> e.getId() == id);
        guardar();
    }

    public List<Evento> listarEventos() {
        return eventos;
    }
    
    public void guardar() {
        Persistencia.guardarTodo(eventos);
    }

    public void recargar() {
        this.eventos = Persistencia.cargarTodo();
    }

    @Override
    public void agregar(Evento evento) { crearEvento(evento); }
    @Override
    public void editar(Evento eventoEditado) { editarEvento(eventoEditado.getId(), eventoEditado); }
    @Override
    public void eliminar(int id) { eliminarEvento(id); }
    @Override
    public List<Evento> listar() { return listarEventos(); }
} 