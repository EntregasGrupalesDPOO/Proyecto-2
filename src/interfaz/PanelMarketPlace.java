package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import Marketplace.Oferta;
import logica.*;

public class PanelMarketPlace extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Oferta> listaOfertas;
    private DefaultListModel<Oferta> modeloLista;

    public PanelMarketPlace(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);

        // 1. Titre
        JLabel lblTitulo = new JLabel("Marketplace - Compra y Reventa");
        lblTitulo.setFont(Estilos.FUENTE_TITULO);
        lblTitulo.setForeground(Estilos.COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // 2. Liste des Offres
        modeloLista = new DefaultListModel<>();
        listaOfertas = new JList<>(modeloLista);
        listaOfertas.setFont(Estilos.FUENTE_NORMAL);
        listaOfertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // --- RENDERER PERSONNALISÉ (Pour afficher Venue, Localidad, etc.) ---
        listaOfertas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof Oferta) {
                    Oferta o = (Oferta) value;
                    Tiquete t = o.getTiquete();
                    
                    // On cherche les infos détaillées (Venue, Evento, Localidad)
                    String detalles = buscarDetalles(t);
                    
                    // Formatage HTML pour l'affichage
                    String html = "<html>" +
                            "<div style='padding:5px; border-bottom:1px solid #ccc'>" +
                            "<font size='4' color='#2c3e50'><b>" + detalles + "</b></font><br>" + // Nom Event + Venue
                            "Fecha: <b>" + t.getFecha() + "</b> | Hora: " + t.getHora() + "<br>" +
                            "Precio: <font color='green'><b>$" + o.getPrecio() + "</b></font> " +
                            "<i>(Vendedor: " + o.getVendedor().getLogin() + ")</i>" +
                            "</div></html>";
                    
                    setText(html);
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(listaOfertas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // 3. Boutons d'Action
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Estilos.COLOR_FONDO);

        JButton btnRefrescar = new JButton("Actualizar");
        Estilos.estilizarBoton(btnRefrescar, Estilos.COLOR_PRIMARIO);

        // Bouton pour l'achat direct (Demandé)
        JButton btnComprar = new JButton("Comprar Ahora");
        Estilos.estilizarBoton(btnComprar, Estilos.COLOR_EXITO); // Vert

        JButton btnContraOferta = new JButton("Hacer Contraoferta");
        Estilos.estilizarBoton(btnContraOferta, Estilos.COLOR_SECUNDARIO); // Orange

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnComprar);
        panelBotones.add(btnContraOferta);
        add(panelBotones, BorderLayout.SOUTH);

        // --- ACTIONS ---
        btnRefrescar.addActionListener(e -> cargarOfertas());
        
        // Action d'achat direct
        btnComprar.addActionListener(e -> comprarOfertaDirectamente());

        btnContraOferta.addActionListener(e -> hacerContraOferta());

        // Charge initiale
        cargarOfertas();
    }

    /**
     * Méthode utilitaire pour retrouver l'Événement et le Venue d'un ticket.
     * Comme le Tiquete ne connait pas son parent, on parcourt le système.
     */
    private String buscarDetalles(Tiquete tiqueteObjetivo) {
        BoletasMaster sistema = ventana.getSistema();
        
        // Parcourir tous les événements
        for (ArrayList<Evento> lista: Administrador.eventosPorOrganizador.values()) {
            // Parcourir toutes les localités de l'événement
            for (Evento evento : lista) {
            	for (Localidad loc : evento.getLocalidades()) {
                    // Vérifier si le ticket est dans cette localité
                    // Note: On vérifie par ID car l'objet peut être une copie sérialisée différente
                    for (Tiquete t : loc.getTiquetes()) {
                        if (t.getId() == tiqueteObjetivo.getId()) {
                            return evento.getNombre() + " @ " + evento.getVenue().getNombre() + " (" + loc.getNombre() + ")";
                        }
                    }
                }
            }
        }
        return "Evento Desconocido";
    }

    private void cargarOfertas() {
        modeloLista.clear();
        ArrayList<Oferta> ofertas = ventana.getSistema().verOfertas();
        String miUsuario = ventana.getSistema().getUsuarioActual().getLogin();

        for (Oferta o : ofertas) {
            // On affiche seulement les offres non vendues et qui ne sont pas à moi
            if (!o.isVendida() && !o.getVendedor().getLogin().equals(miUsuario)) {
                modeloLista.addElement(o);
            }
        }
    }

    private void comprarOfertaDirectamente() {
        Oferta seleccionada = listaOfertas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una oferta para comprar.");
            return;
        }

        // Checkbox para decidir si usa saldo virtual
        JCheckBox chkUsarSaldo = new JCheckBox("Usar saldo virtual", true); // por defecto marcado

        Object[] mensaje = {
            "¿Desea comprar este tiquete por $" + seleccionada.getPrecio() + "?",
            chkUsarSaldo
        };

        int confirm = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Confirmar Compra",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.OK_OPTION) {
            try {
                Cliente yo = (Cliente) ventana.getSistema().getUsuarioActual();

                boolean usarSaldo = chkUsarSaldo.isSelected();

                // ahora pasamos el valor real de usarSaldo
                yo.acceptarOferta(seleccionada, usarSaldo);

                JOptionPane.showMessageDialog(this, "¡Compra realizada con éxito! El tiquete es tuyo.");
                cargarOfertas(); // refrescar la lista

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo comprar: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void hacerContraOferta() {
        Oferta seleccionada = listaOfertas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una oferta de la lista.");
            return;
        }

        // Campo de texto para el nuevo precio
        JTextField txtPrecio = new JTextField(
            String.valueOf(seleccionada.getPrecio())
        );

        // CheckBox para decidir si usar saldo virtual
        JCheckBox chkUsarSaldo = new JCheckBox("Usar saldo virtual");
        chkUsarSaldo.setSelected(true); // por defecto SÍ usa saldo

        Object[] msg = {
            "El precio actual es $" + seleccionada.getPrecio() + 
            "\nIngrese su contraoferta:", 
            txtPrecio,
            chkUsarSaldo
        };

        int opt = JOptionPane.showConfirmDialog(
                this,
                msg,
                "Hacer Contraoferta",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opt != JOptionPane.OK_OPTION) {
            return; // canceló
        }

        String precioStr = txtPrecio.getText().trim();
        if (precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double nuevoPrecio = Double.parseDouble(precioStr);
            boolean usarSaldo = chkUsarSaldo.isSelected();

            // Ahora pasamos el bool a la lógica
            ventana.getSistema().hacerContraOferta(seleccionada, nuevoPrecio, usarSaldo);

            JOptionPane.showMessageDialog(this, "¡Contraoferta enviada al vendedor!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}