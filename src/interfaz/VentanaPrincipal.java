package interfaz;

import javax.swing.*;
import java.awt.*;

// Ventana principal de la aplicación de gestión de eventos
public class VentanaPrincipal extends JFrame {
    private PanelCalendario panelCalendario;
    private JTable tablaEventos;
    private JButton btnAgregarEvento;
    private JButton btnEditarEvento;
    private JButton btnVerReportes;
    private JButton btnGestionarRecursos;
    private JButton btnGestionarAsistentes;
    private JLabel lblTituloEventos;

    public VentanaPrincipal() {
        setTitle("Gestión de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Panel superior: calendario
        panelCalendario = new PanelCalendario();
        // Centro: tabla de eventos
        tablaEventos = new JTable(); // El modelo se setea desde el controlador
        JScrollPane scrollTabla = new JScrollPane(tablaEventos);
        // Título arriba de la tabla
        lblTituloEventos = new JLabel();
        lblTituloEventos.setFont(new Font("Arial", Font.BOLD, 20));
        lblTituloEventos.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.add(lblTituloEventos, BorderLayout.CENTER);
        // Panel central con título, tabla y calendario
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BorderLayout(10, 10));
        // Panel intermedio para título y tabla (vertical)
        JPanel panelTituloTabla = new JPanel();
        panelTituloTabla.setLayout(new BoxLayout(panelTituloTabla, BoxLayout.Y_AXIS));
        panelTituloTabla.add(panelTitulo);
        // Espacio entre título y tabla
        panelTituloTabla.add(Box.createVerticalStrut(10));
        // Limitar altura de la tabla al 30% de la pantalla
        int alturaMaxTabla = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.3);
        scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, alturaMaxTabla));
        scrollTabla.setPreferredSize(new Dimension(scrollTabla.getPreferredSize().width, alturaMaxTabla));
        panelTituloTabla.add(scrollTabla);
        // Limitar altura del calendario al 50% de la pantalla
        int alturaMaxCalendario = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.5);
        panelCalendario.setMaximumSize(new Dimension(Integer.MAX_VALUE, alturaMaxCalendario));
        panelCalendario.setPreferredSize(new Dimension(panelCalendario.getPreferredSize().width, alturaMaxCalendario));
        panelCentral.add(panelTituloTabla, BorderLayout.NORTH);
        panelCentral.add(panelCalendario, BorderLayout.CENTER);
        JScrollPane scrollCentral = new JScrollPane(panelCentral);
        scrollCentral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCentral.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCentral.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollCentral, BorderLayout.CENTER);

        // Panel inferior: botones de acción
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnAgregarEvento = new JButton("Agregar evento");
        btnEditarEvento = new JButton("Editar evento");
        btnVerReportes = new JButton("Ver reportes");
        btnGestionarRecursos = new JButton("Gestionar recursos");
        btnGestionarAsistentes = new JButton("Gestionar asistentes");
        panelBotones.add(btnAgregarEvento);
        panelBotones.add(btnEditarEvento);
        panelBotones.add(btnVerReportes);
        panelBotones.add(btnGestionarRecursos);
        panelBotones.add(btnGestionarAsistentes);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public JTable getTablaEventos() {
        return tablaEventos;
    }

    public JButton getBtnAgregarEvento() {
        return btnAgregarEvento;
    }

    public JButton getBtnEditarEvento() {
        return btnEditarEvento;
    }

    public JButton getBtnVerReportes() {
        return btnVerReportes;
    }

    public JButton getBtnGestionarRecursos() {
        return btnGestionarRecursos;
    }

    public JButton getBtnGestionarAsistentes() {
        return btnGestionarAsistentes;
    }

    public PanelCalendario getPanelCalendario() {
        return panelCalendario;
    }

    public void setTituloEventos(String titulo) {
        lblTituloEventos.setText(titulo);
    }
} 