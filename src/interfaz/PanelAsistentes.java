package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import modelo.Asistente;
import gestor.GestorAsistentes;

public class PanelAsistentes extends JPanel {
    private JTable tablaGlobales;
    private DefaultTableModel modeloGlobales;
    private JTable tablaEvento;
    private DefaultTableModel modeloEvento;
    private JTextField txtBuscar;
    private JButton btnAgregar;
    private JButton btnQuitar;
    private JButton btnNuevoAsistente;
    private List<Asistente> asistentesEvento;
    private List<Asistente> asistentesGlobales;

    public PanelAsistentes(List<Asistente> asistentesIniciales) {
        setLayout(new BorderLayout(10, 10));
        this.asistentesEvento = new ArrayList<>(asistentesIniciales);
        this.asistentesGlobales = GestorAsistentes.getInstancia().listarAsistentes();

        // Búsqueda con blur search y botón para crear
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        txtBuscar = new JTextField();
        btnNuevoAsistente = new JButton("Nuevo asistente");
        JPanel panelBusquedaIzq = new JPanel(new BorderLayout(5, 5));
        panelBusquedaIzq.add(new JLabel("Buscar asistente registrado:"), BorderLayout.WEST);
        panelBusquedaIzq.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(panelBusquedaIzq, BorderLayout.CENTER);
        panelBusqueda.add(btnNuevoAsistente, BorderLayout.EAST);
        add(panelBusqueda, BorderLayout.NORTH);

        JPanel panelTablas = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        String[] columnas = {"ID", "Nombre", "Email"};
        modeloGlobales = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaGlobales = new JTable(modeloGlobales);
        JScrollPane scrollGlobales = new JScrollPane(tablaGlobales);
        // Ocultar columna ID
        tablaGlobales.getColumnModel().getColumn(0).setMinWidth(0);
        tablaGlobales.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaGlobales.getColumnModel().getColumn(0).setWidth(0);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        panelTablas.add(new JLabel("Asistentes previamente registrados"), gbc);
        gbc.gridy = 1;
        panelTablas.add(scrollGlobales, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JPanel panelBtnsCentro = new JPanel(new GridLayout(2, 1, 0, 10));
        btnAgregar = new JButton("→");
        btnQuitar = new JButton("←");
        panelBtnsCentro.add(btnAgregar);
        panelBtnsCentro.add(btnQuitar);
        panelTablas.add(panelBtnsCentro, gbc);
        gbc.fill = GridBagConstraints.BOTH;

        modeloEvento = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaEvento = new JTable(modeloEvento);
        JScrollPane scrollEvento = new JScrollPane(tablaEvento);
        // Ocultar columna ID
        tablaEvento.getColumnModel().getColumn(0).setMinWidth(0);
        tablaEvento.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaEvento.getColumnModel().getColumn(0).setWidth(0);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.5;
        panelTablas.add(new JLabel("Asistentes registrados al evento"), gbc);
        gbc.gridy = 1;
        panelTablas.add(scrollEvento, gbc);

        add(panelTablas, BorderLayout.CENTER);

        // Listeners del blur search
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarGlobales(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarGlobales(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarGlobales(); }
        });
        btnAgregar.addActionListener(e -> agregarSeleccionados());
        btnQuitar.addActionListener(e -> quitarSeleccionados());
        btnNuevoAsistente.addActionListener(e -> crearNuevoAsistente());

        // mover de izq a derecha al hacer doble click
        tablaGlobales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaGlobales.rowAtPoint(e.getPoint());
                    if (fila != -1) agregarAsistentePorFila(fila);
                }
            }
        });
        // eliminar de la derecha al hacer doble click
        tablaEvento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaEvento.rowAtPoint(e.getPoint());
                    if (fila != -1) quitarAsistentePorFila(fila);
                }
            }
        });

        actualizarTablas();
    }

    private void filtrarGlobales() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Asistente> filtrados = asistentesGlobales.stream()
            .filter(a -> !asistentesEvento.contains(a))
            .filter(a -> a.getNombre().toLowerCase().contains(filtro)
                || a.getEmail().toLowerCase().contains(filtro))
            .collect(Collectors.toList());
        actualizarTablaGlobales(filtrados);
    }

    private void agregarSeleccionados() {
        agregarAsistentesSeleccionados(tablaGlobales, modeloGlobales, asistentesGlobales);
    }

    private void quitarSeleccionados() {
        quitarAsistentesSeleccionados(tablaEvento, modeloEvento, asistentesEvento);
    }

    private void agregarAsistentesSeleccionados(JTable tabla, DefaultTableModel modelo, List<Asistente> fuente) {
        int[] filas = tabla.getSelectedRows();
        for (int fila : filas) {
            int id = (int) modelo.getValueAt(fila, 0);
            Asistente a = fuente.stream().filter(as -> as.getId() == id).findFirst().orElse(null);
            if (a != null && !asistentesEvento.contains(a)) asistentesEvento.add(a);
        }
        actualizarTablas();
    }

    private void quitarAsistentesSeleccionados(JTable tabla, DefaultTableModel modelo, List<Asistente> fuente) {
        int[] filas = tabla.getSelectedRows();
        List<Asistente> aQuitar = new ArrayList<>();
        for (int fila : filas) {
            int id = (int) modelo.getValueAt(fila, 0);
            Asistente a = fuente.stream().filter(as -> as.getId() == id).findFirst().orElse(null);
            if (a != null) aQuitar.add(a);
        }
        asistentesEvento.removeAll(aQuitar);
        actualizarTablas();
    }

    private void actualizarTablas() {
        filtrarGlobales();
        actualizarTablaEvento();
    }

    private void actualizarTablaGlobales(List<Asistente> lista) {
        modeloGlobales.setRowCount(0);
        for (Asistente a : lista) {
            modeloGlobales.addRow(new Object[]{a.getId(), a.getNombre(), a.getEmail()});
        }
    }

    private void actualizarTablaEvento() {
        modeloEvento.setRowCount(0);
        for (Asistente a : asistentesEvento) {
            modeloEvento.addRow(new Object[]{a.getId(), a.getNombre(), a.getEmail()});
        }
    }

    public List<Asistente> getAsistentes() {
        return new ArrayList<>(asistentesEvento);
    }

    private void agregarAsistentePorFila(int fila) {
        agregarAsistentesSeleccionados(tablaGlobales, modeloGlobales, asistentesGlobales);
    }

    private void quitarAsistentePorFila(int fila) {
        quitarAsistentesSeleccionados(tablaEvento, modeloEvento, asistentesEvento);
    }

    private void crearNuevoAsistente() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del asistente:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        String email = JOptionPane.showInputDialog(this, "Email del asistente:");
        if (email == null || email.trim().isEmpty()) return;
        boolean existe = asistentesGlobales.stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(email.trim()));
        if (existe) {
            JOptionPane.showMessageDialog(this, "El asistente ya se encuentra registrado con ese mail.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int nuevoId = asistentesGlobales.stream().mapToInt(a -> a.getId()).max().orElse(0) + 1;
        Asistente nuevo = new Asistente(nuevoId, nombre.trim(), email.trim());
        GestorAsistentes.getInstancia().agregarAsistente(nuevo);
        asistentesGlobales = GestorAsistentes.getInstancia().listarAsistentes();
        if (!asistentesEvento.contains(nuevo)) {
            asistentesEvento.add(nuevo);
        }
        actualizarTablas();
    }
} 