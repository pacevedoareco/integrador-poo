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

public class FormularioEvento extends JDialog {
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtFecha;
    private JComboBox<Ubicacion> cmbUbicacion;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private boolean aceptado = false;

    private PanelAsistentes panelAsistentes;
    private PanelRecursos panelRecursos;
    private JTabbedPane pestañas;

    public FormularioEvento(JFrame parent) {
        super(parent, "Agregar/Editar Evento", true);
        setSize(850, 500);
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
        txtNombre = new JTextField(32);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCampos.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextArea(5, 32);
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
        txtFecha = new JTextField(32);
        txtFecha.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCampos.add(txtFecha, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 1;
        cmbUbicacion = new JComboBox<>(GestorUbicaciones.getInstancia().listarUbicaciones().toArray(new Ubicacion[0]));
        panelCampos.add(cmbUbicacion, gbc);

        // Panel asistentes y recursos
        panelAsistentes = new PanelAsistentes(new ArrayList<>());
        panelRecursos = new PanelRecursos(new ArrayList<>(), (Ubicacion) cmbUbicacion.getSelectedItem());
        pestañas = new JTabbedPane();
        pestañas.addTab("Detalles", panelCampos);
        pestañas.addTab("Asistentes", panelAsistentes);
        pestañas.addTab("Recursos", panelRecursos);
        add(pestañas, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnAceptar = new JButton("Aceptar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAceptar.addActionListener(e -> {
            if (validarCampos()) {
                aceptado = true;
                setVisible(false);
            }
        });
        btnCancelar.addActionListener(e -> {
            aceptado = false;
            setVisible(false);
        });

        cmbUbicacion.addActionListener(e -> {
            // Al cambiar la ubicación, actualizar el panel de recursos
            List<Recurso> recursosActuales = panelRecursos.getRecursos();
            panelRecursos = new PanelRecursos(recursosActuales, (Ubicacion) cmbUbicacion.getSelectedItem());
            pestañas.setComponentAt(2, panelRecursos);
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
            JOptionPane.showMessageDialog(this, "La fecha debe tener formato dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isAceptado() {
        return aceptado;
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
        pestañas.setComponentAt(1, panelAsistentes);
        pestañas.setComponentAt(2, panelRecursos);
    }
} 