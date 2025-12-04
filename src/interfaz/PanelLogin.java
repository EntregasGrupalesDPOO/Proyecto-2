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

        // Titre
        JLabel lblTitulo = new JLabel("Bienvenido a BoletaMaster");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 20, 5);
        add(lblTitulo, gbc);

        // Reset insets
        gbc.insets = new Insets(5, 5, 5, 5);

        // Sélection du Rôle
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

        // --- PANNEAU DE BOUTONS ---
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false); // Transparent pour garder le gris du fond

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(50, 150, 250));
        btnLogin.setForeground(Color.WHITE);

        JButton btnRegistro = new JButton("Registrarse");
        btnRegistro.setBackground(new Color(100, 100, 100)); // Gris foncé
        btnRegistro.setForeground(Color.WHITE);

        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistro);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(panelBotones, gbc);

        // --- ACTIONS ---
        btnLogin.addActionListener(e -> intentarLogin());
        
        btnRegistro.addActionListener(e -> {
            // Ouvrir la fenêtre d'inscription
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
            // Feedback visuel simple
            // JOptionPane.showMessageDialog(this, "Bienvenido " + user); 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de acceso: " + ex.getMessage(), "Login Fallido", JOptionPane.ERROR_MESSAGE);
        }
    }
}