package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import gestor.GestorRecursos;
import modelo.recursos.Recurso;
import modelo.recursos.Salon;
import modelo.recursos.Catering;
import modelo.recursos.EquipoAudiovisual;
import modelo.recursos.Ubicacion;

// Ventana modal para la gestión global de recursos
public class VentanaGestionRecursos extends JDialog {
    private GestorRecursos gestor;
    private JTable tablaRecursos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public VentanaGestionRecursos(JFrame parent) {
        super(parent, "Gestión de Recursos Globales", true);
        this.gestor = GestorRecursos.getInstancia();
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
        // Ocultar columna ID
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
            gestor.agregarRecurso(form.getRecurso());
            actualizarTabla();
            form.refrescarUbicacionesCombo();
        }
    }

    private void editarRecurso() {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Recurso original = gestor.listarRecursos().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (original == null) return;
        FormularioRecursoGlobal form = new FormularioRecursoGlobal((JFrame) getParent(), original);
        form.setVisible(true);
        if (form.isAceptado()) {
            Recurso editado = form.getRecurso();
            // Mantener el mismo ID
            editado.setId(original.getId());
            gestor.editarRecurso(editado);
            actualizarTabla();
        }
    }

    private void eliminarRecurso() {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        gestor.eliminarRecurso(id);
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Recurso r : gestor.listarRecursos()) {
            modeloTabla.addRow(new Object[]{r.getId(), r.getTipo(), r.getNombre(), r.getDetalle()});
        }
    }

    // Editar recurso por fila (doble clic)
    private void editarRecursoPorFila(int fila) {
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Recurso original = gestor.listarRecursos().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (original == null) return;
        FormularioRecursoGlobal form = new FormularioRecursoGlobal((JFrame) getParent(), original);
        form.setVisible(true);
        if (form.isAceptado()) {
            Recurso editado = form.getRecurso();
            editado.setId(original.getId());
            gestor.editarRecurso(editado);
            actualizarTabla();
        }
    }
} 