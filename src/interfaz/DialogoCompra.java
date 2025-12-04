package interfaz;

import javax.swing.*;
import java.awt.*;
import logica.BoletasMaster;
import logica.Evento;

public class DialogoCompra extends JDialog {
    private BoletasMaster sistema;
    private JComboBox<Evento> comboEventos;
    private JTextField txtLocalidad;
    private JSpinner spinnerCantidad;
    private JButton btnComprar;

    public DialogoCompra(Window owner, BoletasMaster sistema) {
        super(owner, "Comprar Tiquetes", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Seleccione Evento:"));
        comboEventos = new JComboBox<>();
        
        // Para cada Evento e en sistema.getEventos()
        for (Evento e : sistema.getEventos()) {
            comboEventos.addItem(e);
        }
        
        // Mostrar el nombre del evento correctamente
        comboEventos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Si el valor es una instancia de Evento
                if (value instanceof Evento) {
                    // Establecer el texto con el nombre del evento
                    setText(((Evento) value).getNombre());
                }
                return this;
            }
        });
        add(comboEventos);

        add(new JLabel("Nombre Localidad (ej: General):"));
        txtLocalidad = new JTextField();
        add(txtLocalidad);

        add(new JLabel("Cantidad:"));
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        add(spinnerCantidad);

        add(new JLabel("")); // Espaciador
        btnComprar = new JButton("Comprar");
        add(btnComprar);

        btnComprar.addActionListener(e -> comprar());
    }

    private void comprar() {
        Evento evento = (Evento) comboEventos.getSelectedItem();
        String localidad = txtLocalidad.getText();
        int cantidad = (int) spinnerCantidad.getValue();

        // Si el evento es nulo o la localidad está vacía
        if (evento == null || localidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los datos");
            return;
        }

        try {
            // Comprar tiquetes en el sistema
            sistema.comprarTiquetes(cantidad, evento, localidad);
            JOptionPane.showMessageDialog(this, "Compra exitosa!");
            dispose(); // Cerrar la ventana
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al comprar: " + ex.getMessage());
        }
    }
}