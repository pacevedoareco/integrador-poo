package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import modelo.recursos.Recurso;
import gestor.GestorRecursos;
import modelo.recursos.Salon;
import modelo.recursos.Catering;
import modelo.recursos.EquipoAudiovisual;
import modelo.recursos.Ubicacion;

// Panel para gestionar recursos de un evento
public class PanelRecursos extends JPanel {
    private JTable tablaGlobales;
    private DefaultTableModel modeloGlobales;
    private JTable tablaEvento;
    private DefaultTableModel modeloEvento;
    private JTextField txtBuscar;
    private JButton btnAgregar;
    private JButton btnQuitar;
    private List<Recurso> recursosEvento;
    private List<Recurso> recursosGlobales;
    private Ubicacion ubicacionEvento;

    public PanelRecursos(List<Recurso> recursosIniciales, Ubicacion ubicacionEvento) {
        setLayout(new BorderLayout(10, 10));
        this.recursosEvento = new ArrayList<>(recursosIniciales);
        this.recursosGlobales = GestorRecursos.getInstancia().listarRecursos();
        this.ubicacionEvento = ubicacionEvento;

        // Campo de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        txtBuscar = new JTextField();
        panelBusqueda.add(new JLabel("Buscar recurso global:"), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        add(panelBusqueda, BorderLayout.NORTH);

        // Tablas y botones
        JPanel panelTablas = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        // Tabla recursos globales
        String[] columnas = {"ID", "Tipo", "Nombre", "Detalle"};
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
        panelTablas.add(new JLabel("Recursos globales"), gbc);
        gbc.gridy = 1;
        panelTablas.add(scrollGlobales, gbc);

        // Botón agregar
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JPanel panelBtnsCentro = new JPanel(new GridLayout(2, 1, 0, 10));
        btnAgregar = new JButton("→");
        btnQuitar = new JButton("←");
        panelBtnsCentro.add(btnAgregar);
        panelBtnsCentro.add(btnQuitar);
        panelTablas.add(panelBtnsCentro, gbc);
        gbc.fill = GridBagConstraints.BOTH;

        // Tabla recursos del evento
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
        panelTablas.add(new JLabel("Recursos del evento"), gbc);
        gbc.gridy = 1;
        panelTablas.add(scrollEvento, gbc);

        add(panelTablas, BorderLayout.CENTER);

        // Listeners
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarGlobales(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarGlobales(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarGlobales(); }
        });
        btnAgregar.addActionListener(e -> agregarSeleccionados());
        btnQuitar.addActionListener(e -> quitarSeleccionados());
        // Doble clic en tabla de recursos globales
        tablaGlobales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaGlobales.rowAtPoint(e.getPoint());
                    if (fila != -1) {
                        agregarRecursoPorFila(fila);
                    }
                }
            }
        });

        actualizarTablas();
    }

    private void filtrarGlobales() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Recurso> filtrados = recursosGlobales.stream()
            .filter(r -> !recursosEvento.contains(r))
            .filter(r -> {
                if (r instanceof Salon) {
                    Salon s = (Salon) r;
                    return s.getUbicacion() != null && s.getUbicacion().equals(ubicacionEvento);
                }
                return true;
            })
            .filter(r -> r.getNombre().toLowerCase().contains(filtro)
                || r.getTipo().toLowerCase().contains(filtro)
                || getDetalle(r).toLowerCase().contains(filtro))
            .collect(Collectors.toList());
        actualizarTablaGlobales(filtrados);
    }

    private boolean puedeAgregarSalon(Salon salonNuevo) {
        Salon salonExistente = recursosEvento.stream().filter(s -> s instanceof Salon).map(s -> (Salon) s).findFirst().orElse(null);
        if (salonExistente != null) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "Este evento ya cuenta con un salón (" + salonExistente.getNombre() + "). ¿Estás seguro de que deseas agregar un salón adicional?",
                    "Agregar salón adicional", JOptionPane.YES_NO_OPTION);
            return resp == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void agregarSeleccionados() {
        int[] filas = tablaGlobales.getSelectedRows();
        for (int fila : filas) {
            int id = (int) modeloGlobales.getValueAt(fila, 0);
            Recurso r = recursosGlobales.stream().filter(rec -> rec.getId() == id).findFirst().orElse(null);
            if (r != null && !recursosEvento.contains(r)) {
                if (r instanceof Salon) {
                    if (!puedeAgregarSalon((Salon) r)) continue;
                }
                recursosEvento.add(r);
            }
        }
        actualizarTablas();
    }

    private void quitarSeleccionados() {
        int[] filas = tablaEvento.getSelectedRows();
        List<Recurso> aQuitar = new ArrayList<>();
        for (int fila : filas) {
            int id = (int) modeloEvento.getValueAt(fila, 0);
            Recurso r = recursosEvento.stream().filter(rec -> rec.getId() == id).findFirst().orElse(null);
            if (r != null) aQuitar.add(r);
        }
        recursosEvento.removeAll(aQuitar);
        actualizarTablas();
    }

    private void actualizarTablas() {
        filtrarGlobales();
        actualizarTablaEvento();
    }

    private void actualizarTablaGlobales(List<Recurso> lista) {
        modeloGlobales.setRowCount(0);
        for (Recurso r : lista) {
            modeloGlobales.addRow(new Object[]{r.getId(), r.getTipo(), r.getNombre(), r.getDetalle()});
        }
    }

    private void actualizarTablaEvento() {
        modeloEvento.setRowCount(0);
        for (Recurso r : recursosEvento) {
            modeloEvento.addRow(new Object[]{r.getId(), r.getTipo(), r.getNombre(), r.getDetalle()});
        }
    }

    private String getDetalle(Recurso r) {
        return r.getDetalle();
    }

    public List<Recurso> getRecursos() {
        return new ArrayList<>(recursosEvento);
    }

    // Agrega un recurso global al evento por índice de fila (con control de salón)
    private void agregarRecursoPorFila(int fila) {
        int id = (int) modeloGlobales.getValueAt(fila, 0);
        Recurso r = recursosGlobales.stream().filter(rec -> rec.getId() == id).findFirst().orElse(null);
        if (r != null && !recursosEvento.contains(r)) {
            if (r instanceof Salon) {
                if (!puedeAgregarSalon((Salon) r)) return;
            }
            recursosEvento.add(r);
            actualizarTablas();
        }
    }
} 