package interfaz;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import logica.Administrador;
import logica.Evento;
import logica.Organizador;
import logica.Solicitud;

public class PanelAdmin extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Solicitud> listaSolicitudes;
    private DefaultListModel<Solicitud> modeloLista;

    public PanelAdmin(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());

        add(new JLabel("Panel de Administración"), BorderLayout.NORTH);

        // Lista de solicitudes
        modeloLista = new DefaultListModel<>();
        cargarSolicitudes();
        listaSolicitudes = new JList<>(modeloLista);
        add(new JScrollPane(listaSolicitudes), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 4, 10, 10));


        JButton btnAceptar = new JButton("Aceptar Solicitud");
        JButton btnRechazar = new JButton("Rechazar Solicitud");
        JButton btnGanancias = new JButton("Ver Ganancias Globales");
        JButton btnConfigTarifaImpresion = new JButton("Tarifa Impresión");
        JButton btnConfigTarifaTipoEvento = new JButton("Tarifa por Tipo de Evento");
        JButton btnConfigMaximos = new JButton("Máximos por Transacción");
        JButton btnLogout = new JButton("Cerrar Sesión");
        JButton btnCancelarEvento = new JButton("Cancelar Evento");


        panelBotones.add(btnAceptar);
        panelBotones.add(btnRechazar);
        panelBotones.add(btnGanancias);
        panelBotones.add(btnConfigTarifaImpresion);
        panelBotones.add(btnConfigTarifaTipoEvento);
        panelBotones.add(btnConfigMaximos);
        panelBotones.add(btnCancelarEvento); 
        panelBotones.add(btnLogout);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones básicas
        btnLogout.addActionListener(e -> ventana.cerrarSesion());

        btnAceptar.addActionListener(e -> atenderSolicitud(true));
        btnRechazar.addActionListener(e -> atenderSolicitud(false));

        btnGanancias.addActionListener(e -> mostrarDialogoGanancias());
        
        btnCancelarEvento.addActionListener(e -> cancelarEventoDirecto());


        // 🔧 NUEVO: fijar tarifa de impresión
        btnConfigTarifaImpresion.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Ingrese la tarifa de impresión (valor entero en pesos):",
                    "Configurar Tarifa de Impresión",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (input == null) return; // cancelado

            try {
                int valor = Integer.parseInt(input.trim());
                ventana.getSistema().getAdministrador().fijarTarifaImpresion(valor);
                JOptionPane.showMessageDialog(this, "Tarifa de impresión actualizada a: $" + valor);
            } catch (NumberFormatException exNum) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al fijar tarifa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 🔧 NUEVO: añadir tarifa por tipo de evento
        btnConfigTarifaTipoEvento.addActionListener(e -> {
            JTextField txtTipo = new JTextField();
            JTextField txtValor = new JTextField();

            Object[] msg = {
                    "Tipo de evento (ej: MUSICAL, DEPORTIVO):", txtTipo,
                    "Tarifa asociada (double):", txtValor
            };

            int opt = JOptionPane.showConfirmDialog(
                    this,
                    msg,
                    "Añadir/Actualizar Tarifa por Tipo de Evento",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (opt != JOptionPane.OK_OPTION) return;

            try {
                String tipo = txtTipo.getText().trim();
                double valor = Double.parseDouble(txtValor.getText().trim());

                if (tipo.isEmpty()) {
                    throw new IllegalArgumentException("El tipo de evento no puede estar vacío.");
                }

                ventana.getSistema().getAdministrador().anadirTarifaTipoEvento(tipo, valor);
                JOptionPane.showMessageDialog(this,
                        "Tarifa para tipo '" + tipo + "' fijada en: " + valor);
            } catch (NumberFormatException exNum) {
                JOptionPane.showMessageDialog(this, "El valor debe ser numérico (double).", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al configurar tarifa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 🔧 NUEVO: fijar máximos por transacción
        btnConfigMaximos.addActionListener(e -> {
            JSpinner spinnerInd = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
            JSpinner spinnerMult = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));

            Object[] msg = {
                    "Máximo tiquetes INDIVIDUALES por transacción:", spinnerInd,
                    "Máximo tiquetes MÚLTIPLES por transacción:", spinnerMult
            };

            int opt = JOptionPane.showConfirmDialog(
                    this,
                    msg,
                    "Configurar Máximos por Transacción",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (opt != JOptionPane.OK_OPTION) return;

            try {
                int maxInd = (Integer) spinnerInd.getValue();
                int maxMult = (Integer) spinnerMult.getValue();

                ventana.getSistema().getAdministrador().fijarMaximosPorTransaccion(maxInd, maxMult);
                JOptionPane.showMessageDialog(this,
                        "Máximos actualizados:\n" +
                        "Individuales: " + maxInd + "\n" +
                        "Múltiples: " + maxMult);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al fijar máximos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Carga las solicitudes pendientes en la lista gráfica
    private void cargarSolicitudes() {
        modeloLista.clear();
        for (Solicitud s : ventana.getSistema().getAdministrador().getSolicitudes()) {
            if (s.getEstado().equals(Solicitud.ESTADO_PENDIENTE)) {
                modeloLista.addElement(s);
            }
        }
    }

    // Atiende la solicitud seleccionada (acepta = true/false)
    private void atenderSolicitud(boolean aceptar) {
        Solicitud s = listaSolicitudes.getSelectedValue();
        if (s == null) return;
        try {
            ventana.getSistema().getAdministrador().atenderSolicitud(s, aceptar);
            JOptionPane.showMessageDialog(this, "Solicitud " + (aceptar ? "Aceptada" : "Rechazada"));
            cargarSolicitudes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void mostrarDialogoGanancias() {
        String[] opciones = {
            "Globales",
            "Por fecha",
            "Por organizador",
            "Por evento"
        };

        int seleccion = JOptionPane.showOptionDialog(
                this,
                "¿Qué tipo de consulta de ganancias desea realizar?",
                "Consultar Ganancias",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == JOptionPane.CLOSED_OPTION || seleccion < 0) {
            return;
        }

        Administrador admin = ventana.getSistema().getAdministrador();

        try {
            switch (seleccion) {

                // 0) GANANCIAS GLOBALES
                case 0: {
                    double g = admin.gananciasGlobales();
                    JOptionPane.showMessageDialog(this,
                            "Ganancias globales: $" + g);
                    break;
                }

                // 1) POR FECHA (usando el HashMap eventosPorFecha)
                case 1: {
                    HashMap<LocalDate, ArrayList<Evento>> mapa = Administrador.eventosPorFecha;
                    if (mapa == null || mapa.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "No hay eventos registrados por fecha.");
                        return;
                    }

                    List<LocalDate> fechas = new ArrayList<>(mapa.keySet());
                    fechas.sort(null); // ordena cronológicamente

                    JComboBox<LocalDate> comboFechas =
                            new JComboBox<>(fechas.toArray(new LocalDate[0]));

                    int opt = JOptionPane.showConfirmDialog(
                            this,
                            comboFechas,
                            "Seleccione la fecha",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (opt != JOptionPane.OK_OPTION) return;

                    LocalDate fechaSel = (LocalDate) comboFechas.getSelectedItem();
                    if (fechaSel == null) return;

                    double g = admin.gananciasPorFecha(fechaSel);
                    JOptionPane.showMessageDialog(this,
                            "Ganancias para la fecha " + fechaSel + ": $" + g);
                    break;
                }

                // 2) POR ORGANIZADOR (usando eventosPorOrganizador)
                case 2: {
                    HashMap<Organizador, ArrayList<Evento>> mapaOrg =
                            Administrador.eventosPorOrganizador;

                    if (mapaOrg == null || mapaOrg.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "No hay organizadores con eventos registrados.");
                        return;
                    }

                    List<Organizador> organizadores = new ArrayList<>(mapaOrg.keySet());

                    JComboBox<Organizador> comboOrg =
                            new JComboBox<>(organizadores.toArray(new Organizador[0]));

                    int opt = JOptionPane.showConfirmDialog(
                            this,
                            comboOrg,
                            "Seleccione el organizador",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (opt != JOptionPane.OK_OPTION) return;

                    Organizador orgSel = (Organizador) comboOrg.getSelectedItem();
                    if (orgSel == null) return;

                    double g = admin.gananciasPorOrganizador(orgSel);
                    JOptionPane.showMessageDialog(this,
                            "Ganancias para el organizador " + orgSel.getLogin() +
                            ": $" + g);
                    break;
                }

                // 3) POR EVENTO (primero organizador, luego evento de ese organizador)
                case 3: {
                    HashMap<Organizador, ArrayList<Evento>> mapaOrg =
                            Administrador.eventosPorOrganizador;

                    if (mapaOrg == null || mapaOrg.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "No hay organizadores con eventos registrados.");
                        return;
                    }

                    // 3.1 Seleccionar organizador
                    List<Organizador> organizadores = new ArrayList<>(mapaOrg.keySet());
                    JComboBox<Organizador> comboOrg =
                            new JComboBox<>(organizadores.toArray(new Organizador[0]));

                    int optOrg = JOptionPane.showConfirmDialog(
                            this,
                            comboOrg,
                            "Seleccione el organizador",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (optOrg != JOptionPane.OK_OPTION) return;

                    Organizador orgSel = (Organizador) comboOrg.getSelectedItem();
                    if (orgSel == null) return;

                    // 3.2 Tomar sus eventos desde su ArrayList<Evento> interno
                    // asumo que tienes un getter tipo getEventos() o getEventosCreados()
                    List<Evento> eventosOrg = orgSel.getEventosCreados(); 
                    // si tu método se llama distinto, cámbialo aquí, por ejemplo getEventosCreados()

                    if (eventosOrg == null || eventosOrg.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "El organizador seleccionado no tiene eventos.");
                        return;
                    }

                    JComboBox<Evento> comboEventos =
                            new JComboBox<>(eventosOrg.toArray(new Evento[0]));

                    int optEv = JOptionPane.showConfirmDialog(
                            this,
                            comboEventos,
                            "Seleccione el evento",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (optEv != JOptionPane.OK_OPTION) return;

                    Evento eSel = (Evento) comboEventos.getSelectedItem();
                    if (eSel == null) return;

                    double g = admin.gananciasPorEvento(eSel);
                    JOptionPane.showMessageDialog(this,
                            "Ganancias para el evento \"" + eSel.getNombre() +
                            "\": $" + g);
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar ganancias: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelarEventoDirecto() {
        Administrador admin = ventana.getSistema().getAdministrador();

        // Usamos el mismo mapa estático que en mostrarDialogoGanancias
        HashMap<Organizador, ArrayList<Evento>> mapaOrg =
                Administrador.eventosPorOrganizador;

        if (mapaOrg == null || mapaOrg.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay organizadores con eventos registrados.");
            return;
        }

        // 1) Seleccionar organizador
        java.util.List<Organizador> organizadores = new ArrayList<>(mapaOrg.keySet());
        JComboBox<Organizador> comboOrg =
                new JComboBox<>(organizadores.toArray(new Organizador[0]));

        int optOrg = JOptionPane.showConfirmDialog(
                this,
                comboOrg,
                "Seleccione el organizador del evento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (optOrg != JOptionPane.OK_OPTION) return;

        Organizador orgSel = (Organizador) comboOrg.getSelectedItem();
        if (orgSel == null) {
            JOptionPane.showMessageDialog(this,
                    "No se seleccionó organizador.");
            return;
        }

        // 2) Tomar sus eventos (desde el propio organizador)
        java.util.List<Evento> eventosOrg = orgSel.getEventosCreados(); // ajusta si tu getter tiene otro nombre
        if (eventosOrg == null || eventosOrg.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El organizador seleccionado no tiene eventos.");
            return;
        }

        JComboBox<Evento> comboEventos =
                new JComboBox<>(eventosOrg.toArray(new Evento[0]));

        int optEv = JOptionPane.showConfirmDialog(
                this,
                comboEventos,
                "Seleccione el evento a cancelar",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (optEv != JOptionPane.OK_OPTION) return;

        Evento eSel = (Evento) comboEventos.getSelectedItem();
        if (eSel == null) {
            JOptionPane.showMessageDialog(this,
                    "No se seleccionó evento.");
            return;
        }

        // 3) Confirmación final
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de cancelar el evento \"" + eSel.getNombre() + "\"?\n" +
                "Se reembolsarán automáticamente los tiquetes comprados.",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            admin.cancelarEvento(eSel);
            JOptionPane.showMessageDialog(this,
                    "Evento \"" + eSel.getNombre() + "\" cancelado y tiquetes reembolsados.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cancelar evento: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
