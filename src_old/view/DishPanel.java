package view;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DishPanel extends JPanel {

    public DishPanel(String nombre, String descripcion, String tipo,
                     String proteinas, String carbs, String calorias, String icono) {

        setLayout(new BorderLayout(10, 0));
        setOpaque(true);
        setBackground(new Color(0xF7FBFF));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 35), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel icon = new JLabel(icono, SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(44, 44));
        icon.setFont(icon.getFont().deriveFont(20f));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblNombre = new JLabel(nombre + "  •  " + tipo);
        lblNombre.setForeground(ComeUCVView.AZUL_OSCURO);
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD, 13.5f));

        JLabel lblDesc = new JLabel(
                "<html><div style='width:260px; color:#0B1B2B;'>" + descripcion + "</div></html>"
        );
        lblDesc.setFont(lblDesc.getFont().deriveFont(12.5f));

        JLabel macros = new JLabel(
                "<html><div style='color:#173B66; font-size:12px;'>" +
                        "<b>Proteínas:</b> " + proteinas + " &nbsp;&nbsp; <b>Carbohidratos:</b> " + carbs +
                        "<br/><b>Calorías:</b> " + calorias +
                        "</div></html>"
        );

        info.add(lblNombre);
        info.add(Box.createVerticalStrut(4));
        info.add(lblDesc);
        info.add(Box.createVerticalStrut(6));
        info.add(macros);

        add(icon, BorderLayout.WEST);
        add(info, BorderLayout.CENTER);
    }
}
