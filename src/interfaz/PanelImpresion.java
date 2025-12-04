package interfaz;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import logica.Tiquete;

public class PanelImpresion extends JPanel {
    private Tiquete tiquete;

    public PanelImpresion(Tiquete tiquete) {
        this.tiquete = tiquete;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Mejora del renderizado (Anti-aliasing)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fondo del tiquete
        GradientPaint gradiente = new GradientPaint(0, 0, new Color(30, 30, 100), getWidth(), getHeight(), new Color(0, 0, 50));
        g2d.setPaint(gradiente);
        g2d.fillRect(10, 10, getWidth() - 20, getHeight() - 20);

        // 2. Línea punteada de desprendimiento
        g2d.setColor(Color.WHITE);
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(200, 10, 200, getHeight() - 10);

        // 3. Info Izquierda (Stub)
        g2d.setColor(Color.ORANGE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2d.drawString("BOLETAMASTER", 25, 50);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.drawString("ID: " + tiquete.getId(), 25, 90);
        g2d.drawString("$" + tiquete.getPrecioReal(), 25, 120);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString("Impreso el:", 25, 180);
        g2d.drawString(tiquete.getFechaImpresion().toString(), 25, 195);

        // 4. Info Derecha (Evento)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Serif", Font.BOLD, 28));
        g2d.drawString("EVENTO EN VIVO", 220, 60);
        
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g2d.drawString("Fecha: " + tiquete.getFecha(), 220, 110);
        g2d.drawString("Hora: " + "20:00", 220, 140); // Si la hora no es accesible directamente

        // 5. Código QR Simulado
        // Nosotros generamos un patrón basado en el ID del tiquete
        dibujarQR(g2d, 450, 80, 120);
    }

    private void dibujarQR(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, size, size);
        g2d.setColor(Color.BLACK);
        
        int cells = 20;
        int cellSize = size / cells;
        
        // Usar el ID del tiquete como semilla para que el QR sea único para este tiquete
        Random rand = new Random(tiquete.getId());

        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < cells; j++) {
                // Crear los "ojos" del código QR (esquinas)
                boolean isCorner = (i < 5 && j < 5) || (i > 14 && j < 5) || (i < 5 && j > 14);
                
                if (isCorner) {
                    // Dibujar los cuadros de ubicación fijos
                    if (i==0 || i==4 || j==0 || j==4 || (i>1 && i<3 && j>1 && j<3)) { 
                        // Esto es una simplificación visual
                        g2d.fillRect(x + i*cellSize, y + j*cellSize, cellSize, cellSize);
                    }
                } else {
                    // Relleno aleatorio pero determinista (basado en el ID)
                    if (rand.nextBoolean()) {
                        g2d.fillRect(x + i*cellSize, y + j*cellSize, cellSize, cellSize);
                    }
                }
            }
        }
        
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("QR Simulado", x + 30, y + size + 10);
    }
}