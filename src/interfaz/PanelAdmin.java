package interfaz;

import javax.swing.*;
import java.awt.*;
import logica.Solicitud;

public class PanelAdmin extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Solicitud> listaSolicitudes;
    private DefaultListModel<Solicitud> modeloLista;

    public PanelAdmin(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());

        add(new JLabel("Panel de Administración"), BorderLayout.NORTH);

        // Liste des demandes
        modeloLista = new DefaultListModel<>();
        cargarSolicitudes();
        listaSolicitudes = new JList<>(modeloLista);
        add(new JScrollPane(listaSolicitudes), BorderLayout.CENTER);

        // Boutons
        JPanel panelBotones = new JPanel();
        JButton btnAceptar = new JButton("Aceptar Solicitud");
        JButton btnRechazar = new JButton("Rechazar Solicitud");
        JButton btnGanancias = new JButton("Ver Ganancias Globales");
        JButton btnLogout = new JButton("Cerrar Sesión");

        panelBotones.add(btnAceptar);
        panelBotones.add(btnRechazar);
        panelBotones.add(btnGanancias);
        panelBotones.add(btnLogout);
        add(panelBotones, BorderLayout.SOUTH);

        // Actions
        btnLogout.addActionListener(e -> ventana.cerrarSesion());

        btnAceptar.addActionListener(e -> atenderSolicitud(true));
        btnRechazar.addActionListener(e -> atenderSolicitud(false));

        btnGanancias.addActionListener(e -> {
            try {
                double ganancias = ventana.getSistema().getAdministrador().gananciasGlobales();
                JOptionPane.showMessageDialog(this, "Ganancias Globales: $" + ganancias);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void cargarSolicitudes() {
        modeloLista.clear();
        for (Solicitud s : ventana.getSistema().getAdministrador().getSolicitudes()) {
            if (s.getEstado().equals(Solicitud.ESTADO_PENDIENTE)) {
                modeloLista.addElement(s);
            }
        }
    }

    private void atenderSolicitud(boolean aceptar) {
        Solicitud s = listaSolicitudes.getSelectedValue();
        if (s == null) return;
        try {
            ventana.getSistema().getAdministrador().atenderSolicitud(s, aceptar);
            JOptionPane.showMessageDialog(this, "Solicitud " + (aceptar ? "Aceptada" : "Rechazada"));
            cargarSolicitudes(); // Rafraîchir
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}