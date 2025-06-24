package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import modelo.Asistente;
import modelo.recursos.Recurso;
import modelo.recursos.Ubicacion;
import gestor.GestorUbicaciones;
import util.ValidadorCampos;
import java.time.LocalDate;
import modelo.Evento;
import java.time.format.DateTimeFormatter;

public class FormularioEvento extends JDialog {
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtFecha;
    private JComboBox<Ubicacion> cmbUbicacion;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnEliminar;
    private boolean guardarPresionado = false;
    private boolean eliminarPresionado = false;
    private Evento eventoOriginal;

    private PanelAsistentes panelAsistentes;
    private PanelRecursos panelRecursos;
    private JTabbedPane pestanias;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FormularioEvento(JFrame parent) {
        this(parent, null);
    }

    public FormularioEvento(JFrame parent, Evento evento) {
        super(parent, evento == null ? "Agregar Evento" : "Editar Evento", true);
        this.eventoOriginal = evento;
        setSize(850, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // gridx y gridy me permiten navegar la grilla
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(evento != null ? evento.getNombre() : "", 32);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCampos.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextArea(evento != null ? evento.getDescripcion() : "", 5, 32);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(400, 100));
        panelCampos.add(scrollDesc, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Fecha (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1;
        txtFecha = new JTextField(evento != null ? evento.getFecha().format(FORMATO_FECHA) : "", 32);
        txtFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCampos.add(txtFecha, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 1;
        cmbUbicacion = new JComboBox<>(GestorUbicaciones.getInstancia().listarUbicaciones().toArray(new Ubicacion[0]));
        if (evento != null && evento.getUbicacion() != null) {
            cmbUbicacion.setSelectedItem(evento.getUbicacion());
        }
        panelCampos.add(cmbUbicacion, gbc);

        
        panelAsistentes = new PanelAsistentes(evento != null ? evento.getAsistentes() : new ArrayList<>());
        panelRecursos = new PanelRecursos(evento != null ? evento.getRecursos() : new ArrayList<>(), (Ubicacion) cmbUbicacion.getSelectedItem());
        pestanias = new JTabbedPane();
        pestanias.addTab("Detalles", panelCampos);
        pestanias.addTab("Asistentes", panelAsistentes);
        pestanias.addTab("Recursos", panelRecursos);
        add(pestanias, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        btnEliminar = new JButton("Eliminar");
        panelBotones.add(btnGuardar);
        if (evento != null) panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> {
            if (validarCampos()) {
                guardarPresionado = true;
                setVisible(false);
            }
        });
        btnCancelar.addActionListener(e -> {
            guardarPresionado = false;
            setVisible(false);
        });
        btnEliminar.addActionListener(e -> {
            eliminarPresionado = true;
            setVisible(false);
        });

        cmbUbicacion.addActionListener(e -> {
            // Al cambiar la ubicación, actualizar el panel de recursos
            List<Recurso> recursosActuales = panelRecursos.getRecursos();
            panelRecursos = new PanelRecursos(recursosActuales, (Ubicacion) cmbUbicacion.getSelectedItem());
            pestanias.setComponentAt(2, panelRecursos);
        });
    }

    private boolean validarCampos() {
        String fechaIngresada = txtFecha.getText().trim();
        if (!ValidadorCampos.esCampoObligatorio(txtNombre.getText()) ||
            !ValidadorCampos.esCampoObligatorio(txtDescripcion.getText()) ||
            !ValidadorCampos.esCampoObligatorio(fechaIngresada) ||
            cmbUbicacion.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        fechaIngresada = ValidadorCampos.normalizarFecha(fechaIngresada);
        txtFecha.setText(fechaIngresada);
        if (!ValidadorCampos.esFechaValida(fechaIngresada)) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener formato dd/mm/aaaa.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isGuardarPresionado() {
        return guardarPresionado;
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public String getDescripcion() {
        return txtDescripcion.getText().trim();
    }

    public String getFecha() {
        return txtFecha.getText().trim();
    }

    public Ubicacion getUbicacion() {
        return (Ubicacion) cmbUbicacion.getSelectedItem();
    }

    public List<Asistente> getAsistentes() {
        return panelAsistentes.getAsistentes();
    }

    public List<Recurso> getRecursos() {
        return panelRecursos.getRecursos();
    }

    public void setDatos(String nombre, String descripcion, String fecha, Ubicacion ubicacion, List<Asistente> asistentes, List<Recurso> recursos) {
        txtNombre.setText(nombre);
        txtDescripcion.setText(descripcion);
        txtFecha.setText(fecha);
        cmbUbicacion.setSelectedItem(ubicacion);
        panelAsistentes = new PanelAsistentes(asistentes);
        panelRecursos = new PanelRecursos(recursos, ubicacion);
        pestanias.setComponentAt(1, panelAsistentes);
        pestanias.setComponentAt(2, panelRecursos);
    }

    public boolean isEliminarPresionado() {
        return eliminarPresionado;
    }

    public Evento getEventoEditado() {
        return new Evento(
            eventoOriginal != null ? eventoOriginal.getId() : -1,
            getNombre(),
            getDescripcion(),
            LocalDate.parse(getFecha(), FORMATO_FECHA),
            getUbicacion()
        );
    }
} 