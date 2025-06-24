package gestor;

import java.util.List;
import modelo.Evento;
import persistencia.Persistencia;

// Clase que gestiona la creación, edición y eliminación de eventos en memoria y sincroniza con la persistencia
// Para crear un nuevo evento, asistente o recurso, usar GeneradorId.siguienteIdEvento(), siguienteIdAsistente() y siguienteIdRecurso() respectivamente.
// Ejemplo:
// Evento evento = new Evento(GeneradorId.siguienteIdEvento(), nombre, descripcion, fecha, ubicacion);
// Asistente asistente = new Asistente(GeneradorId.siguienteIdAsistente(), nombre, email);
// Salon salon = new Salon(GeneradorId.siguienteIdRecurso(), nombre, capacidad);
public class GestorEventos implements GestorEntidad<Evento> {
    private List<Evento> eventos;

    public GestorEventos() {
        this.eventos = Persistencia.cargarTodo();
    }

    public void crearEvento(Evento evento) {
        eventos.add(evento);
        guardar();
    }

    public void editarEvento(int id, Evento eventoEditado) {
        for (int i = 0; i < eventos.size(); i++) {
            if (eventos.get(i).getId() == id) {
                eventos.set(i, eventoEditado);
                break;
            }
        }
        guardar();
    }

    public void eliminarEvento(int id) {
        eventos.removeIf(e -> e.getId() == id);
        guardar();
    }

    public List<Evento> listarEventos() {
        return eventos;
    }

    // Sincroniza la lista de eventos con el archivo de persistencia
    public void guardar() {
        Persistencia.guardarTodo(eventos);
    }

    // Recarga la lista de eventos desde el archivo de persistencia
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