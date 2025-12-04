package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import logica.BoletasMaster;
import logica.Cliente;
import logica.Evento;
import logica.Localidad;
import logica.Tiquete;

public class PanelCliente extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Tiquete> listaTiquetes;
    private DefaultListModel<Tiquete> modeloLista;
    private JLabel lblSaldo; // Etiqueta para mostrar el saldo

    public PanelCliente(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Estilos.COLOR_FONDO);

        // 1. PESTAÑAS
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Estilos.FUENTE_NORMAL);

        JPanel panelMisTiquetes = crearPanelMisTiquetes();
        tabs.addTab("Mis Tiquetes", new ImageIcon(), panelMisTiquetes, "Ver y usar mis tiquetes");
        tabs.addTab("Marketplace", new ImageIcon(), new PanelMarketPlace(ventana), "Comprar tiquetes de otros");
        tabs.addTab("Mis Ventas", new ImageIcon(), new PanelMisVentas(ventana), "Gestionar mis reventas");

        // Listener para actualizar el saldo cuando cambias de pestaña (por si vendiste algo)
        tabs.addChangeListener(e -> actualizarSaldoVisual());

        add(tabs, BorderLayout.CENTER);

        // 2. FOOTER (Botón Salir)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Estilos.COLOR_FONDO);
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        Estilos.estilizarBoton(btnLogout, Estilos.COLOR_ERROR);
        
        footer.add(btnLogout);
        add(footer, BorderLayout.SOUTH);

        btnLogout.addActionListener(e -> ventana.cerrarSesion());
    }

    private JPanel crearPanelMisTiquetes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Estilos.COLOR_FONDO);

        // --- ENCABEZADO CON SALDO ---
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(Estilos.COLOR_FONDO);
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Mis Tiquetes:");
        lblTitulo.setFont(Estilos.FUENTE_TITULO);
        
        // Etiqueta de Saldo (Nuevo)
        lblSaldo = new JLabel("Saldo: $0.0");
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldo.setForeground(new Color(0, 100, 0)); // Verde oscuro
        actualizarSaldoVisual(); // Carga inicial

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(lblSaldo, BorderLayout.EAST);
        panel.add(panelTop, BorderLayout.NORTH);

        // --- LISTA ---
        modeloLista = new DefaultListModel<>();
        cargarTiquetes();
        listaTiquetes = new JList<>(modeloLista);
        listaTiquetes.setFont(Estilos.FUENTE_NORMAL);
        
        listaTiquetes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tiquete) {
                    Tiquete t = (Tiquete) value;
                    // Buscamos el nombre del evento para mostrarlo en la lista también
                    String nombreEvento = buscarNombreEvento(t);
                    
                    String estado = t.isImpreso() ? "<font color='red'>[IMPRESO]</font>" : "<font color='green'>[DISPONIBLE]</font>";
                    setText("<html><b>" + nombreEvento + "</b> (ID: " + t.getId() + ") | " + t.getFecha() + " | " + estado + "</html>");
                    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
                return this;
            }
        });

        panel.add(new JScrollPane(listaTiquetes), BorderLayout.CENTER);

        // --- BOTONES DE ACCIÓN ---
        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 0)); // 4 botones ahora
        panelBotones.setBackground(Estilos.COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnRecargar = new JButton("Recargar Saldo"); // Nuevo Botón
        Estilos.estilizarBoton(btnRecargar, new Color(40, 167, 69)); // Verde

        JButton btnComprarOficial = new JButton("Comprar Oficial");
        Estilos.estilizarBoton(btnComprarOficial, Estilos.COLOR_PRIMARIO);

        JButton btnImprimir = new JButton("Imprimir / QR");
        Estilos.estilizarBoton(btnImprimir, Estilos.COLOR_SECUNDARIO);

        JButton btnVender = new JButton("Vender (Marketplace)");
        Estilos.estilizarBoton(btnVender, new Color(100, 100, 100));

        panelBotones.add(btnRecargar);
        panelBotones.add(btnComprarOficial);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnVender);

        panel.add(panelBotones, BorderLayout.SOUTH);

        // --- ACCIONES ---
        
        // 1. Recargar Saldo
        btnRecargar.addActionListener(e -> {
            String montoStr = JOptionPane.showInputDialog(this, "Ingrese monto a recargar:");
            if (montoStr != null) {
                try {
                    double monto = Double.parseDouble(montoStr);
                    if (monto <= 0) throw new NumberFormatException();
                    
                    Cliente c = (Cliente) ventana.getSistema().getUsuarioActual();
                    c.actualizarSaldoVirtual(monto); // Método existente en Cliente
                    
                    actualizarSaldoVisual();
                    JOptionPane.showMessageDialog(this, "Saldo actualizado correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 2. Comprar
        btnComprarOficial.addActionListener(e -> {
            new DialogoCompra(ventana, ventana.getSistema()).setVisible(true);
            cargarTiquetes();
            actualizarSaldoVisual(); // Actualizar saldo tras la compra
        });

        // 3. Imprimir
        btnImprimir.addActionListener(e -> {
            Tiquete t = listaTiquetes.getSelectedValue();
            if (t == null) { JOptionPane.showMessageDialog(this, "Seleccione un tiquete."); return; }
            imprimirTiquete(t);
        });

        // 4. Vender
        btnVender.addActionListener(e -> {
            Tiquete t = listaTiquetes.getSelectedValue();
            if (t == null) { JOptionPane.showMessageDialog(this, "Seleccione un tiquete para vender."); return; }
            publicarEnMarketplace(t);
        });

        return panel;
    }

    // Método auxiliar para actualizar la etiqueta de saldo
    private void actualizarSaldoVisual() {
        if (lblSaldo != null) {
            Cliente c = (Cliente) ventana.getSistema().getUsuarioActual();
            lblSaldo.setText("Saldo: $" + c.getSaldoVirtual());
        }
    }

    // Método para buscar el nombre del evento (ya que Tiquete no lo tiene directamente)
    private String buscarNombreEvento(Tiquete tiqueteBuscado) {
        BoletasMaster sistema = ventana.getSistema();
        for (Evento e : sistema.getEventos()) {
            for (Localidad l : e.getLocalidades()) {
                for (Tiquete t : l.getTiquetes()) {
                    if (t.getId() == tiqueteBuscado.getId()) {
                        return e.getNombre();
                    }
                }
            }
        }
        return "Evento Desconocido";
    }

    private void cargarTiquetes() {
        modeloLista.clear();
        Cliente cliente = (Cliente) ventana.getSistema().getUsuarioActual();
        if (cliente.getTiquetes() != null) {
            for (Tiquete t : cliente.getTiquetes().values()) {
                modeloLista.addElement(t);
            }
        }
    }

    private void imprimirTiquete(Tiquete t) {
        if (t.isImpreso()) {
            JOptionPane.showMessageDialog(this, "Este tiquete YA fue impreso.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        t.marcarComoImpreso();
        
        // Buscamos el nombre para pasarlo al panel de impresión
        String nombreEvento = buscarNombreEvento(t);

        JDialog dialogo = new JDialog(ventana, "Tiquete Digital", true);
        dialogo.setSize(650, 300);
        dialogo.setLocationRelativeTo(this);
        
        // Pasamos el nombre del evento al panel
        dialogo.add(new PanelImpresion(t)); 
        
        dialogo.setVisible(true);
        listaTiquetes.repaint();
    }

    private void publicarEnMarketplace(Tiquete t) {
        if (t.isImpreso()) {
            JOptionPane.showMessageDialog(this, "No puede vender un tiquete impreso.", "Prohibido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String precioStr = JOptionPane.showInputDialog(this, "Precio de venta:");
        if (precioStr != null) {
            try {
                double precio = Double.parseDouble(precioStr);
                ventana.getSistema().publicarOferta(t, "Reventa", precio);
                JOptionPane.showMessageDialog(this, "¡Publicado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}