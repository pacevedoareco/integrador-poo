package persistencia;

// Clase utilitaria para la generación de IDs únicos
public class GeneradorId {
    private GeneradorId() {}

    public static int siguienteIdEvento() {
        return Persistencia.getSiguienteIdEvento();
    }

    public static int siguienteIdAsistente() {
        return Persistencia.getSiguienteIdAsistente();
    }

    public static int siguienteIdRecurso() {
        return Persistencia.getSiguienteIdRecurso();
    }
} 