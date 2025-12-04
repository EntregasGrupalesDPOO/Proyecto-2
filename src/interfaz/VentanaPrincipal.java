package interfaz;

import javax.swing.*;
import java.awt.*;
import logica.BoletasMaster;

public class VentanaPrincipal extends JFrame {
    
    private BoletasMaster sistema;
    private JPanel panelContenedor;
    private CardLayout cardLayout;

    public VentanaPrincipal(BoletasMaster sistema) {
        this.sistema = sistema;
        
        setTitle("BoletaMaster - Proyecto 3");
        setSize(900, 600);
        setLocationRelativeTo(null);


        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                guardarDatos();
                System.exit(0);
            }
        });
        // ------------------------

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.add(new PanelLogin(this), "LOGIN");
        
        add(panelContenedor);
        setVisible(true);
    }

    // Ajoutez cette méthode à la fin de la classe
    private void guardarDatos() {
        try {
            System.out.println("Guardando datos...");
            sistema.escribirAdministrador();
            sistema.escribirClientes();
            sistema.escribirOrganizadores();
            sistema.escribirEventos();
            sistema.escribirTiquetes();
            sistema.escribirVenues();
            sistema.escribirMarketplace();
            System.out.println("Datos guardados en disco.");
        } catch (Exception e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public BoletasMaster getSistema() {
        return sistema;
    }
    
    // Méthode appelée après une connexion réussie
    public void mostrarMenuCliente() {
        // On recrée le panel pour qu'il se mette à jour avec le client connecté
        panelContenedor.add(new PanelCliente(this), "CLIENTE");
        cardLayout.show(panelContenedor, "CLIENTE");
        setTitle("BoletaMaster - Menú Cliente: " + sistema.getUsuarioActual().getLogin());
    }

    public void cerrarSesion() {
        cardLayout.show(panelContenedor, "LOGIN");
        setTitle("BoletaMaster - Login");
    }

    public static void main(String[] args) {
        // Initialisation du système
        BoletasMaster sistema = new BoletasMaster();
        
        // Charger les données existantes (si tu as déjà exécuté la console)
        sistema.leerAdministrador();
        sistema.leerClientes();
        sistema.leerOrganizadores();
        sistema.leerEventos();
        sistema.leerTiquetes();
        sistema.leerVenues();

        // Lancer l'interface
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema));
    }
    
 // ... code existant ...

    public void mostrarPanelAdmin() {
        panelContenedor.add(new PanelAdmin(this), "ADMIN");
        cardLayout.show(panelContenedor, "ADMIN");
        setTitle("BoletaMaster - Administrador");
    }

    public void mostrarPanelOrganizador() {
        panelContenedor.add(new PanelOrganizador(this), "ORGANIZADOR");
        cardLayout.show(panelContenedor, "ORGANIZADOR");
        setTitle("BoletaMaster - Organizador: " + sistema.getUsuarioActual().getLogin());
    }
    
    // Remplacez la méthode montrerMenuCliente pour être cohérent
    public void mostrarPanelCliente() {
        panelContenedor.add(new PanelCliente(this), "CLIENTE");
        cardLayout.show(panelContenedor, "CLIENTE");
        setTitle("BoletaMaster - Cliente: " + sistema.getUsuarioActual().getLogin());
    }

    // ... reste du code ...
}