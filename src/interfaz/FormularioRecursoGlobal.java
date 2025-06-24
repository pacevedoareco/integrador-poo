package interfaz;

import javax.swing.*;
import java.awt.*;
import modelo.recursos.Recurso;
import modelo.recursos.Salon;
import modelo.recursos.Catering;
import modelo.recursos.EquipoAudiovisual;
import modelo.recursos.Ubicacion;
import gestor.GestorUbicaciones;

// Formulario modal para agregar o editar un recurso global
public class FormularioRecursoGlobal extends JDialog {
    private JComboBox<String> cmbTipo;
    private JTextField txtNombre;
    private JTextField txtCapacidad;
    private JTextField txtTipoComida;
    private JTextField txtTipoEquipo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private boolean aceptado = false;
    private Recurso recursoResultante;
    private JLabel lblCapacidad;
    private JLabel lblTipoComida;
    private JLabel lblTipoEquipo;
    private JLabel lblUbicacion;
    private JComboBox<Ubicacion> cmbUbicacion;

    public FormularioRecursoGlobal(JFrame parent, Recurso recursoOriginal) {
        super(parent, recursoOriginal == null ? "Agregar recurso" : "Editar recurso", true);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(new JLabel("Tipo de recurso:"), gbc);
        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{"Salón", "Catering", "Equipo Audiovisual", "Ubicación"});
        panelCampos.add(cmbTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        panelCampos.add(txtNombre, gbc);

        lblCapacidad = new JLabel("Capacidad:");
        lblTipoComida = new JLabel("Tipo de comida:");
        lblTipoEquipo = new JLabel("Detalles:");
        lblUbicacion = new JLabel("Ubicación:");
        cmbUbicacion = new JComboBox<>(GestorUbicaciones.getInstancia().listarUbicaciones().toArray(new Ubicacion[0]));
        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(lblUbicacion, gbc);
        gbc.gridx = 1;
        panelCampos.add(cmbUbicacion, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(lblCapacidad, gbc);
        gbc.gridx = 1;
        txtCapacidad = new JTextField(20);
        panelCampos.add(txtCapacidad, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(lblTipoComida, gbc);
        gbc.gridx = 1;
        txtTipoComida = new JTextField(20);
        panelCampos.add(txtTipoComida, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelCampos.add(lblTipoEquipo, gbc);
        gbc.gridx = 1;
        txtTipoEquipo = new JTextField(20);
        panelCampos.add(txtTipoEquipo, gbc);

        add(panelCampos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Mostrar/ocultar campos según tipo
        cmbTipo.addActionListener(e -> actualizarCamposVisibles());
        actualizarCamposVisibles();

        // Si es edición, cargar datos
        if (recursoOriginal != null) {
            cmbTipo.setSelectedItem(recursoOriginal.getTipo());
            txtNombre.setText(recursoOriginal.getNombre());
            if (recursoOriginal instanceof Salon) {
                txtCapacidad.setText(String.valueOf(((Salon) recursoOriginal).getCapacidad()));
                cmbUbicacion.setSelectedItem(((Salon) recursoOriginal).getUbicacion());
            } else if (recursoOriginal instanceof Catering) {
                txtTipoComida.setText(((Catering) recursoOriginal).getTipoComida());
            } else if (recursoOriginal instanceof EquipoAudiovisual) {
                txtTipoEquipo.setText(((EquipoAudiovisual) recursoOriginal).getTipoEquipo());
            }
        }
        refrescarUbicacionesCombo();

        btnGuardar.addActionListener(e -> {
            if (validarCampos()) {
                aceptado = true;
                recursoResultante = construirRecurso(recursoOriginal == null ? -1 : recursoOriginal.getId());
                setVisible(false);
            }
        });
        btnCancelar.addActionListener(e -> {
            aceptado = false;
            setVisible(false);
        });
    }

    private void actualizarCamposVisibles() {
        String tipo = (String) cmbTipo.getSelectedItem();
        boolean esSalon = "Salón".equals(tipo);
        boolean esCatering = "Catering".equals(tipo);
        boolean esEquipo = "Equipo Audiovisual".equals(tipo);
        boolean esUbicacion = "Ubicación".equals(tipo);
        lblCapacidad.setVisible(esSalon);
        txtCapacidad.setVisible(esSalon);
        lblUbicacion.setVisible(esSalon);
        cmbUbicacion.setVisible(esSalon);
        lblTipoComida.setVisible(esCatering);
        txtTipoComida.setVisible(esCatering);
        lblTipoEquipo.setVisible(esEquipo);
        txtTipoEquipo.setVisible(esEquipo);
        txtNombre.setVisible(true);
    }

    private boolean validarCampos() {
        String tipo = (String) cmbTipo.getSelectedItem();
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("Salón".equals(tipo)) {
            if (txtCapacidad.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La capacidad es obligatoria para un salón.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                int cap = Integer.parseInt(txtCapacidad.getText().trim());
                if (cap <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La capacidad debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else if ("Catering".equals(tipo)) {
            if (txtTipoComida.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El tipo de comida es obligatorio para catering.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else if ("Equipo Audiovisual".equals(tipo)) {
            if (txtTipoEquipo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El tipo de equipo es obligatorio para equipo audiovisual.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private Recurso construirRecurso(int id) {
        String tipo = (String) cmbTipo.getSelectedItem();
        String nombre = txtNombre.getText().trim();
        if ("Salón".equals(tipo)) {
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            Ubicacion ubicacion = (Ubicacion) cmbUbicacion.getSelectedItem();
            return new Salon(id, nombre, capacidad, ubicacion);
        } else if ("Catering".equals(tipo)) {
            return new Catering(id, nombre, txtTipoComida.getText().trim());
        } else if ("Equipo Audiovisual".equals(tipo)) {
            return new EquipoAudiovisual(id, nombre, txtTipoEquipo.getText().trim());
        } else if ("Ubicación".equals(tipo)) {
            return new Ubicacion(id, nombre);
        }
        return null;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public Recurso getRecurso() {
        return recursoResultante;
    }

    public void refrescarUbicacionesCombo() {
        cmbUbicacion.removeAllItems();
        for (Ubicacion u : GestorUbicaciones.getInstancia().listarUbicaciones()) {
            cmbUbicacion.addItem(u);
        }
    }
} 