import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mostrarSplashScreen();
        
        SwingUtilities.invokeLater(() -> {
            vista.AdminVista vistaCostos = new vista.AdminVista();
            new controlador.AdminControlador(vistaCostos);
            
            vistaCostos.setLocationRelativeTo(null);
            vistaCostos.setVisible(true);
        });
    }
    
    private static void mostrarSplashScreen() {
        JWindow splashScreen = new JWindow();
        JPanel content = new JPanel(new BorderLayout());
        
        content.setBackground(new Color(10, 57, 102));
        
        JLabel title = new JLabel("ComeUCV", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        
        JLabel subtitle = new JLabel("Sistema Administrativo de Comedor", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(173, 216, 230));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
        
        JLabel version = new JLabel("Versión 2.0", SwingConstants.CENTER);
        version.setFont(new Font("SansSerif", Font.ITALIC, 12));
        version.setForeground(Color.LIGHT_GRAY);
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        content.add(title, BorderLayout.CENTER);
        content.add(subtitle, BorderLayout.SOUTH);
        content.add(version, BorderLayout.NORTH);
        
        splashScreen.setContentPane(content);
        splashScreen.setSize(500, 300);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        splashScreen.dispose();
    }
}