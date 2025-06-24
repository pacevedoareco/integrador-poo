package interfaz;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import modelo.Evento;
import modelo.Asistente;
import modelo.recursos.Recurso;
import modelo.recursos.Ubicacion;
import gestor.GestorUbicaciones;
import interfaz.PanelAsistentes;
import interfaz.PanelRecursos;
import util.ValidadorCampos;

// Vista modal para mostrar y editar detalles de un evento y permitir eliminarlo
public class VistaDetalleEvento extends JDialog {
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtFecha;
    private JComboBox<Ubicacion> cmbUbicacion;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnCancelar;
    private boolean guardarPresionado = false;
    private boolean eliminarPresionado = false;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private PanelAsistentes panelAsistentes;
    private PanelRecursos panelRecursos;
    private JTabbedPane pestañas;

    public VistaDetalleEvento(JFrame parent, Evento evento) {
        super(parent, "Detalles del Evento", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Pestaña Detalles
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(evento.getNombre(), 32);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCampos.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextArea(evento.getDescripcion(), 5, 32);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(400, 100));
        panelCampos.add(scrollDesc, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtFecha = new JTextField(evento.getFecha().format(FORMATO_FECHA), 32);
        txtFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCampos.add(txtFecha, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 1;
        cmbUbicacion = new JComboBox<>(GestorUbicaciones.getInstancia().listarUbicaciones().toArray(new Ubicacion[0]));
        cmbUbicacion.setSelectedItem(evento.getUbicacion());
        panelCampos.add(cmbUbicacion, gbc);

        // Panel asistentes y recursos
        panelAsistentes = new PanelAsistentes(evento.getAsistentes());
        panelRecursos = new PanelRecursos(evento.getRecursos(), evento.getUbicacion());
        pestañas = new JTabbedPane();
        pestañas.addTab("Detalles", panelCampos);
        pestañas.addTab("Asistentes", panelAsistentes);
        pestañas.addTab("Recursos", panelRecursos);
        add(pestañas, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar cambios");
        btnEliminar = new JButton("Eliminar evento");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> {
            if (validarCampos()) {
                guardarPresionado = true;
                setVisible(false);
            }
        });
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este evento?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarPresionado = true;
                setVisible(false);
            }
        });
        btnCancelar.addActionListener(e -> {
            guardarPresionado = false;
            eliminarPresionado = false;
            setVisible(false);
        });
    }

    private boolean validarCampos() {
        String fechaIngresada = txtFecha.getText().trim();
        if (!ValidadorCampos.esCampoObligatorio(txtNombre.getText()) ||
            !ValidadorCampos.esCampoObligatorio(txtDescripcion.getText()) ||
            !ValidadorCampos.esCampoObligatorio(fechaIngresada)) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        fechaIngresada = ValidadorCampos.normalizarFecha(fechaIngresada);
        txtFecha.setText(fechaIngresada);
        if (!ValidadorCampos.esFechaValida(fechaIngresada)) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener formato dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isGuardarPresionado() {
        return guardarPresionado;
    }

    public boolean isEliminarPresionado() {
        return eliminarPresionado;
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

    public LocalDate getFechaLocalDate() {
        return LocalDate.parse(getFecha(), FORMATO_FECHA);
    }

    public List<Asistente> getAsistentes() {
        return panelAsistentes.getAsistentes();
    }

    public List<Recurso> getRecursos() {
        return panelRecursos.getRecursos();
    }

    // (Opcional) Método para refrescar datos si se desea reutilizar la vista
    public void setDatos(Evento evento) {
        txtNombre.setText(evento.getNombre());
        txtDescripcion.setText(evento.getDescripcion());
        txtFecha.setText(evento.getFecha().format(FORMATO_FECHA));
        cmbUbicacion.setSelectedItem(evento.getUbicacion());
        panelAsistentes = new PanelAsistentes(evento.getAsistentes());
        panelRecursos = new PanelRecursos(evento.getRecursos(), evento.getUbicacion());
        pestañas.setComponentAt(1, panelAsistentes);
        pestañas.setComponentAt(2, panelRecursos);
    }
} 