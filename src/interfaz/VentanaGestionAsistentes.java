package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import gestor.GestorAsistentes;
import modelo.Asistente;

// Ventana modal para la gestión global de asistentes
public class VentanaGestionAsistentes extends JDialog {
    private GestorAsistentes gestor;
    private JTable tablaAsistentes;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public VentanaGestionAsistentes(JFrame parent) {
        super(parent, "Gestión de Asistentes Registrados", true);
        this.gestor = GestorAsistentes.getInstancia();
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] columnas = {"ID", "Nombre", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaAsistentes = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaAsistentes);
        // Ocultar columna ID
        tablaAsistentes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaAsistentes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaAsistentes.getColumnModel().getColumn(0).setWidth(0);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAgregar = new JButton("Agregar asistente");
        btnEditar = new JButton("Editar asistente");
        btnEliminar = new JButton("Eliminar asistente");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarAsistente());
        btnEditar.addActionListener(e -> editarAsistente());
        btnEliminar.addActionListener(e -> eliminarAsistente());
        btnCerrar.addActionListener(e -> setVisible(false));

        actualizarTabla();
    }

    private void agregarAsistente() {
        Asistente asistente = dialogoAsistente(null);
        if (asistente != null) {
            gestor.agregarAsistente(asistente);
            actualizarTabla();
        }
    }

    private void editarAsistente() {
        int fila = tablaAsistentes.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Asistente original = gestor.listarAsistentes().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
        if (original == null) return;
        Asistente editado = dialogoAsistente(original);
        if (editado != null) {
            gestor.editarAsistente(editado);
            actualizarTabla();
        }
    }

    private void eliminarAsistente() {
        int fila = tablaAsistentes.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        gestor.eliminarAsistente(id);
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Asistente a : gestor.listarAsistentes()) {
            modeloTabla.addRow(new Object[]{a.getId(), a.getNombre(), a.getEmail()});
        }
    }

    // Diálogo para crear o editar un asistente
    private Asistente dialogoAsistente(Asistente original) {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del asistente:", original == null ? "" : original.getNombre());
        if (nombre == null || nombre.trim().isEmpty()) return null;
        String email = JOptionPane.showInputDialog(this, "Email del asistente:", original == null ? "" : original.getEmail());
        if (email == null || email.trim().isEmpty()) return null;
        int id = original == null ? gestor.listarAsistentes().stream().mapToInt(Asistente::getId).max().orElse(0) + 1 : original.getId();
        return new Asistente(id, nombre.trim(), email.trim());
    }
} 