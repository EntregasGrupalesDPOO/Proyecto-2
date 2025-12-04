package interfaz;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import logica.Evento;
import logica.Organizador;
import logica.Venue;

public class PanelOrganizador extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Evento> listaEventosPropios;
    private DefaultListModel<Evento> modeloLista;

    public PanelOrganizador(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        
        // En-tête
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Panel de Organizador - Mis Eventos"));
        add(topPanel, BorderLayout.NORTH);

        // Liste des événements
        modeloLista = new DefaultListModel<>();
        cargarEventos();
        listaEventosPropios = new JList<>(modeloLista);
        
        // Affichage personnalisé
        listaEventosPropios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Evento) {
                    Evento ev = (Evento) value;
                    setText(ev.getNombre() + " (" + ev.getFecha() + " " + ev.getHora() + ") - " + ev.getEstado());
                }
                return this;
            }
        });
        add(new JScrollPane(listaEventosPropios), BorderLayout.CENTER);

        // --- BOUTONS ---
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 5, 5));
        
        JButton btnCrearEvento = new JButton("Crear Evento");
        JButton btnProponerVenue = new JButton("Proponer Nuevo Venue");
        JButton btnCrearLocalidad = new JButton("Añadir Localidad");
        JButton btnEstadisticas = new JButton("Ver Mis Ganancias");
        JButton btnComprar = new JButton("Comprar Tiquetes");
        JButton btnLogout = new JButton("Cerrar Sesión");

        panelBotones.add(btnCrearEvento);
        panelBotones.add(btnProponerVenue);
        panelBotones.add(btnCrearLocalidad);
        panelBotones.add(btnEstadisticas);
        panelBotones.add(btnComprar);
        panelBotones.add(btnLogout);
        
        add(panelBotones, BorderLayout.SOUTH);

        // --- ACTIONS ---
        btnLogout.addActionListener(e -> ventana.cerrarSesion());
        btnComprar.addActionListener(e -> new DialogoCompra(ventana, ventana.getSistema()).setVisible(true));
        btnCrearEvento.addActionListener(e -> crearEvento());
        btnProponerVenue.addActionListener(e -> proponerVenue());
        btnCrearLocalidad.addActionListener(e -> crearLocalidad());
        btnEstadisticas.addActionListener(e -> verGanancias());
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

    // --- MODIFICATION ICI : Ajout des champs Date et Heure ---
    private void crearEvento() {
        if (ventana.getSistema().getVenues().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay Venues disponibles.\nProponga uno nuevo y espere a que el Admin lo apruebe.");
            return;
        }
        
        // Sélection du Venue
        JComboBox<Venue> comboVenues = new JComboBox<>();
        for (Venue v : ventana.getSistema().getVenues()) {
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

        // Champs de saisie
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtTipo = new JTextField("MUSICAL");
        // Pré-remplir avec la date de demain pour aider l'utilisateur
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
                // Conversion des textes en objets Date/Heure
                LocalDate fecha = LocalDate.parse(fechaStr);
                LocalTime hora = LocalTime.parse(horaStr);

                ventana.getSistema().agendarEvento(nombre, desc, v, 
                        (Organizador)ventana.getSistema().getUsuarioActual(), 
                        tipo, fecha, hora);
                
                JOptionPane.showMessageDialog(this, "Evento Creado Exitosamente!");
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
        
        Object[] message = {
            "Nombre Localidad (Ej: VIP):", txtNombre,
            "Precio:", txtPrecio,
            "Capacidad:", txtCapacidad
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Añadir Localidad a " + evento.getNombre(), JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String nombreLoc = txtNombre.getText();
                double precio = Double.parseDouble(txtPrecio.getText());
                int cap = Integer.parseInt(txtCapacidad.getText());
                
                ventana.getSistema().crearLocalidadEvento(nombreLoc, cap, precio, "BASICO", evento);
                JOptionPane.showMessageDialog(this, "Localidad creada!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error (Verifique números): " + ex.getMessage());
            }
        }
    }
    
    private void verGanancias() {
        Organizador org = (Organizador) ventana.getSistema().getUsuarioActual();
        JOptionPane.showMessageDialog(this, "Mis Ganancias Globales: $" + org.consultarGananciasGlobales());
    }
}