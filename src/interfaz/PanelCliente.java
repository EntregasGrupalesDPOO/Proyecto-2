package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import logica.Administrador;
import logica.Cliente;
import logica.Evento;
import logica.Localidad;
import logica.Tiquete;
import logica.TiqueteMultiple;

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
        
        lblSaldo = new JLabel();
        actualizarSaldoVisual();
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldo.setForeground(new Color(0, 100, 0)); // Verde oscuro

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
                    String nombreEvento = buscarNombreEvento(t);
                    String estado = t.isImpreso() ? "<font color='red'>[IMPRESO]</font>"
                                                  : "<font color='green'>[DISPONIBLE]</font>";
                    setText("<html><b>" + nombreEvento + "</b> (ID: " + t.getId() + ") | "
                            + t.getFecha() + " | " + estado + "</html>");
                    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
                return this;
            }
        });

        panel.add(new JScrollPane(listaTiquetes), BorderLayout.CENTER);

        // --- BOTONES DE ACCIÓN ---
        JPanel panelBotones = new JPanel(new GridLayout(1, 5, 10, 0));
        panelBotones.setBackground(Estilos.COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnComprarOficial = new JButton("Comprar Oficial");
        Estilos.estilizarBoton(btnComprarOficial, Estilos.COLOR_PRIMARIO);

        JButton btnImprimir = new JButton("Imprimir / QR");
        Estilos.estilizarBoton(btnImprimir, Estilos.COLOR_SECUNDARIO);

        JButton btnVender = new JButton("Vender (Marketplace)");
        Estilos.estilizarBoton(btnVender, new Color(100, 100, 100));
        
        JButton btnTransferir = new JButton("Transferir");
        Estilos.estilizarBoton(btnTransferir, new Color(180, 120, 0)); 

        // 🔹 NUEVO: Botón solicitar reembolso
        JButton btnReembolso = new JButton("Solicitar reembolso");
        Estilos.estilizarBoton(btnReembolso, new Color(150, 0, 0));

        panelBotones.add(btnComprarOficial);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnVender);
        panelBotones.add(btnTransferir);
        panelBotones.add(btnReembolso);

        panel.add(panelBotones, BorderLayout.SOUTH);

        // --- ACCIONES ---
        btnComprarOficial.addActionListener(e -> {
            new DialogoCompra(ventana, ventana.getSistema()).setVisible(true);
            cargarTiquetes();
            actualizarSaldoVisual();
        });

        btnImprimir.addActionListener(e -> {
            Tiquete t = listaTiquetes.getSelectedValue();
            if (t == null) { 
                JOptionPane.showMessageDialog(this, "Seleccione un tiquete."); 
                return; 
            }
            imprimirTiquete(t);
        });

        btnVender.addActionListener(e -> {
            Tiquete t = listaTiquetes.getSelectedValue();
            if (t == null) { 
                JOptionPane.showMessageDialog(this, "Seleccione un tiquete para vender."); 
                return; 
            }
            publicarEnMarketplace(t);
        });
        
        btnTransferir.addActionListener(e -> {
            Tiquete t = listaTiquetes.getSelectedValue();
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un tiquete para transferir.");
                return;
            }
            abrirDialogoTransferencia(t);
        });

        // 🔹 ACCIÓN NUEVA: Solicitar reembolso por calamidad
        btnReembolso.addActionListener(e -> {
            Tiquete t = listaTiquetes.getSelectedValue();
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un tiquete para solicitar reembolso.");
                return;
            }

            String razon = JOptionPane.showInputDialog(
                    this,
                    "Explique la razón de la calamidad:",
                    "Solicitud de reembolso",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (razon == null) return; // canceló
            razon = razon.trim();
            if (razon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe escribir una razón para la solicitud.");
                return;
            }

            try {
                ventana.getSistema().solicitarReembolso(t, razon);
                JOptionPane.showMessageDialog(this, 
                        "Solicitud de reembolso enviada al administrador.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al solicitar reembolso: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void actualizarSaldoVisual() {
        if (lblSaldo != null) {
            Cliente c = (Cliente) ventana.getSistema().getUsuarioActual();
            lblSaldo.setText("Saldo: $" + c.getSaldoVirtual());
        }
    }

    private String buscarNombreEvento(Tiquete tiqueteBuscado) {
        for (ArrayList<Evento> lista: Administrador.eventosPorOrganizador.values()) {
            for (Evento e : lista) {
                for (Localidad l : e.getLocalidades()) {
                    for (Tiquete t : l.getTiquetes()) {
                        if (t instanceof TiqueteMultiple) {
                            for (Tiquete ti:((TiqueteMultiple) t).getTiquetes()) {
                                if (ti.getId() == tiqueteBuscado.getId()) {
                                    return e.getNombre();
                                }
                            }
                        } else {
                            if (t.getId() == tiqueteBuscado.getId()) {
                                return e.getNombre();
                            }
                        }
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
                if (t instanceof TiqueteMultiple) {
                    for (Tiquete ti:((TiqueteMultiple) t).getTiquetes()) {
                        modeloLista.addElement(ti);
                    }
                } else {
                    modeloLista.addElement(t);
                }
            }
        }
    }

    private void imprimirTiquete(Tiquete t) {
        if (t.isImpreso()) {
            JOptionPane.showMessageDialog(this, "Este tiquete YA fue impreso.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        t.marcarComoImpreso();

        JDialog dialogo = new JDialog(ventana, "Tiquete Digital", true);
        dialogo.setSize(650, 300);
        dialogo.setLocationRelativeTo(this);
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
    
    private void abrirDialogoTransferencia(Tiquete t) {
        JTextField txtLoginDestino = new JTextField();
        JPasswordField txtPassword = new JPasswordField();

        Object[] message = {
            "Login del destinatario:", txtLoginDestino,
            "Confirma tu contraseña:", txtPassword
        };

        int option = JOptionPane.showConfirmDialog(
                this, message, "Transferir Tiquete", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) return;

        String loginDestino = txtLoginDestino.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (loginDestino.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        try {
            Cliente cliente = (Cliente) ventana.getSistema().getUsuarioActual();
            TiqueteMultiple tm = cliente.buscarTiqueteMultiple(t);
            if (tm == null) {
                cliente.transferirTiquete(t, loginDestino, pass);
            } else {
                cliente.transferirTiquete(tm, t, loginDestino, pass);
            }

            JOptionPane.showMessageDialog(this, 
                    "Tiquete transferido correctamente a " + loginDestino);

            cargarTiquetes();
            listaTiquetes.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error al transferir: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
