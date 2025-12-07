package interfaz;

import javax.swing.*;
import java.awt.*;
import logica.BoletasMaster;
import logica.Venue;

public class VentanaPrincipal extends JFrame {
    
    private BoletasMaster sistema;
    private JPanel panelContenedor;
    private CardLayout cardLayout;

    public VentanaPrincipal(BoletasMaster sistema) {
        this.sistema = sistema;
        
        setTitle("BoletaMaster - Proyecto 3");
        setSize(900, 600);
        setLocationRelativeTo(null);

        // En lugar de cerrar abruptamente (EXIT_ON_CLOSE), interceptamos el cierre
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Agregamos un listener que guardará los datos antes de salir
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                guardarDatos(); // Guarda todo
                System.exit(0); // Cierra la aplicación
            }
        });

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.add(new PanelLogin(this), "LOGIN");
        
        add(panelContenedor);
        setVisible(true);
    }

    // Agregue este método al final de la clase
    private void guardarDatos() {
        try {
            System.out.println("Guardando datos...");
            sistema.escribirAdministrador();
            sistema.escribirClientes();
            sistema.escribirOrganizadores();
            sistema.escribirEventos();
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
    
    // Método llamado después de un inicio de sesión exitoso
    public void mostrarMenuCliente() {
        // Recreamos el panel para que se actualice con el cliente conectado
        panelContenedor.add(new PanelCliente(this), "CLIENTE");
        cardLayout.show(panelContenedor, "CLIENTE");
        setTitle("BoletaMaster - Menú Cliente: " + sistema.getUsuarioActual().getLogin());
    }

    public void cerrarSesion() {
        cardLayout.show(panelContenedor, "LOGIN");
        setTitle("BoletaMaster - Login");
    }

    public static void main(String[] args) {
        // 1. Inicialización del sistema
        BoletasMaster sistema = new BoletasMaster();
        
        // 2. Intentar cargar datos existentes
        sistema.leerAdministrador();
        sistema.leerClientes();
        sistema.leerOrganizadores();
        sistema.leerEventos();
        sistema.leerVenues();
        sistema.leerMarketplace(); 

        // Si después de cargar, no hay administrador creamos uno.
        if (sistema.getAdministrador() == null) {
            sistema.agregarAdministrador("admin", "admin");
            
        }
        // -------------------------------------------------------------

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema));
    }
    

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
    
    public void mostrarPanelCliente() {
        panelContenedor.add(new PanelCliente(this), "CLIENTE");
        cardLayout.show(panelContenedor, "CLIENTE");
        setTitle("BoletaMaster - Cliente: " + sistema.getUsuarioActual().getLogin());
    }

}