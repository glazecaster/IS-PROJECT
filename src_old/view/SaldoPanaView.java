package view;

import java.awt.*;
import javax.swing.*;

public class SaldoPanaView extends JPanel {

    private final JButton btnRegresar = new JButton("Regresar");

    public interface RegresarListener {
        void onRegresar();
    }

    private RegresarListener regresarListener;

    public SaldoPanaView() {
        setLayout(new BorderLayout());
        setBackground(ComeUCVView.BLANCO);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComeUCVView.BLANCO);
        card.setPreferredSize(new Dimension(720, 340));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBackground(ComeUCVView.AZUL_OSCURO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.addActionListener(e -> {
            if (regresarListener != null) {
                regresarListener.onRegresar();
            }
        });
        top.add(btnRegresar);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        JLabel lblCedula = new JLabel("Cédula del estudiante a recargar");
        lblCedula.setForeground(ComeUCVView.TEXTO_OSCURO);
        JTextField txtCedula = new JTextField(20);
        txtCedula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lblMonto = new JLabel("Monto a recargar:");
        lblMonto.setForeground(ComeUCVView.TEXTO_OSCURO);
        JTextField txtMonto = new JTextField(20);
        txtMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JButton btnRecargar = new JButton("Recargar");
        btnRecargar.setFocusPainted(false);
        btnRecargar.setBackground(ComeUCVView.AZUL_OSCURO);
        btnRecargar.setForeground(Color.WHITE);

        body.add(lblCedula);
        body.add(Box.createVerticalStrut(6));
        body.add(txtCedula);
        body.add(Box.createVerticalStrut(18));
        body.add(lblMonto);
        body.add(Box.createVerticalStrut(6));
        body.add(txtMonto);
        body.add(Box.createVerticalStrut(24));
        body.add(btnRecargar);

        card.add(top, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    public void setOnRegresar(RegresarListener l) {
        this.regresarListener = l;
    }
}