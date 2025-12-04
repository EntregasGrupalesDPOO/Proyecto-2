package interfaz;

import javax.swing.*;
import java.awt.*;

public class Estilos {
    // Paleta de colores "BoletaMaster"
    public static final Color COLOR_FONDO = new Color(245, 245, 250); // Gris muy claro
    public static final Color COLOR_PRIMARIO = new Color(40, 60, 120); // Azul oscuro elegante
    public static final Color COLOR_SECUNDARIO = new Color(255, 140, 0); // Naranja (Acciones)
    public static final Color COLOR_TEXTO = new Color(50, 50, 50);
    public static final Color COLOR_EXITO = new Color(40, 167, 69); // Verde
    public static final Color COLOR_ERROR = new Color(220, 53, 69); // Rojo

    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 12);

    // Método para estilizar botones rápidamente
    public static void estilizarBoton(JButton btn, Color colorFondo) {
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFont(FUENTE_BOTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}