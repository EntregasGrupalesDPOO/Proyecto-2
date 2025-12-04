package interfaz;

import javax.swing.*;
import java.awt.*;
import Marketplace.ContraOferta;
import Marketplace.Oferta;
import logica.Cliente;

public class PanelMisVentas extends JPanel {
    private VentanaPrincipal ventana;
    private JList<String> listaGestion;
    private DefaultListModel<String> modeloLista;
    // Guardamos referencias para procesar acciones
    private java.util.List<ContraOferta> contraOfertasVisibles;

    public PanelMisVentas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);
        contraOfertasVisibles = new java.util.ArrayList<>();

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Mis Ventas");
        lblTitulo.setFont(Estilos.FUENTE_TITULO);
        lblTitulo.setForeground(Estilos.COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Lista
        modeloLista = new DefaultListModel<>();
        listaGestion = new JList<>(modeloLista);
        listaGestion.setFont(Estilos.FUENTE_NORMAL);
        add(new JScrollPane(listaGestion), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Estilos.COLOR_FONDO);

        JButton btnActualizar = new JButton("Actualizar");
        Estilos.estilizarBoton(btnActualizar, Estilos.COLOR_PRIMARIO);

        JButton btnAceptar = new JButton("Aceptar Oferta");
        Estilos.estilizarBoton(btnAceptar, Estilos.COLOR_EXITO);

        JButton btnRechazar = new JButton("Rechazar Oferta");
        Estilos.estilizarBoton(btnRechazar, Estilos.COLOR_ERROR);

        panelBotones.add(btnActualizar);
        panelBotones.add(btnAceptar);
        panelBotones.add(btnRechazar);
        add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnActualizar.addActionListener(e -> cargarDatos());
        btnAceptar.addActionListener(e -> procesarContraOferta(true));
        btnRechazar.addActionListener(e -> procesarContraOferta(false));

        cargarDatos();
    }

    private void cargarDatos() {
        modeloLista.clear();
        contraOfertasVisibles.clear();
        Cliente yo = (Cliente) ventana.getSistema().getUsuarioActual();

        // Buscamos en todas las ofertas del sistema cuáles son mías
        for (Oferta o : ventana.getSistema().verOfertas()) {
            if (o.getVendedor().equals(yo) && !o.isVendida()) {
                modeloLista.addElement("--- MI VENTA: " + o.getDescripcion() + " ($" + o.getPrecio() + ") ---");
                contraOfertasVisibles.add(null); // Espaciador (no es clickable)

                // Buscamos contraofertas para esta venta
                for (ContraOferta co : o.getContraOfertas()) {
                    if (!co.isAceptada()) {
                        modeloLista.addElement("   [OFERTA RECIBIDA] De: " + co.getComprador() + " - Ofrece: $" + co.getNuevoPrecio());
                        contraOfertasVisibles.add(co); // Guardamos la referencia
                    }
                }
            }
        }
    }

    private void procesarContraOferta(boolean aceptar) {
        int index = listaGestion.getSelectedIndex();
        if (index < 0 || index >= contraOfertasVisibles.size()) return;

        ContraOferta co = contraOfertasVisibles.get(index);
        
        if (co == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una línea de 'OFERTA RECIBIDA', no el título.");
            return;
        }

        try {
            if (aceptar) {
                ventana.getSistema().aceptarContraOferta(co);
                JOptionPane.showMessageDialog(this, "¡Venta realizada con éxito!");
            } else {
                ventana.getSistema().rechazarContraOferta(co);
                JOptionPane.showMessageDialog(this, "Contraoferta rechazada.");
            }
            cargarDatos(); // Recargar
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}