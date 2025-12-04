package interfaz;

import javax.swing.*;
import java.awt.*;
import logica.BoletasMaster;

public class DialogoRegistro extends JDialog {
    private BoletasMaster sistema;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> comboRol;

    public DialogoRegistro(Window owner, BoletasMaster sistema) {
        super(owner, "Registrar Nuevo Usuario", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        setSize(350, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        JLabel lblTitulo = new JLabel("Crear Cuenta Nueva");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        // Choix du rôle (Uniquement Client ou Organisateur, pas Admin)
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Tipo de Usuario:"), gbc);
        
        comboRol = new JComboBox<>(new String[]{"Cliente", "Organizador"});
        gbc.gridx = 1;
        add(comboRol, gbc);

        // Champs Utilisateur
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Nuevo Usuario:"), gbc);
        
        txtUser = new JTextField(15);
        gbc.gridx = 1;
        add(txtUser, gbc);

        // Champs Mot de passe
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Nueva Contraseña:"), gbc);
        
        txtPass = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtPass, gbc);

        // Bouton de confirmation
        JButton btnRegistrar = new JButton("Crear Cuenta");
        btnRegistrar.setBackground(new Color(40, 180, 100)); // Vert
        btnRegistrar.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(btnRegistrar, gbc);

        // Action du bouton
        btnRegistrar.addActionListener(e -> registrarUsuario());
    }

    private void registrarUsuario() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();
        String rol = (String) comboRol.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérification si l'utilisateur existe déjà (Simplifié)
        if (sistema.getClientes().containsKey(user) || sistema.getOrganizadores().containsKey(user)) {
            JOptionPane.showMessageDialog(this, "El usuario '" + user + "' ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if ("Cliente".equals(rol)) {
                sistema.agregarCliente(user, pass);
                JOptionPane.showMessageDialog(this, "¡Cliente registrado con éxito!");
            } else if ("Organizador".equals(rol)) {
                sistema.agregarOrganizador(user, pass);
                JOptionPane.showMessageDialog(this, "¡Organizador registrado con éxito!");
            }
            dispose(); // Fermer la fenêtre après succès
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}