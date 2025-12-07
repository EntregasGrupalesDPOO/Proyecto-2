package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogoSeleccionAsientos extends JDialog {

    private JList<String> listaAsientos;
    private JButton btnAceptar;
    private JButton btnCancelar;

    private List<String> seleccionFinal;
    private final int cantidadRequerida;

    public DialogoSeleccionAsientos(Window owner,
                                    List<String> asientosDisponibles,
                                    int cantidadRequerida) {
        super(owner, "Seleccionar asientos", ModalityType.APPLICATION_MODAL);
        this.cantidadRequerida = cantidadRequerida;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lbl = new JLabel("Seleccione exactamente " + cantidadRequerida + " asientos:");
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lbl, BorderLayout.NORTH);

        listaAsientos = new JList<>(asientosDisponibles.toArray(new String[0]));
        listaAsientos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(listaAsientos), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancelar = new JButton("Cancelar");
        btnAceptar = new JButton("Aceptar");

        btnCancelar.addActionListener(e -> {
            seleccionFinal = null;
            dispose();
        });

        btnAceptar.addActionListener(e -> {
            List<String> seleccion = listaAsientos.getSelectedValuesList();
            if (seleccion.size() != cantidadRequerida) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar exactamente " + cantidadRequerida + " asientos.",
                        "Selección inválida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            seleccionFinal = new ArrayList<>(seleccion);
            dispose();
        });

        sur.add(btnCancelar);
        sur.add(btnAceptar);
        add(sur, BorderLayout.SOUTH);
    }

    public List<String> getSeleccionFinal() {
        return seleccionFinal;
    }
}