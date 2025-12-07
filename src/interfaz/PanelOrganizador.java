package interfaz;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import logica.Evento;
import logica.Organizador;
import logica.Venue;
import java.util.List;
import logica.Localidad;

public class PanelOrganizador extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Evento> listaEventosPropios;
    private DefaultListModel<Evento> modeloLista;

    public PanelOrganizador(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        
        // Encabezado
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Panel de Organizador - Mis Eventos"));
        add(topPanel, BorderLayout.NORTH);

        // Lista de los eventos
        modeloLista = new DefaultListModel<>();
        cargarEventos();
        listaEventosPropios = new JList<>(modeloLista);
        
        // Visualización personalizada
        listaEventosPropios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Evento) {
                    Evento ev = (Evento) value;
                    // Muestra Nombre, Fecha, Hora y Estado
                    setText(ev.getNombre() + " (" + ev.getFecha() + " " + ev.getHora() + ") - " + ev.getEstado());
                }
                return this;
            }
        });
        add(new JScrollPane(listaEventosPropios), BorderLayout.CENTER);

        // --- BOTONES ---
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 5, 5));
        
        JButton btnCrearEvento = new JButton("Crear Evento");
        JButton btnProponerVenue = new JButton("Proponer Nuevo Venue");
        JButton btnCrearLocalidad = new JButton("Añadir Localidad");
        JButton btnEstadisticas = new JButton("Ver Mis Ganancias");
        JButton btnLogout = new JButton("Cerrar Sesión");
        JButton btnCancelarEvento = new JButton("Solicitar Cancelación");


        panelBotones.add(btnCrearEvento);
        panelBotones.add(btnProponerVenue);
        panelBotones.add(btnCrearLocalidad);
        panelBotones.add(btnEstadisticas);
        panelBotones.add(btnCancelarEvento);
        panelBotones.add(btnLogout);
        
        add(panelBotones, BorderLayout.SOUTH);

        // --- ACCIONES ---
        btnLogout.addActionListener(e -> ventana.cerrarSesion());
        btnCrearEvento.addActionListener(e -> crearEvento());
        btnProponerVenue.addActionListener(e -> proponerVenue());
        btnCrearLocalidad.addActionListener(e -> crearLocalidad());
        btnEstadisticas.addActionListener(e -> verGanancias());
        btnCancelarEvento.addActionListener(e -> solicitarCancelacionEvento());
    }

    private void cargarEventos() {
        modeloLista.clear();
        Organizador org = (Organizador) ventana.getSistema().getUsuarioActual();
        if (org.getEventosCreados() != null) {
            for (Evento e : org.getEventosCreados()) {
                modeloLista.addElement(e);
            }
        }
    }

    // --- MODIFICACIÓN AQUÍ: Adición de campos Fecha y Hora ---
    private void crearEvento() {
        if (Venue.venues.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay Venues disponibles.\nProponga uno nuevo y espere a que el Admin lo apruebe.");
            return;
        }
        
        // Selección del Venue
        JComboBox<Venue> comboVenues = new JComboBox<>();
        for (Venue v : Venue.venues.values()) {
            comboVenues.addItem(v);
        }
        comboVenues.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Venue) setText(((Venue) value).getNombre());
                return this;
            }
        });

        // Campos de entrada
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtTipo = new JTextField("MUSICAL");
        // Prellenar con la fecha de mañana para ayudar al usuario
        JTextField txtFecha = new JTextField(LocalDate.now().plusDays(1).toString()); 
        JTextField txtHora = new JTextField("20:00");
        
        Object[] message = {
            "Seleccione Venue:", comboVenues,
            "Nombre del Evento:", txtNombre,
            "Descripción:", txtDescripcion,
            "Tipo (MUSICAL, DEPORTIVO, etc):", txtTipo,
            "Fecha (YYYY-MM-DD):", txtFecha,
            "Hora (HH:MM):", txtHora
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Crear Nuevo Evento", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Venue v = (Venue) comboVenues.getSelectedItem();
            String nombre = txtNombre.getText();
            String desc = txtDescripcion.getText();
            String tipo = txtTipo.getText();
            String fechaStr = txtFecha.getText();
            String horaStr = txtHora.getText();
            
            try {
                // Conversión de los textos a objetos Date/Hora
                LocalDate fecha = LocalDate.parse(fechaStr);
                LocalTime hora = LocalTime.parse(horaStr);

                ((Organizador) ventana.getSistema().getUsuarioActual()).crearEvento(nombre, desc, v, tipo, fecha, hora);
                JOptionPane.showMessageDialog(this, "¡Evento Creado Exitosamente!");
                cargarEventos();
                
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this, "Error de formato de fecha/hora.\nUse YYYY-MM-DD (ej: 2025-12-31) y HH:MM (ej: 20:30).", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void proponerVenue() {
        JTextField txtNombre = new JTextField();
        JTextField txtUbicacion = new JTextField();
        JSpinner spinnerCapacidad = new JSpinner(new SpinnerNumberModel(100, 1, 100000, 100));

        Object[] message = {
            "Nombre del Venue:", txtNombre,
            "Ubicación:", txtUbicacion,
            "Capacidad Total:", spinnerCapacidad
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Proponer Venue", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String ubicacion = txtUbicacion.getText().trim();
            int capacidad = (int) spinnerCapacidad.getValue();

            if (nombre.isEmpty() || ubicacion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe completar todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ventana.getSistema().proponerVenue(capacidad, nombre, ubicacion);
                JOptionPane.showMessageDialog(this, "¡Solicitud enviada al Administrador!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void crearLocalidad() {
        Evento evento = listaEventosPropios.getSelectedValue();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento de la lista primero.");
            return;
        }

        JTextField txtNombre = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtCapacidad = new JTextField();

        // NUEVO: tipo de tiquete
        JComboBox<String> comboTipoTiquete = new JComboBox<>(new String[] {
            "BASICO", "ENUMERADO", "MULTIPLE"
        });

        // NUEVO: número de sub-tiquetes si es múltiple
        JSpinner spinnerNumSubTiquetes = new JSpinner(
            new SpinnerNumberModel(2, 1, 100, 1)
        );
        spinnerNumSubTiquetes.setEnabled(false);  // solo se habilita si el tipo es MULTIPLE

        comboTipoTiquete.addActionListener(e -> {
            String seleccionado = (String) comboTipoTiquete.getSelectedItem();
            boolean esMultiple = "MULTIPLE".equals(seleccionado);
            spinnerNumSubTiquetes.setEnabled(esMultiple);
        });
        
        JTextField txtDescuento = new JTextField(); // porcentaje, opcional


        Object[] message = {
            "Nombre Localidad (Ej: VIP):", txtNombre,
            "Precio:", txtPrecio,
            "Capacidad:", txtCapacidad,
            "Tipo de tiquete (BASICO, ENUMERADO, MULTIPLE):", comboTipoTiquete,
            "N° de tiquetes que contiene cada tiquete múltiple (solo MULTIPLE):", spinnerNumSubTiquetes,
            "Descuento (% opcional):", txtDescuento
        };

        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            "Añadir Localidad a " + evento.getNombre(),
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                String nombreLoc = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int cap = Integer.parseInt(txtCapacidad.getText().trim());
                String tipoTiquete = (String) comboTipoTiquete.getSelectedItem();

                // Valor para MULTIPLE (por ahora solo lo leemos; tú lo usarás en la lógica)
                double descuento = 0.0;
                String descStr = txtDescuento.getText().trim();
                if (!descStr.isEmpty()) {
                    descuento = Double.parseDouble(descStr); 
                }
                int numSubTiquetes;

                if ("MULTIPLE".equals(tipoTiquete)) {
                    numSubTiquetes = (Integer) spinnerNumSubTiquetes.getValue();
                    ((Organizador) ventana.getSistema().getUsuarioActual()).anadirLocalidadAEvento(nombreLoc, cap, precio, tipoTiquete, evento, descuento/100, numSubTiquetes);
                } else {
                	((Organizador) ventana.getSistema().getUsuarioActual()).anadirLocalidadAEvento(nombreLoc, cap, precio, tipoTiquete, evento, descuento/100);

                }
                

                JOptionPane.showMessageDialog(this, "¡Localidad creada!");
            } catch (NumberFormatException exNum) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error (verifique que precio y capacidad sean números válidos).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
    
    private void verGanancias() {
        Organizador org = (Organizador) ventana.getSistema().getUsuarioActual();

        String[] opciones = {
            "Ganancias globales",
            "Ganancias por evento",
            "Ganancias por localidad",
            "Porcentaje global de venta",
            "Porcentaje de venta por evento",
            "Porcentaje de venta por localidad"
        };

        int seleccion = JOptionPane.showOptionDialog(
                this,
                "¿Qué desea consultar?",
                "Mis Ganancias / Porcentajes",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == JOptionPane.CLOSED_OPTION || seleccion < 0) {
            return;
        }

        try {
            switch (seleccion) {
                // 0) Ganancias globales
                case 0: {
                    double g = org.consultarGananciasGlobales();
                    JOptionPane.showMessageDialog(
                            this,
                            "Ganancias globales: $" + g
                    );
                    break;
                }

                // 1) Ganancias por evento
                case 1: {
                    Evento eSel = seleccionarEvento(org);
                    if (eSel == null) return;

                    double g = org.consultarGananciasEvento(eSel);
                    JOptionPane.showMessageDialog(
                            this,
                            "Ganancias del evento \"" + eSel.getNombre() + "\": $" + g
                    );
                    break;
                }

                // 2) Ganancias por localidad
                case 2: {
                    Evento eSel = seleccionarEvento(org);
                    if (eSel == null) return;

                    Localidad locSel = seleccionarLocalidad(eSel);
                    if (locSel == null) return;

                    double g = org.consultarGananciasLocalidad(locSel);
                    JOptionPane.showMessageDialog(
                            this,
                            "Ganancias de la localidad \"" + locSel.getNombre() +
                            "\" del evento \"" + eSel.getNombre() + "\": $" + g
                    );
                    break;
                }

                // 3) Porcentaje global de venta
                case 3: {
                    double p = org.consultarPorcentajeGlobales();
                    if (Double.isNaN(p) || Double.isInfinite(p)) p = 0.0;
                    JOptionPane.showMessageDialog(
                            this,
                            String.format("Porcentaje global de venta: %.2f %%", p * 100)
                    );
                    break;
                }

                // 4) Porcentaje de venta por evento
                case 4: {
                    Evento eSel = seleccionarEvento(org);
                    if (eSel == null) return;

                    double p = org.consultarPorcentajeEvento(eSel);
                    if (Double.isNaN(p) || Double.isInfinite(p)) p = 0.0;
                    JOptionPane.showMessageDialog(
                            this,
                            String.format(
                                    "Porcentaje de venta del evento \"%s\": %.2f %%",
                                    eSel.getNombre(), p * 100
                            )
                    );
                    break;
                }

                // 5) Porcentaje de venta por localidad
                case 5: {
                    Evento eSel = seleccionarEvento(org);
                    if (eSel == null) return;

                    Localidad locSel = seleccionarLocalidad(eSel);
                    if (locSel == null) return;

                    double p = org.consultarPorcentajeLocalidad(locSel);
                    if (Double.isNaN(p) || Double.isInfinite(p)) p = 0.0;
                    JOptionPane.showMessageDialog(
                            this,
                            String.format(
                                    "Porcentaje de venta de la localidad \"%s\" del evento \"%s\": %.2f %%",
                                    locSel.getNombre(), eSel.getNombre(), p * 100
                            )
                    );
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        
        
    }
 // Permite elegir un evento del organizador (usa la lista interna y la selección actual del JList)
    private Evento seleccionarEvento(Organizador org) {
        List<Evento> eventos = org.getEventosCreados(); 
        if (eventos == null || eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No tienes eventos creados.");
            return null;
        }

        Evento seleccionadoEnLista = listaEventosPropios.getSelectedValue();

        JComboBox<Evento> comboEventos =
                new JComboBox<>(eventos.toArray(new Evento[0]));

        if (seleccionadoEnLista != null) {
            comboEventos.setSelectedItem(seleccionadoEnLista);
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                comboEventos,
                "Seleccione el evento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opt != JOptionPane.OK_OPTION) {
            return null;
        }

        return (Evento) comboEventos.getSelectedItem();
    }

    // Permite elegir una localidad de un evento
    private Localidad seleccionarLocalidad(Evento evento) {
        java.util.List<Localidad> locs = evento.getLocalidades();
        if (locs == null || locs.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El evento seleccionado no tiene localidades.");
            return null;
        }

        JComboBox<Localidad> comboLocs =
                new JComboBox<>(locs.toArray(new Localidad[0]));

        int opt = JOptionPane.showConfirmDialog(
                this,
                comboLocs,
                "Seleccione la localidad",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opt != JOptionPane.OK_OPTION) {
            return null;
        }

        return (Localidad) comboLocs.getSelectedItem();
    }
    
    private void solicitarCancelacionEvento() {
        Organizador org = (Organizador) ventana.getSistema().getUsuarioActual();

        // Intentamos usar el evento seleccionado en la lista
        Evento evento = listaEventosPropios.getSelectedValue();

        if (evento == null) {
            // Si no hay seleccionado, dejamos que elija con el mismo helper de antes
            evento = seleccionarEvento(org);
        }

        if (evento == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un evento para solicitar su cancelación.");
            return;
        }

        // Cuadro para escribir la razón
        JTextArea txtRazon = new JTextArea(5, 30);
        txtRazon.setLineWrap(true);
        txtRazon.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtRazon);

        Object[] mensaje = {
            "Explique la razón de la cancelación:", scroll
        };

        int opt = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Solicitar cancelación de \"" + evento.getNombre() + "\"",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opt != JOptionPane.OK_OPTION) {
            return;
        }

        String razon = txtRazon.getText().trim();
        if (razon.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe escribir una razón para la cancelación.");
            return;
        }

        try {
            ventana.getSistema().solicitarCancelacionEvento(evento, razon);
            JOptionPane.showMessageDialog(this,
                    "Solicitud de cancelación enviada al administrador.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al solicitar cancelación: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}