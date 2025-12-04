package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import logica.Cliente;
import logica.Tiquete;

public class PanelCliente extends JPanel {
    private VentanaPrincipal ventana;
    private JList<Tiquete> listaTiquetes;
    private DefaultListModel<Tiquete> modeloLista;

    public PanelCliente(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());

        // 1. En-tête
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Mis Tiquetes Comprados:"));
        add(panelTop, BorderLayout.NORTH);

        // 2. Liste des billets
        modeloLista = new DefaultListModel<>();
        cargarTiquetes(); // Charger les données initiales
        listaTiquetes = new JList<>(modeloLista);
        
        // Affichage personnalisé dans la liste (ID + Evento + Statut)
        listaTiquetes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tiquete) {
                    Tiquete t = (Tiquete) value;
                    // On essaie d'afficher la date, l'ID et si c'est imprimé
                    String info = "ID: " + t.getId() + " - Fecha: " + t.getFecha();
                    if (t.isImpreso()) {
                        info += " [YA IMPRESO - BLOQUEADO]";
                        setForeground(Color.GRAY); // Griser les billets imprimés
                    } else {
                        setForeground(Color.BLACK);
                    }
                    setText(info);
                }
                return this;
            }
        });

        add(new JScrollPane(listaTiquetes), BorderLayout.CENTER);

        // 3. Boutons (C'est ici que tu avais l'erreur)
        JPanel panelBotones = new JPanel();
        
        // --- DÉCLARATION DES BOUTONS ---
        JButton btnComprar = new JButton("Comprar Nuevo Tiquete"); // Le bouton manquant
        JButton btnImprimir = new JButton("Imprimir Tiquete Seleccionado");
        JButton btnLogout = new JButton("Cerrar Sesión");

        // --- AJOUT AU PANEL ---
        panelBotones.add(btnComprar); // Maintenant ça marche car btnComprar existe juste au-dessus
        panelBotones.add(btnImprimir);
        panelBotones.add(btnLogout);
        
        add(panelBotones, BorderLayout.SOUTH);

        // 4. Actions (Listeners)
        
        // Action Déconnexion
        btnLogout.addActionListener(e -> ventana.cerrarSesion());
        
        // Action Acheter (Ouvre le dialogue d'achat)
        btnComprar.addActionListener(e -> {
            DialogoCompra dialogo = new DialogoCompra(ventana, ventana.getSistema());
            dialogo.setVisible(true);
            // Une fois l'achat fini (fenêtre fermée), on recharge la liste pour voir le nouveau billet
            cargarTiquetes();
        });
        
        // Action Imprimer
        btnImprimir.addActionListener(e -> {
            Tiquete seleccionado = listaTiquetes.getSelectedValue();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un tiquete de la lista.");
                return;
            }
            imprimirTiquete(seleccionado);
        });
    }

    // Charge les billets depuis le modèle vers la liste graphique
    private void cargarTiquetes() {
        modeloLista.clear();
        Cliente cliente = (Cliente) ventana.getSistema().getUsuarioActual();
        HashMap<Integer, Tiquete> misTiquetes = cliente.getTiquetes();
        
        if (misTiquetes != null) {
            for (Tiquete t : misTiquetes.values()) {
                modeloLista.addElement(t);
            }
        }
    }

    private void imprimirTiquete(Tiquete t) {
        // Règle métier : Bloquer si déjà imprimé
        if (t.isImpreso()) {
            JOptionPane.showMessageDialog(this, "Este tiquete YA fue impreso y no se puede reimprimir ni transferir.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Marquer comme imprimé dans le modèle (Logique)
        t.marcarComoImpreso();
        
        // Sauvegarder (Simulation de persistance)
        // ventana.getSistema().escribirTiquetes(); // Décommenter si la persistance est active

        // Ouvrir la fenêtre d'impression Java2D (Le vrai rendu visuel)
        JDialog dialogoImpresion = new JDialog(ventana, "Visualización de Tiquete", true);
        dialogoImpresion.setSize(650, 300);
        dialogoImpresion.setLocationRelativeTo(this);
        dialogoImpresion.add(new PanelImpresion(t)); // Utilise la classe PanelImpresion qu'on a créée
        dialogoImpresion.setVisible(true);
        
        // Rafraîchir l'affichage pour montrer que le billet est maintenant "BLOQUEADO"
        listaTiquetes.repaint();
    }
}