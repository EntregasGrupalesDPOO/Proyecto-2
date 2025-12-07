package interfaz;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import logica.Administrador;
import logica.BoletasMaster;
import logica.Evento;
import logica.Localidad;

public class DialogoCompra extends JDialog {
	private static final String PANTALLA_TIPO            = "tipo";
    private static final String PANTALLA_EVENTO_LOCALIDAD= "evento_localidad";
    private static final String PANTALLA_SIMPLE          = "simple";
    private static final String PANTALLA_NUMERADO        = "numerado";
    private static final String PANTALLA_MULTI           = "multi";
    private static final String PANTALLA_MULTI_LOCALIDADES = "multi_localidades";


    private List<Evento> eventosSeleccionadosMulti;
    private JPanel panelMultiLocalidades; 
    private HashMap<Evento, JComboBox<Localidad>> mapaComboLocalidades;

    private final BoletasMaster sistema;

    private CardLayout cardLayout;
    private JPanel panelCards;
    private JRadioButton rbUnicoEvento;
    private JRadioButton rbMultiEvento;

    private JComboBox<Evento> comboEventoUnico;
    private JComboBox<Localidad> comboLocalidad;

    private Evento eventoSeleccionado;
    private Localidad localidadSeleccionada;

    private JSpinner spinnerCantidadSimple;
    private JCheckBox chkUsarSaldoSimple;

    private JSpinner spinnerCantidadNumerado;
    private JCheckBox chkUsarSaldoNumerado;

    private JList<Evento> listaEventosMulti;
    private JCheckBox chkUsarSaldoMulti;

    public DialogoCompra(Window owner, BoletasMaster sistema) {
        super(owner, "Comprar Tiquetes", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;

        setSize(500, 350);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);

        panelCards.add(crearPanelTipoCompra(), PANTALLA_TIPO);
        panelCards.add(crearPanelEventoLocalidad(), PANTALLA_EVENTO_LOCALIDAD);
        panelCards.add(crearPanelCompraSimple(), PANTALLA_SIMPLE);
        panelCards.add(crearPanelCompraNumerado(), PANTALLA_NUMERADO);
        panelCards.add(crearPanelMultiEvento(), PANTALLA_MULTI);
        panelCards.add(crearPanelMultiLocalidades(), PANTALLA_MULTI_LOCALIDADES);


        add(panelCards, BorderLayout.CENTER);

        // Iniciar en selección de tipo
        cardLayout.show(panelCards, PANTALLA_TIPO);
    }

    // ======================
    //  PANTALLA 0: TIPO
    // ======================
    private JPanel crearPanelTipoCompra() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("¿Qué tipo de compra desea realizar?");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 10, 10));
        rbUnicoEvento = new JRadioButton("Tiquete para un solo evento");
        rbMultiEvento = new JRadioButton("Tiquete para múltiples eventos");

        ButtonGroup group = new ButtonGroup();
        group.add(rbUnicoEvento);
        group.add(rbMultiEvento);
        rbUnicoEvento.setSelected(true);

        centro.add(rbUnicoEvento);
        centro.add(rbMultiEvento);

        panel.add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSiguiente = new JButton("Siguiente");

        btnCancelar.addActionListener(e -> dispose());
        btnSiguiente.addActionListener(e -> {
            if (rbUnicoEvento.isSelected()) {
                cargarEventosEnCombos();
                cardLayout.show(panelCards, PANTALLA_EVENTO_LOCALIDAD);
            } else {
                cargarEventosMulti();
                cardLayout.show(panelCards, PANTALLA_MULTI);
            }
        });

        sur.add(btnCancelar);
        sur.add(btnSiguiente);

        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // =============================
    //  PANTALLA 1: EVENTO + LOCALIDAD
    // =============================
    private JPanel crearPanelEventoLocalidad() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Seleccione evento y localidad");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 10, 10));

        centro.add(new JLabel("Evento:"));
        comboEventoUnico = new JComboBox<>();
        centro.add(comboEventoUnico);

        centro.add(new JLabel("Localidad:"));
        comboLocalidad = new JComboBox<>();
        centro.add(comboLocalidad);

        panel.add(centro, BorderLayout.CENTER);

        // Renderers: mostrar nombre
        comboEventoUnico.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Evento) {
                    setText(((Evento) value).getNombre());
                }
                return this;
            }
        });

        comboLocalidad.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Localidad) {
                    setText(((Localidad) value).getNombre());
                }
                return this;
            }
        });

        comboEventoUnico.addActionListener(e -> actualizarLocalidades());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtras = new JButton("Atrás");
        JButton btnSiguiente = new JButton("Siguiente");

        btnAtras.addActionListener(e -> cardLayout.show(panelCards, PANTALLA_TIPO));

        btnSiguiente.addActionListener(e -> {
            eventoSeleccionado = (Evento) comboEventoUnico.getSelectedItem();
            localidadSeleccionada = (Localidad) comboLocalidad.getSelectedItem();

            if (eventoSeleccionado == null || localidadSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un evento y una localidad.");
                return;
            }

            // Decidimos si la localidad tiene asientos numerados
            if (localidadSeleccionada.getTipoTiquete().equals("ENUMERADO")) { // ajusta nombre si tu método se llama distinto
                cardLayout.show(panelCards, PANTALLA_NUMERADO);
            } else {
                cardLayout.show(panelCards, PANTALLA_SIMPLE);
            }
        });

        sur.add(btnAtras);
        sur.add(btnSiguiente);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarEventosEnCombos() {
        comboEventoUnico.removeAllItems();
        for (ArrayList<Evento> lista : Administrador.getEventosPorFecha().values()) {
            for (Evento e : lista) {
                comboEventoUnico.addItem(e);
            }
        }
        actualizarLocalidades();
    }

    private void actualizarLocalidades() {
        comboLocalidad.removeAllItems();
        Evento e = (Evento) comboEventoUnico.getSelectedItem();
        if (e != null) {
            for (Localidad l : e.getLocalidades()) {
                comboLocalidad.addItem(l);
            }
        }
    }

    // ======================
    //  PANTALLA 2A: SIMPLE
    // ======================
    private JPanel crearPanelCompraSimple() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Compra de tiquetes");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 10, 10));
        centro.add(new JLabel("Cantidad de tiquetes:"));
        spinnerCantidadSimple = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        centro.add(spinnerCantidadSimple);

        centro.add(new JLabel("Usar saldo virtual:"));
        chkUsarSaldoSimple = new JCheckBox();
        centro.add(chkUsarSaldoSimple);

        panel.add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtras = new JButton("Atrás");
        JButton btnComprar = new JButton("Comprar");

        btnAtras.addActionListener(e -> cardLayout.show(panelCards, PANTALLA_EVENTO_LOCALIDAD));
        btnComprar.addActionListener(e -> comprarSimple());

        sur.add(btnAtras);
        sur.add(btnComprar);

        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void comprarSimple() {
        if (eventoSeleccionado == null || localidadSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Falta seleccionar evento y localidad.");
            cardLayout.show(panelCards, PANTALLA_EVENTO_LOCALIDAD);
            return;
        }

        int cantidad = (Integer) spinnerCantidadSimple.getValue();
        boolean usarSaldo = chkUsarSaldoSimple.isSelected();

        try {
            this.sistema.getUsuarioActual().comprarTiquete(cantidad, eventoSeleccionado, localidadSeleccionada.getNombre(), usarSaldo);
            JOptionPane.showMessageDialog(this, "¡Compra exitosa!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al comprar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================
    //  PANTALLA 2B: NUMERADO
    // =========================
    private JPanel crearPanelCompraNumerado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Compra con asientos numerados");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 10, 10));
        centro.add(new JLabel("Cantidad de tiquetes (asientos):"));
        spinnerCantidadNumerado = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        centro.add(spinnerCantidadNumerado);

        centro.add(new JLabel("Usar saldo virtual:"));
        chkUsarSaldoNumerado = new JCheckBox();
        centro.add(chkUsarSaldoNumerado);

        panel.add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtras = new JButton("Atrás");
        JButton btnSiguiente = new JButton("Seleccionar asientos…");

        btnAtras.addActionListener(e -> cardLayout.show(panelCards, PANTALLA_EVENTO_LOCALIDAD));
        btnSiguiente.addActionListener(e -> comprarNumerado());

        sur.add(btnAtras);
        sur.add(btnSiguiente);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void comprarNumerado() {
        if (eventoSeleccionado == null || localidadSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Falta seleccionar evento y localidad.");
            cardLayout.show(panelCards, PANTALLA_EVENTO_LOCALIDAD);
            return;
        }

        int cantidad = (Integer) spinnerCantidadNumerado.getValue();
        boolean usarSaldo = chkUsarSaldoNumerado.isSelected();

        try {
            // Aquí asumimos que el sistema te puede dar la lista de asientos disponibles para esa localidad
            ArrayList<String> disponibles = new ArrayList<String>();
            for (int i = 1; i < localidadSeleccionada.getTiquetes().size() + 1; i++) {
            	disponibles.add(String.valueOf(i));
            }
            DialogoSeleccionAsientos dlg = new DialogoSeleccionAsientos(
                    this,
                    disponibles,
                    cantidad
            );
            dlg.setVisible(true);

            ArrayList<String> seleccion = (ArrayList<String>) dlg.getSeleccionFinal();
            if (seleccion == null || seleccion.isEmpty()) {
                // Usuario canceló o no seleccionó nada
                return;
            }
            ArrayList<Integer> seleccionInt = new ArrayList<Integer>();
            for(String str:seleccion) {
            	seleccionInt.add(Integer.parseInt(str));
            }
            sistema.getUsuarioActual().comprarTiquete(cantidad, eventoSeleccionado, localidadSeleccionada.getNombre(), seleccionInt, usarSaldo);

            JOptionPane.showMessageDialog(this, "¡Compra exitosa!");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al comprar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================
    //  PANTALLA MULTI-EVENTO
    // =========================
    private JPanel crearPanelMultiEvento() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Tiquete para múltiples eventos");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblTitulo, BorderLayout.NORTH);

        listaEventosMulti = new JList<>();
        listaEventosMulti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaEventosMulti.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Evento) {
                    setText(((Evento) value).getNombre());
                }
                return this;
            }
        });

        panel.add(new JScrollPane(listaEventosMulti), BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout());

        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkUsarSaldoMulti = new JCheckBox("Usar saldo virtual");
        opciones.add(chkUsarSaldoMulti);
        abajo.add(opciones, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtras = new JButton("Atrás");
        JButton btnSiguiente = new JButton("Siguiente");

        btnAtras.addActionListener(e -> cardLayout.show(panelCards, PANTALLA_TIPO));
        btnSiguiente.addActionListener(e -> irAPantallaMultiLocalidades());

        botones.add(btnAtras);
        botones.add(btnSiguiente);

        abajo.add(botones, BorderLayout.EAST);

        panel.add(abajo, BorderLayout.SOUTH);

        return panel;
    }
    
    private void irAPantallaMultiLocalidades() {
        List<Evento> seleccionados = listaEventosMulti.getSelectedValuesList();
        if (seleccionados == null || seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un evento.");
            return;
        }

        this.eventosSeleccionadosMulti = new ArrayList<>(seleccionados);
        construirPanelMultiLocalidades();
        cardLayout.show(panelCards, PANTALLA_MULTI_LOCALIDADES);
    }
    
    private void construirPanelMultiLocalidades() {
        if (eventosSeleccionadosMulti == null || eventosSeleccionadosMulti.isEmpty()) {
            return;
        }

        JPanel centro = new JPanel(new GridLayout(eventosSeleccionadosMulti.size(), 2, 10, 10));
        mapaComboLocalidades = new HashMap<>();

        for (Evento e : eventosSeleccionadosMulti) {
            JLabel lblEvento = new JLabel(e.getNombre());
            centro.add(lblEvento);

            JComboBox<Localidad> comboLoc = new JComboBox<>();
            for (Localidad l : e.getLocalidades()) {
                comboLoc.addItem(l);
            }

            comboLoc.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Localidad) {
                        setText(((Localidad) value).getNombre());
                    }
                    return this;
                }
            });

            centro.add(comboLoc);
            mapaComboLocalidades.put(e, comboLoc);
        }

        panelMultiLocalidades.add(centro, BorderLayout.CENTER);
        panelMultiLocalidades.revalidate();
        panelMultiLocalidades.repaint();
    }
    
    private JPanel crearPanelMultiLocalidades() {
        panelMultiLocalidades = new JPanel(new BorderLayout(10, 10));
        panelMultiLocalidades.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Seleccione la localidad para cada evento");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        panelMultiLocalidades.add(lblTitulo, BorderLayout.NORTH);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtras = new JButton("Atrás");
        JButton btnComprar = new JButton("Comprar");

        btnAtras.addActionListener(e -> cardLayout.show(panelCards, PANTALLA_MULTI));
        btnComprar.addActionListener(e -> comprarMultiEventoConLocalidades());

        sur.add(btnAtras);
        sur.add(btnComprar);

        panelMultiLocalidades.add(sur, BorderLayout.SOUTH);

        return panelMultiLocalidades;
    }
    
    private void comprarMultiEventoConLocalidades() {
        if (eventosSeleccionadosMulti == null || eventosSeleccionadosMulti.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay eventos seleccionados.");
            cardLayout.show(panelCards, PANTALLA_MULTI);
            return;
        }

        HashMap<Evento, String> mapaEventoLocalidad = new HashMap<>();

        for (Evento e : eventosSeleccionadosMulti) {
            JComboBox<Localidad> comboLoc = mapaComboLocalidades.get(e);
            if (comboLoc == null) {
                JOptionPane.showMessageDialog(this, "Falta seleccionar localidad para el evento: " + e.getNombre());
                return;
            }

            Localidad locSeleccionada = (Localidad) comboLoc.getSelectedItem();
            if (locSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Falta seleccionar localidad para el evento: " + e.getNombre());
                return;
            }

            mapaEventoLocalidad.put(e, locSeleccionada.getNombre());
        }

        boolean usarSaldo = chkUsarSaldoMulti != null && chkUsarSaldoMulti.isSelected();

        try {

            sistema.getUsuarioActual().comprarTiqueteMultiEvento(mapaEventoLocalidad, usarSaldo);

            JOptionPane.showMessageDialog(this, "¡Compra multi-evento exitosa!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al comprar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void cargarEventosMulti() {
        DefaultListModel<Evento> modelo = new DefaultListModel<>();
        for (ArrayList<Evento> lista : Administrador.getEventosPorFecha().values()) {
            for (Evento e : lista) {
                modelo.addElement(e);
            }
        }
        listaEventosMulti.setModel(modelo);
    }

}