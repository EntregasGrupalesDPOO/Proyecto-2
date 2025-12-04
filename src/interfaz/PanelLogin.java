package interfaz;

import javax.swing.*;
import java.awt.*;

public class PanelLogin extends JPanel {
    private VentanaPrincipal ventana;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> comboRol;

    public PanelLogin(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Bienvenido a BoletaMaster");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 20, 5);
        add(lblTitulo, gbc);

        // Reiniciar insets
        gbc.insets = new Insets(5, 5, 5, 5);

        // Selección del Rol
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Rol:"), gbc);
        comboRol = new JComboBox<>(new String[]{"Cliente", "Organizador", "Administrador"});
        gbc.gridx = 1;
        add(comboRol, gbc);

        // User
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Usuario:"), gbc);
        txtUser = new JTextField(15);
        gbc.gridx = 1;
        add(txtUser, gbc);

        // Pass
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Contraseña:"), gbc);
        txtPass = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtPass, gbc);

        // --- PANEL DE BOTONES ---
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false); // Transparente para mantener el color de fondo

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(50, 150, 250));
        btnLogin.setForeground(Color.WHITE);

        JButton btnRegistro = new JButton("Registrarse");
        btnRegistro.setBackground(new Color(100, 100, 100)); // Gris oscuro
        btnRegistro.setForeground(Color.WHITE);

        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistro);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(panelBotones, gbc);


        btnLogin.addActionListener(e -> intentarLogin());
        
        btnRegistro.addActionListener(e -> {
            // Abrir la ventana de registro
            DialogoRegistro dialogo = new DialogoRegistro(ventana, ventana.getSistema());
            dialogo.setVisible(true);
        });
    }

    private void intentarLogin() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        String rol = (String) comboRol.getSelectedItem();

        try {
            switch (rol) {
                case "Cliente":
                    ventana.getSistema().loginCliente(user, pass);
                    ventana.mostrarPanelCliente();
                    break;
                case "Organizador":
                    ventana.getSistema().loginOrganizador(user, pass);
                    ventana.mostrarPanelOrganizador();
                    break;
                case "Administrador":
                    ventana.getSistema().loginAdministrador(user, pass);
                    ventana.mostrarPanelAdmin();
                    break;
            }
            // Retroalimentación visual simple
            // JOptionPane.showMessageDialog(this, "Bienvenido " + user); 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de acceso: " + ex.getMessage(), "Login Fallido", JOptionPane.ERROR_MESSAGE);
        }
    }
}