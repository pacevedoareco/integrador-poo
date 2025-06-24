package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import gestor.GestorRecursos;
import gestor.GestorUbicaciones;
import modelo.recursos.Recurso;
import modelo.recursos.Salon;
import modelo.recursos.Catering;
import modelo.recursos.EquipoAudiovisual;
import modelo.recursos.Ubicacion;
import persistencia.Persistencia;
import gestor.GestorEventos;
import modelo.Evento;

// Ventana modal para la gestión global de recursos
public class VentanaGestionRecursos extends JDialog {
    private GestorRecursos gestor;
    private GestorUbicaciones gestorUbicaciones;
    private GestorEventos gestorEventos;
    private JTable tablaRecursos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public VentanaGestionRecursos(JFrame parent) {
        super(parent, "Gestión de Recursos Globales", true);
        this.gestor = GestorRecursos.getInstancia();
        this.gestorUbicaciones = GestorUbicaciones.getInstancia();
        this.gestorEventos = GestorEventos.getInstancia();
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] columnas = {"ID", "Tipo", "Nombre", "Detalle"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRecursos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaRecursos);
        tablaRecursos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaRecursos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaRecursos.getColumnModel().getColumn(0).setWidth(0);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAgregar = new JButton("Agregar recurso");
        btnEditar = new JButton("Editar recurso");
        btnEliminar = new JButton("Eliminar recurso");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarRecurso());
        btnEditar.addActionListener(e -> editarRecurso());
        btnEliminar.addActionListener(e -> eliminarRecurso());
        btnCerrar.addActionListener(e -> setVisible(false));

        actualizarTabla();

        tablaRecursos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaRecursos.rowAtPoint(e.getPoint());
                    if (fila != -1) {
                        editarRecursoPorFila(fila);
                    }
                }
            }
        });
    }

    private void agregarRecurso() {
        FormularioRecursoGlobal form = new FormularioRecursoGlobal((JFrame) getParent(), null);
        form.setVisible(true);
        if (form.isAceptado()) {
            Recurso nuevo = form.getRecurso();
            if (nuevo instanceof Ubicacion) {
                gestorUbicaciones.agregarUbicacion((Ubicacion) nuevo);
                Persistencia.guardarUbicacionesGlobales(gestorUbicaciones.listarUbicaciones());
            } else {
                gestor.agregarRecurso(nuevo);
            }
            actualizarTabla();
            form.refrescarUbicacionesCombo();
        }
    }

    private void editarRecurso() {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String tipo = (String) modeloTabla.getValueAt(fila, 1);
        Recurso original = null;
        if ("Ubicación".equals(tipo)) {
            original = gestorUbicaciones.listarUbicaciones().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        } else {
            original = gestor.listarRecursos().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        }
        if (original == null) return;
        FormularioRecursoGlobal form = new FormularioRecursoGlobal((JFrame) getParent(), original);
        form.setVisible(true);
        if (form.isAceptado()) {
            Recurso editado = form.getRecurso();
            editado.setId(original.getId());
            if (editado instanceof Ubicacion) {
                gestorUbicaciones.editarUbicacion((Ubicacion) editado);
                Persistencia.guardarUbicacionesGlobales(gestorUbicaciones.listarUbicaciones());
            } else {
                gestor.editarRecurso(editado);
            }
            actualizarTabla();
        }
    }

    private void eliminarRecurso() {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String tipo = (String) modeloTabla.getValueAt(fila, 1);
        if ("Ubicación".equals(tipo)) {
            // Validar dependencias antes de eliminar
            boolean enUso = false;
            for (Recurso r : gestor.listarRecursos()) {
                if (r instanceof Salon) {
                    Salon s = (Salon) r;
                    if (s.getUbicacion() != null && s.getUbicacion().getId() == id) {
                        enUso = true;
                        break;
                    }
                }
            }
            if (!enUso) {
                for (Evento ev : gestorEventos.listarEventos()) {
                    if (ev.getUbicacion() != null && ev.getUbicacion().getId() == id) {
                        enUso = true;
                        break;
                    }
                    for (Recurso r : ev.getRecursos()) {
                        if (r instanceof Salon) {
                            Salon s = (Salon) r;
                            if (s.getUbicacion() != null && s.getUbicacion().getId() == id) {
                                enUso = true;
                                break;
                            }
                        }
                    }
                    if (enUso) break;
                }
            }
            if (enUso) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar la ubicación porque está en uso por un salón o evento.", "Ubicación en uso", JOptionPane.ERROR_MESSAGE);
                return;
            }
            gestorUbicaciones.eliminarUbicacion(id);
            Persistencia.guardarUbicacionesGlobales(gestorUbicaciones.listarUbicaciones());
        } else {
            gestor.eliminarRecurso(id);
        }
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        for (Recurso r : gestor.listarRecursos()) {
            String tipo = r.getTipo();
            if(tipo.equals("Salón")){
                Salon s = (Salon) r;
                tipo = tipo.concat(" ("+ s.getUbicacion().getNombre() +")");
            }
            filas.add(new Object[]{r.getId(), tipo, r.getNombre(), r.getDetalle()});
        }
        for (Ubicacion u : gestorUbicaciones.listarUbicaciones()) {
            filas.add(new Object[]{u.getId(), u.getTipo(), u.getNombre(), u.getDetalle()});
        }
        filas.sort((a, b) -> a[1].toString().compareToIgnoreCase(b[1].toString()));
        filas.forEach(f -> modeloTabla.addRow(f));
    }

    // Editar recurso por fila (doble clic)
    private void editarRecursoPorFila(int fila) {
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String tipo = (String) modeloTabla.getValueAt(fila, 1);
        Recurso original = null;
        if ("Ubicación".equals(tipo)) {
            original = gestorUbicaciones.listarUbicaciones().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        } else {
            original = gestor.listarRecursos().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        }
        if (original == null) return;
        FormularioRecursoGlobal form = new FormularioRecursoGlobal((JFrame) getParent(), original);
        form.setVisible(true);
        if (form.isAceptado()) {
            Recurso editado = form.getRecurso();
            editado.setId(original.getId());
            if (editado instanceof Ubicacion) {
                gestorUbicaciones.editarUbicacion((Ubicacion) editado);
                Persistencia.guardarUbicacionesGlobales(gestorUbicaciones.listarUbicaciones());
            } else {
                gestor.editarRecurso(editado);
            }
            actualizarTabla();
        }
    }
} 