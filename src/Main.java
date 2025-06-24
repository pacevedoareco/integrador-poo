import java.time.format.DateTimeFormatter;
import javax.swing.SwingUtilities;
import gestor.GestorEventos;
import interfaz.VentanaPrincipal;
import interfaz.FormularioEvento;
import modelo.Evento;
import persistencia.Persistencia;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import interfaz.PanelReporte;
import interfaz.VentanaGestionRecursos;
import interfaz.VentanaGestionAsistentes;
import javax.swing.table.DefaultTableModel;
import java.util.stream.Collectors;
import java.util.List;

public class Main {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorEventos gestor = GestorEventos.getInstancia();
            VentanaPrincipal ventana = new VentanaPrincipal();

            cargarEventosMesActual(ventana, gestor);

            ventana.getPanelCalendario().getBtnAnterior().addActionListener(e -> {
                ventana.getPanelCalendario().mostrarMesAnterior();
                cargarEventosMesActual(ventana, gestor);
            });
            ventana.getPanelCalendario().getBtnSiguiente().addActionListener(e -> {
                ventana.getPanelCalendario().mostrarMesSiguiente();
                cargarEventosMesActual(ventana, gestor);
            });

            ventana.getBtnAgregarEvento().addActionListener(e -> {
                FormularioEvento form = new FormularioEvento(ventana);
                form.setVisible(true);
                if (form.isGuardarPresionado()) {
                    try {
                        Evento nuevo = new Evento(
                            Persistencia.getSiguienteIdEvento(),
                            form.getNombre(),
                            form.getDescripcion(),
                            LocalDate.parse(form.getFecha(), FORMATO_FECHA),
                            form.getUbicacion()
                        );
                        nuevo.getAsistentes().addAll(form.getAsistentes());
                        nuevo.getRecursos().addAll(form.getRecursos());
                        gestor.crearEvento(nuevo);
                        cargarEventosMesActual(ventana, gestor);
                        JOptionPane.showMessageDialog(ventana, "Evento creado exitosamente.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ventana, "Error al crear el evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            ventana.getBtnEditarEvento().addActionListener(e -> {
                int fila = ventana.getTablaEventos().getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(ventana, "Seleccione un evento para editar.");
                    return;
                }
                abrirVistaDetalleEvento(ventana, gestor, fila);
            });

            ventana.getTablaEventos().addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int fila = ventana.getTablaEventos().rowAtPoint(e.getPoint());
                        if (fila != -1) {
                            abrirVistaDetalleEvento(ventana, gestor, fila);
                        }
                    }
                }
            });

            ventana.getBtnVerReportes().addActionListener(e -> {
                PanelReporte reporte = new PanelReporte(ventana, gestor);
                reporte.setVisible(true);
            });

            ventana.getBtnGestionarRecursos().addActionListener(e -> {
                VentanaGestionRecursos vgr = new VentanaGestionRecursos(ventana);
                vgr.setVisible(true);
            });

            ventana.getBtnGestionarAsistentes().addActionListener(e -> {
                VentanaGestionAsistentes vga = new VentanaGestionAsistentes(ventana);
                vga.setVisible(true);
            });

            ventana.setVisible(true);
        });
    }

    private static void cargarEventosMesActual(VentanaPrincipal ventana, GestorEventos gestor) {
        LocalDate mesActual = ventana.getPanelCalendario().getMesActual();
        String nombreMes = mesActual.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("es-AR"));

        String titulo = "Eventos de " + nombreMes.substring(0,1).toUpperCase() + nombreMes.substring(1) + " " + mesActual.getYear();
        ventana.setTituloEventos(titulo);
        List<Evento> eventosMes = gestor.listarEventos().stream()
                .filter(ev -> ev.getFecha().getYear() == mesActual.getYear() && ev.getFecha().getMonthValue() == mesActual.getMonthValue())
                .sorted((e1, e2) -> e1.getFecha().compareTo(e2.getFecha()))
                .collect(Collectors.toList());
        ventana.getPanelCalendario().setEventosDelMes(eventosMes);
        String[] columnas = {"ID", "Nombre", "Descripción", "Fecha", "Ubicación"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        int filaHoy = -1;
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i < eventosMes.size(); i++) {
            Evento ev = eventosMes.get(i);
            modelo.addRow(new Object[]{ev.getId(), ev.getNombre(), ev.getDescripcion(), ev.getFecha().format(FORMATO_FECHA), ev.getUbicacion()});
            if (filaHoy == -1 && ev.getFecha().isEqual(hoy)) {
                filaHoy = i;
            }
        }
        ventana.getTablaEventos().setModel(modelo);
        if (ventana.getTablaEventos().getColumnCount() > 0) {
            ventana.getTablaEventos().getColumnModel().getColumn(0).setMinWidth(0);
            ventana.getTablaEventos().getColumnModel().getColumn(0).setMaxWidth(0);
            ventana.getTablaEventos().getColumnModel().getColumn(0).setWidth(0);
        }
        if (filaHoy != -1) {
            ventana.getTablaEventos().setRowSelectionInterval(filaHoy, filaHoy);
            ventana.getTablaEventos().scrollRectToVisible(ventana.getTablaEventos().getCellRect(filaHoy, 0, true));
        }
    }

    private static void abrirVistaDetalleEvento(VentanaPrincipal ventana, GestorEventos gestor, int fila) {
        int idEvento = (int) ventana.getTablaEventos().getValueAt(fila, 0);
        Evento evento = gestor.listarEventos().stream().filter(ev -> ev.getId() == idEvento).findFirst().orElse(null);
        if (evento == null) return;
        FormularioEvento form = new FormularioEvento(ventana, evento);
        form.setVisible(true);
        if (form.isGuardarPresionado()) {
            try {
                Evento editado = form.getEventoEditado();
                editado.getAsistentes().addAll(form.getAsistentes());
                editado.getRecursos().addAll(form.getRecursos());
                gestor.editarEvento(evento.getId(), editado);
                cargarEventosMesActual(ventana, gestor);
                JOptionPane.showMessageDialog(ventana, "Evento editado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, "Error al editar el evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (form.isEliminarPresionado()) {
            gestor.eliminarEvento(evento.getId());
            cargarEventosMesActual(ventana, gestor);
            JOptionPane.showMessageDialog(ventana, "Evento eliminado exitosamente.");
        }
    }
} 