package interfaz;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import modelo.Evento;

// Panel que muestra el calendario mensual y los eventos
public class PanelCalendario extends JPanel {
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JLabel lblMes;
    private JPanel panelDias;
    private LocalDate mesActual;
    private List<Evento> eventosDelMes;

    private static final String[] DIAS_SEMANA = {"L", "M", "M", "J", "V", "S", "D"};

    public PanelCalendario() {
        setLayout(new BorderLayout());
        mesActual = LocalDate.now().withDayOfMonth(1);

        // Panel de navegación
        JPanel panelNavegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        btnAnterior = new JButton("< Anterior");
        btnSiguiente = new JButton("Siguiente >");
        lblMes = new JLabel(getNombreMes(mesActual), SwingConstants.CENTER);
        lblMes.setFont(new Font("Arial", Font.BOLD, 16));
        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(lblMes);
        panelNavegacion.add(btnSiguiente);
        add(panelNavegacion, BorderLayout.NORTH);

        // Panel de días
        panelDias = new JPanel();
        panelDias.setLayout(new GridLayout(0, 7, 5, 5)); // 7 columnas para los días de la semana
        add(panelDias, BorderLayout.CENTER);

        // Inicializar los días del mes
        eventosDelMes = List.of();
        actualizarCalendario();
    }

    private String getNombreMes(LocalDate fecha) {
        return fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")) + " " + fecha.getYear();
    }

    public void setEventosDelMes(List<Evento> eventos) {
        this.eventosDelMes = eventos;
        actualizarCalendario();
    }

    public void actualizarCalendario() {
        panelDias.removeAll();
        // Encabezados de los días de la semana
        for (String dia : DIAS_SEMANA) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(new Color(30, 30, 30));
            panelDias.add(lbl);
        }
        // Mapa de día -> eventos
        Map<Integer, StringBuilder> eventosPorDia = new HashMap<>();
        for (Evento evento : eventosDelMes) {
            int dia = evento.getFecha().getDayOfMonth();
            eventosPorDia.putIfAbsent(dia, new StringBuilder());
            eventosPorDia.get(dia).append("- ").append(evento.getNombre()).append("<br>");
        }
        LocalDate primerDia = mesActual;
        int diasEnMes = primerDia.lengthOfMonth();
        int primerDiaSemana = primerDia.getDayOfWeek().getValue(); // 1=Lunes
        LocalDate hoy = LocalDate.now();
        boolean esMesActual = mesActual.getYear() == hoy.getYear() && mesActual.getMonthValue() == hoy.getMonthValue();
        // Espacios en blanco hasta el primer día (ajustado por encabezados)
        for (int i = 1; i < primerDiaSemana; i++) {
            panelDias.add(new JLabel(""));
        }
        for (int dia = 1; dia <= diasEnMes; dia++) {
            boolean esHoy = esMesActual && dia == hoy.getDayOfMonth();
            StringBuilder html = new StringBuilder("<html>");
            if (esHoy) {
                html.append("<b><span style='color:#0a2a5c;'>" + dia + "</span></b>");
            } else {
                html.append("<b>" + dia + "</b>");
            }
            if (eventosPorDia.containsKey(dia)) {
                // Mostrar cantidad si hay más de 2 eventos
                int cantidad = eventosPorDia.get(dia).toString().split("<br>").length;
                if (cantidad > 2) {
                    html.append("<br><span style='color:#444;'>" + cantidad + " eventos</span>");
                } else {
                    html.append("<br>").append(eventosPorDia.get(dia));
                }
            }
            html.append("</html>");
            JLabel lblDia = new JLabel(html.toString(), SwingConstants.CENTER);
            lblDia.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            if (esHoy) {
                lblDia.setOpaque(true);
                lblDia.setBackground(new Color(173, 216, 230)); // azul claro
                lblDia.setFont(lblDia.getFont().deriveFont(Font.BOLD));
            }
            panelDias.add(lblDia);
        }
        panelDias.revalidate();
        panelDias.repaint();
        lblMes.setText(getNombreMes(mesActual));
    }

    public void mostrarMesAnterior() {
        mesActual = mesActual.minusMonths(1);
        actualizarCalendario();
    }

    public void mostrarMesSiguiente() {
        mesActual = mesActual.plusMonths(1);
        actualizarCalendario();
    }

    public JButton getBtnAnterior() {
        return btnAnterior;
    }

    public JButton getBtnSiguiente() {
        return btnSiguiente;
    }

    public LocalDate getMesActual() {
        return mesActual;
    }
} 