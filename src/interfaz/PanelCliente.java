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

        // 1. Encabezado
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Mis Tiquetes Comprados:"));
        add(panelTop, BorderLayout.NORTH);

        // 2. Lista de los billetes
        modeloLista = new DefaultListModel<>();
        cargarTiquetes(); // Cargar los datos iniciales
        listaTiquetes = new JList<>(modeloLista);
        
        // Visualización personalizada en la lista (ID + Evento + Estado)
        listaTiquetes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tiquete) {
                    Tiquete t = (Tiquete) value;
                    // Intentamos mostrar la fecha, el ID y si está impreso
                    String info = "ID: " + t.getId() + " - Fecha: " + t.getFecha();
                    if (t.isImpreso()) {
                        info += " [YA IMPRESO - BLOQUEADO]";
                        setForeground(Color.GRAY); // Poner en gris los billetes impresos
                    } else {
                        setForeground(Color.BLACK);
                    }
                    setText(info);
                }
                return this;
            }
        });

        add(new JScrollPane(listaTiquetes), BorderLayout.CENTER);

        // 3. Botones (Es aquí donde tenías el error)
        JPanel panelBotones = new JPanel();
        
        // --- DECLARACIÓN DE LOS BOTONES ---
        JButton btnComprar = new JButton("Comprar Nuevo Tiquete"); // El botón faltante
        JButton btnImprimir = new JButton("Imprimir Tiquete Seleccionado");
        JButton btnLogout = new JButton("Cerrar Sesión");

        // --- ADICIÓN AL PANEL ---
        panelBotones.add(btnComprar); // Ahora funciona porque btnComprar existe justo arriba
        panelBotones.add(btnImprimir);
        panelBotones.add(btnLogout);
        
        add(panelBotones, BorderLayout.SOUTH);

        // 4. Acciones (Listeners)
        
        // Acción Desconexión
        btnLogout.addActionListener(e -> ventana.cerrarSesion());
        
        // Acción Comprar (Abre el diálogo de compra)
        btnComprar.addActionListener(e -> {
            // Nota: DialogoCompra debe estar definido en el package 'interfaz'
            // Nota: PanelImpresion debe estar definido en el package 'interfaz'
            // Las clases PanelImpresion y DialogoCompra deben estar disponibles en el package 'interfaz'
            // La clase PanelImpresion debe estar disponible en el package 'interfaz'
            DialogoCompra dialogo = new DialogoCompra(ventana, ventana.getSistema());
            dialogo.setVisible(true);
            // Una vez que la compra termina (ventana cerrada), recargamos la lista para ver el nuevo billete
            cargarTiquetes();
        });
        
        // Acción Imprimir
        btnImprimir.addActionListener(e -> {
            Tiquete seleccionado = listaTiquetes.getSelectedValue();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un tiquete de la lista.");
                return;
            }
            imprimirTiquete(seleccionado);
        });
    }

    // Carga los billetes desde el modelo hacia la lista gráfica
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
        // Regla de negocio: Bloquear si ya está impreso
        if (t.isImpreso()) {
            JOptionPane.showMessageDialog(this, "Este tiquete YA fue impreso y no se puede reimprimir ni transferir.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Marcar como impreso en el modelo (Lógica)
        t.marcarComoImpreso();
        
        // Guardar (Simulación de persistencia)
        // ventana.getSistema().escribirTiquetes(); // Descomentar si la persistencia está activa

        // Abrir la ventana de impresión Java2D (El verdadero renderizado visual)
        JDialog dialogoImpresion = new JDialog(ventana, "Visualización de Tiquete", true);
        dialogoImpresion.setSize(650, 300);
        dialogoImpresion.setLocationRelativeTo(this);
        dialogoImpresion.add(new PanelImpresion(t)); // Utiliza la clase PanelImpresion que creamos
        dialogoImpresion.setVisible(true);
        
        // Refrescar la visualización para mostrar que el billete está ahora "BLOQUEADO"
        listaTiquetes.repaint();
    }
}