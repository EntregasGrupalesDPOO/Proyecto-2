package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import Marketplace.ContraOferta;
import Marketplace.Oferta;
import logica.*; 

public class PanelMisVentas extends JPanel {
    private VentanaPrincipal ventana;
    private JList<String> listaGestion;
    private DefaultListModel<String> modeloLista;
    private java.util.List<ContraOferta> contraOfertasVisibles;

    public PanelMisVentas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);
        contraOfertasVisibles = new ArrayList<>();

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Mis Ventas Activas");
        lblTitulo.setFont(Estilos.FUENTE_TITULO);
        lblTitulo.setForeground(Estilos.COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Lista
        modeloLista = new DefaultListModel<>();
        listaGestion = new JList<>(modeloLista);
        listaGestion.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced pour mieux aligner
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


    private String buscarInfoEvento(Tiquete tiqueteBuscado) {
        for (ArrayList<Evento> lista: Administrador.eventosPorOrganizador.values()) {
            for (Evento e : lista) {
            	for (Localidad l : e.getLocalidades()) {
                    for (Tiquete t : l.getTiquetes()) {
                        if (t.getId() == tiqueteBuscado.getId()) {
                            return e.getNombre() + " (" + l.getNombre() + ")";
                        }
                    }
                }
            }
        }
        return "Evento Desconocido";
    }

    private void cargarDatos() {
        modeloLista.clear();
        contraOfertasVisibles.clear();
        Cliente yo = (Cliente) ventana.getSistema().getUsuarioActual();

        for (Oferta o : ventana.getSistema().verOfertas()) {
            if (o.getVendedor().equals(yo) && !o.isVendida()) {
                
                Tiquete t = o.getTiquete();
                String infoEvento = buscarInfoEvento(t); 
                int idTiquete = t.getId();

                String lineaVenta = String.format("VENTA: %s [ID Tiquete: %d] - Precio: $%.2f", 
                                                  infoEvento, idTiquete, o.getPrecio());
                
                modeloLista.addElement(lineaVenta);
                contraOfertasVisibles.add(null); 

                for (ContraOferta co : o.getContraOfertas()) {
                    if (!co.isAceptada()) {
                        String lineaOferta = String.format("   └── [OFERTA] De: %s - Ofrece: $%.2f", 
                                                           co.getComprador(), co.getNuevoPrecio());
                        modeloLista.addElement(lineaOferta);
                        contraOfertasVisibles.add(co); 
                    }
                }

                modeloLista.addElement(" ");
                contraOfertasVisibles.add(null);
            }
        }
        
        if (modeloLista.isEmpty()) {
            modeloLista.addElement("No tienes ventas activas.");
        }
    }

    private void procesarContraOferta(boolean aceptar) {
        int index = listaGestion.getSelectedIndex();
        if (index < 0 || index >= contraOfertasVisibles.size()) return;

        ContraOferta co = contraOfertasVisibles.get(index);
        
        if (co == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una línea que comience con '└── [OFERTA]'.\nNo se puede seleccionar el título de la venta.", "Selección Incorrecta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (aceptar) {
                ventana.getSistema().aceptarContraOferta(co);
                JOptionPane.showMessageDialog(this, "¡Oferta aceptada! El tiquete ha sido transferido y el dinero recibido.");
            } else {
                ventana.getSistema().rechazarContraOferta(co);
                JOptionPane.showMessageDialog(this, "Contraoferta rechazada.");
            }
            cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}