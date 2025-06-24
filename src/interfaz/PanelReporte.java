package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import modelo.Evento;
import modelo.recursos.Recurso;
import gestor.GestorEventos;
import modelo.Asistente;
import modelo.recursos.Ubicacion;

// Panel de reportes visuales (modal)
public class PanelReporte extends JDialog {
    private GestorEventos gestor;
    private JButton btnCerrar;

    public PanelReporte(JFrame parent, GestorEventos gestor) {
        super(parent, "Reportes de Eventos", true);
        this.gestor = gestor;
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Resumen general", crearPanelResumenGeneral());
        pestañas.addTab("Recursos más utilizados", crearPanelRecursos());
        pestañas.addTab("Asistentes", crearPanelAsistentesFrecuentes());
        pestañas.addTab("Próximos y recientes eventos", crearPanelProximosRecientes());
        add(pestañas, BorderLayout.CENTER);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> setVisible(false));
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    // 1. Resumen general
    private JPanel crearPanelResumenGeneral() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 10));
        panel.add(new GraficoEventosPorMes(gestor.listarEventos()));
        panel.add(new GraficoEventosPorUbicacion(gestor.listarEventos()));
        return panel;
    }

    // 2. Recursos más utilizados
    private JPanel crearPanelRecursos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new GraficoRecursos(gestor.listarEventos()), BorderLayout.CENTER);
        panel.add(new JScrollPane(resumenRecursos()), BorderLayout.EAST);
        return panel;
    }

    // 3. Asistentes (tabla + gráfico)
    private JComponent crearPanelAsistentesFrecuentes() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Gráfico arriba con scroll vertical si es necesario
        GraficoAsistentesPorEvento grafico = new GraficoAsistentesPorEvento(gestor.listarEventos());
        JScrollPane scrollGrafico = new JScrollPane(grafico);
        scrollGrafico.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGrafico.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollGrafico.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollGrafico);
        panel.add(Box.createVerticalStrut(20));
        JLabel lblTabla = new JLabel("Asistentes frecuentes");
        lblTabla.setFont(new Font("Arial", Font.BOLD, 16));
        lblTabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTabla);
        panel.add(Box.createVerticalStrut(5));
        JScrollPane tablaScroll = new JScrollPane(tablaAsistentesFrecuentes());
        tablaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Limitar altura de la tabla al 40% de la pantalla
        int alturaMaxTabla = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4);
        tablaScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, alturaMaxTabla));
        tablaScroll.setPreferredSize(new Dimension(tablaScroll.getPreferredSize().width, alturaMaxTabla));
        panel.add(tablaScroll);
        return panel;
    }

    // 4. Próximos y recientes eventos
    private JPanel crearPanelProximosRecientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JScrollPane(tablaProximosRecientes()), BorderLayout.CENTER);
        return panel;
    }

    // --- Utilidades para armado de datos de reportes ---
    private static class DatosReportes {
        static Map<String, Integer> eventosPorMes(List<Evento> eventos) {
            Map<String, Integer> conteo = new LinkedHashMap<>();
            for (Evento ev : eventos) {
                String mes = ev.getFecha().getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es", "ES")) + " " + ev.getFecha().getYear();
                conteo.put(mes, conteo.getOrDefault(mes, 0) + 1);
            }
            return conteo;
        }
        static Map<String, Integer> eventosPorUbicacion(List<Evento> eventos) {
            Map<String, Integer> conteo = new LinkedHashMap<>();
            for (Evento ev : eventos) {
                String ubic = ev.getUbicacion() != null ? ev.getUbicacion().getNombre() : "(Sin ubicación)";
                conteo.put(ubic, conteo.getOrDefault(ubic, 0) + 1);
            }
            return conteo;
        }
        static Map<String, Integer> ordenarPorValorDesc(Map<String, Integer> original) {
            return original.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        }
        static Map<String, Integer> recursosMasUtilizados(List<Evento> eventos) {
            Map<String, Integer> conteo = new LinkedHashMap<>();
            for (Evento ev : eventos) {
                for (Recurso r : ev.getRecursos()) {
                    conteo.put(r.getNombre(), conteo.getOrDefault(r.getNombre(), 0) + 1);
                }
            }
            return ordenarPorValorDesc(conteo);
        }
        static Map<String, Integer> recursosPorTipo(List<Evento> eventos) {
            Map<String, Integer> conteo = new LinkedHashMap<>();
            for (Evento ev : eventos) {
                for (Recurso r : ev.getRecursos()) {
                    conteo.put(r.getTipo(), conteo.getOrDefault(r.getTipo(), 0) + 1);
                }
            }
            return conteo;
        }
        static List<Object[]> asistentesFrecuentes(List<Evento> eventos) {
            Map<Asistente, Integer> conteo = new HashMap<>();
            for (Evento ev : eventos) {
                for (Asistente a : ev.getAsistentes()) {
                    conteo.put(a, conteo.getOrDefault(a, 0) + 1);
                }
            }
            return conteo.entrySet().stream()
                    .filter(e -> e.getValue() > 1)
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .map(e -> new Object[]{e.getKey().getNombre(), e.getKey().getEmail(), e.getValue()})
                    .toList();
        }
        static List<Object[]> proximosYRecientes(List<Evento> eventos) {
            java.time.LocalDate hoy = java.time.LocalDate.now();
            return eventos.stream()
                    .filter(ev -> (!ev.getFecha().isBefore(hoy.minusDays(30)) && !ev.getFecha().isAfter(hoy.plusDays(30))))
                    .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                    .map(ev -> new Object[]{ev.getFecha().isBefore(hoy) ? "Reciente" : "Próximo", ev.getNombre(), ev.getFecha(), ev.getUbicacion() != null ? ev.getUbicacion().getNombre() : "", ev.getAsistentes().size()})
                    .toList();
        }
        static Map<String, Integer> asistentesPorEvento(List<Evento> eventos) {
            Map<String, Integer> conteo = new LinkedHashMap<>();
            for (Evento ev : eventos) {
                conteo.put(ev.getNombre(), ev.getAsistentes().size());
            }
            return ordenarPorValorDesc(conteo);
        }
    }

    // --- Gráficos personalizados ---
    // 1.1 Gráfico de barras: eventos por mes
    private static class GraficoEventosPorMes extends JPanel {
        private final Map<String, Integer> datos;
        public GraficoEventosPorMes(List<Evento> eventos) {
            this.datos = DatosReportes.eventosPorMes(eventos);
            setPreferredSize(new Dimension(400, 400));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Eventos por mes", 20, 30);
            if (datos.isEmpty()) {
                g.drawString("No hay eventos.", 20, 60);
                return;
            }
            int x = 60, y = 350, anchoBarra = 40, gap = 30;
            int max = datos.values().stream().max(Integer::compareTo).orElse(1);
            int escala = max > 0 ? 200 / max : 1;
            int i = 0;
            for (String mes : datos.keySet()) {
                int valor = datos.get(mes);
                int altura = valor * escala;
                g.setColor(new Color(100, 149, 237));
                g.fillRect(x + i * (anchoBarra + gap), y - altura, anchoBarra, altura);
                g.setColor(Color.BLACK);
                g.drawRect(x + i * (anchoBarra + gap), y - altura, anchoBarra, altura);
                g.drawString(mes, x + i * (anchoBarra + gap), y + 20);
                g.drawString(String.valueOf(valor), x + i * (anchoBarra + gap) + 10, y - altura - 5);
                i++;
            }
        }
    }

    // Clase base reutilizable para gráficos de barras horizontales
    private static abstract class PanelGraficoBarrasHorizontales extends JPanel {
        protected String titulo;
        protected Map<String, Integer> datos;
        protected Color colorBarra;
        public PanelGraficoBarrasHorizontales(String titulo, Map<String, Integer> datos, Color colorBarra) {
            this.titulo = titulo;
            this.datos = datos;
            this.colorBarra = colorBarra;
            setPreferredSize(new Dimension(400, 400));
        }
        protected void drawWrappedString(Graphics g, String text, int x, int y, int maxWidth) {
            FontMetrics fm = g.getFontMetrics();
            if (fm.stringWidth(text) <= maxWidth) {
                g.drawString(text, x, y);
            } else {
                int breakPos = text.length();
                while (breakPos > 0 && fm.stringWidth(text.substring(0, breakPos)) > maxWidth) {
                    breakPos--;
                }
                int space = text.lastIndexOf(' ', breakPos);
                if (space > 0) breakPos = space;
                String linea1 = text.substring(0, breakPos);
                String linea2 = text.substring(breakPos).trim();
                g.drawString(linea1, x, y - 7);
                g.drawString(linea2, x, y + 7);
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(titulo, 20, 30);
            if (datos.isEmpty()) {
                g.drawString("No hay datos.", 20, 60);
                return;
            }
            int x = 200, y = 60, altoBarra = 30, gap = 20;
            int max = datos.values().stream().max(Integer::compareTo).orElse(1);
            int anchoMax = getWidth() - 250;
            int escala = max > 0 ? anchoMax / max : 1;
            int i = 0;
            for (String etiqueta : datos.keySet()) {
                int valor = datos.get(etiqueta);
                int ancho = valor * escala;
                g.setColor(colorBarra);
                g.fillRect(x, y + i * (altoBarra + gap), ancho, altoBarra);
                g.setColor(Color.BLACK);
                g.drawRect(x, y + i * (altoBarra + gap), ancho, altoBarra);
                drawWrappedString(g, etiqueta, 20, y + i * (altoBarra + gap) + altoBarra - 10, 170);
                g.drawString(String.valueOf(valor), x + ancho + 10, y + i * (altoBarra + gap) + altoBarra - 10);
                i++;
            }
        }
    }

    // 1.2 Gráfico de barras horizontal: eventos por ubicación
    private static class GraficoEventosPorUbicacion extends PanelGraficoBarrasHorizontales {
        public GraficoEventosPorUbicacion(List<Evento> eventos) {
            super("Eventos por ubicación", DatosReportes.eventosPorUbicacion(eventos), new Color(255, 193, 7));
        }
    }

    // 2.1 Gráfico de barras horizontal: recursos más utilizados
    private static class GraficoRecursos extends PanelGraficoBarrasHorizontales {
        public GraficoRecursos(List<Evento> eventos) {
            super("Recursos más utilizados", DatosReportes.recursosMasUtilizados(eventos), new Color(76, 175, 80));
        }
    }

    // 2.2 Tabla resumen de uso de recursos por tipo
    private JTable resumenRecursos() {
        Map<String, Integer> conteo = DatosReportes.recursosPorTipo(gestor.listarEventos());
        String[] columnas = {"Tipo de recurso", "Cantidad usada"};
        Object[][] datos = new Object[conteo.size()][2];
        int i = 0;
        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            datos[i][0] = entry.getKey();
            datos[i][1] = entry.getValue();
            i++;
        }
        JTable tabla = new JTable(datos, columnas);
        tabla.setEnabled(false);
        return tabla;
    }

    // 3. Tabla de asistentes frecuentes
    private JTable tablaAsistentesFrecuentes() {
        List<Object[]> filas = DatosReportes.asistentesFrecuentes(gestor.listarEventos());
        String[] columnas = {"Nombre", "Email", "Eventos"};
        Object[][] datos = filas.toArray(new Object[0][]);
        JTable tabla = new JTable(datos, columnas);
        tabla.setEnabled(false);
        return tabla;
    }

    // 4. Tabla de próximos y recientes eventos
    private JTable tablaProximosRecientes() {
        List<Object[]> filas = DatosReportes.proximosYRecientes(gestor.listarEventos());
        String[] columnas = {"Tipo", "Nombre", "Fecha", "Ubicación", "Asistentes"};
        Object[][] datos = filas.toArray(new Object[0][]);
        JTable tabla = new JTable(datos, columnas);
        tabla.setEnabled(false);
        return tabla;
    }

    // Gráfico de barras horizontal: asistentes por evento
    private static class GraficoAsistentesPorEvento extends PanelGraficoBarrasHorizontales {
        public GraficoAsistentesPorEvento(List<Evento> eventos) {
            super("Asistentes por evento", DatosReportes.asistentesPorEvento(eventos), new Color(33, 150, 243));
        }
    }
} 