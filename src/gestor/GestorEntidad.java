package gestor;

import java.util.List;

public interface GestorEntidad<T> {
    void agregar(T entidad);
    void editar(T entidad);
    void eliminar(int id);
    List<T> listar();
} 