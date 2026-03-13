package view;

import model.UsuarioServicio;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class SaldoPanaView extends JPanel {

    private final JButton btnRegresar = new JButton("Regresar");
    private final JTextField txtCedula = new JTextField(20);
    private final JTextField txtMonto = new JTextField(20);
    private final JButton btnRecargar = new JButton("Transferir saldo pana");

    public interface RegresarListener {
        void onRegresar();
    }

    public interface TransferenciaListener {
        boolean onTransferir(String cedulaDestino, double monto, String clave, String confirmarClave);
    }

    private RegresarListener regresarListener;
    private TransferenciaListener transferenciaListener;

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

        JLabel lblCedula = new JLabel("Cédula del estudiante a recargar:");
        lblCedula.setForeground(ComeUCVView.TEXTO_OSCURO);
        txtCedula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblMonto = new JLabel("Monto a transferir:");
        lblMonto.setForeground(ComeUCVView.TEXTO_OSCURO);
        txtMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblAyuda = new JLabel("Se generará un código de operación para que el otro estudiante canjee la recarga.");
        lblAyuda.setForeground(new Color(0x173B66));
        lblAyuda.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRecargar.setFocusPainted(false);
        btnRecargar.setBackground(ComeUCVView.AZUL_OSCURO);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.addActionListener(e -> transferir());

        body.add(lblCedula);
        body.add(Box.createVerticalStrut(6));
        body.add(txtCedula);
        body.add(Box.createVerticalStrut(18));
        body.add(lblMonto);
        body.add(Box.createVerticalStrut(6));
        body.add(txtMonto);
        body.add(Box.createVerticalStrut(12));
        body.add(lblAyuda);
        body.add(Box.createVerticalStrut(24));
        body.add(btnRecargar);

        card.add(top, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    private void transferir() {
        String cedula = txtCedula.getText() == null ? "" : txtCedula.getText().trim();
        String montoRaw = txtMonto.getText() == null ? "" : txtMonto.getText().trim();

        if (cedula.isEmpty() || montoRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes completar la cédula y el monto.");
            return;
        }
        if (!UsuarioServicio.esCedulaValida(cedula)) {
            JOptionPane.showMessageDialog(this, "La cédula debe contener solo números y estar entre 5.000.000 y 35.000.000.");
            return;
        }
        if (!montoRaw.matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(this, "Monto inválido. Solo se permiten números.");
            return;
        }

        double monto = Double.parseDouble(montoRaw);
        if (monto <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor que cero.");
            return;
        }
        if (monto > 100) {
            JOptionPane.showMessageDialog(this, "El monto máximo para saldo pana es 100 dólares.");
            return;
        }
        if (transferenciaListener == null) {
            JOptionPane.showMessageDialog(this, "No se pudo procesar la transferencia.");
            return;
        }

        JPasswordField txtClave = new JPasswordField(16);
        JPasswordField txtConfirmar = new JPasswordField(16);
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtClave);
        panel.add(new JLabel("Confirmar contraseña:"));
        panel.add(txtConfirmar);

        int option = JOptionPane.showConfirmDialog(this, panel, "Confirmar transferencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        boolean ok = transferenciaListener.onTransferir(cedula, monto, new String(txtClave.getPassword()), new String(txtConfirmar.getPassword()));
        if (ok) {
            txtCedula.setText("");
            txtMonto.setText("");
        }
    }

    public void setOnRegresar(RegresarListener l) {
        this.regresarListener = l;
    }

    public void setOnTransferir(TransferenciaListener l) {
        this.transferenciaListener = l;
    }
}
