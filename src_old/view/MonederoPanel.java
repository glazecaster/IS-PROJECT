package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

public class MonederoPanel extends JPanel {

    private final JLabel lblUsuario = new JLabel("Usuario: v12345678");
    private final JLabel lblSaldo = new JLabel("$9.50");
    private final JTextField txtCodigoOperacion = new JTextField(14);
    private final JButton btnRecargar = new JButton("Canjear recarga");
    private final JButton btnSaldoPana = new JButton("Saldo Pana");

    public interface RecargaListener {
        boolean onRecargar(String codigoOperacion);
    }

    public interface SaldoPanaListener {
        void onMostrarSaldoPana();
    }

    private RecargaListener recargaListener;
    private SaldoPanaListener saldoPanaListener;

    public MonederoPanel() {
        setLayout(new GridBagLayout());
        setBackground(ComeUCVView.BLANCO);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintDecor((Graphics2D) g);
            }
        };
        card.setLayout(new BorderLayout(0, 14));
        card.setBackground(new Color(0xF7FBFF));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(720, 340));

        JLabel titulo = new JLabel("Mi monedero");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        titulo.setForeground(ComeUCVView.AZUL_OSCURO);

        lblUsuario.setFont(lblUsuario.getFont().deriveFont(java.awt.Font.BOLD, 13.5f));
        lblUsuario.setForeground(ComeUCVView.TEXTO_OSCURO);

        JLabel subt = new JLabel("Monto actual");
        subt.setFont(subt.getFont().deriveFont(java.awt.Font.PLAIN, 12.5f));
        subt.setForeground(new Color(0x173B66));

        lblSaldo.setFont(lblSaldo.getFont().deriveFont(java.awt.Font.BOLD, 42f));
        lblSaldo.setForeground(ComeUCVView.AZUL_OSCURO);

        JPanel ayuda = new JPanel(new BorderLayout());
        ayuda.setOpaque(false);
        JLabel lblAyuda = new JLabel("Ingresa tu código de operación para canjear la recarga.", SwingConstants.LEFT);
        lblAyuda.setForeground(ComeUCVView.TEXTO_OSCURO);
        ayuda.add(lblAyuda, BorderLayout.WEST);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(lblUsuario);
        body.add(Box.createVerticalStrut(18));
        body.add(subt);
        body.add(Box.createVerticalStrut(6));
        body.add(lblSaldo);
        body.add(Box.createVerticalStrut(18));
        body.add(ayuda);
        body.add(Box.createVerticalStrut(12));
        body.add(buildRecargaRow());
        body.add(Box.createVerticalStrut(18));

        card.add(titulo, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        add(card);
    }

    private JPanel buildRecargaRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JLabel lbl = new JLabel("Código de operación:");
        lbl.setForeground(ComeUCVView.TEXTO_OSCURO);

        txtCodigoOperacion.setMaximumSize(new Dimension(180, 28));

        btnRecargar.setFocusPainted(false);
        btnRecargar.setBackground(ComeUCVView.AZUL_OSCURO);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.addActionListener(e -> canjearRecarga());

        btnSaldoPana.setFocusPainted(false);
        btnSaldoPana.setBackground(ComeUCVView.AZUL_OSCURO);
        btnSaldoPana.setForeground(Color.WHITE);
        btnSaldoPana.addActionListener(e -> {
            if (saldoPanaListener != null) {
                saldoPanaListener.onMostrarSaldoPana();
            }
        });

        row.add(lbl);
        row.add(txtCodigoOperacion);
        row.add(btnRecargar);
        row.add(btnSaldoPana);
        return row;
    }

    private void canjearRecarga() {
        String codigo = txtCodigoOperacion.getText() == null ? "" : txtCodigoOperacion.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar el código de operación.");
            return;
        }
        if (!codigo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El código de operación debe contener solo números.");
            return;
        }
        if (codigo.length() != 10) {
            JOptionPane.showMessageDialog(this, "El código de operación debe tener exactamente 10 dígitos.");
            return;
        }
        if (recargaListener == null) {
            JOptionPane.showMessageDialog(this, "No se pudo procesar el canje de la recarga.");
            return;
        }
        boolean ok = recargaListener.onRecargar(codigo);
        if (ok) {
            txtCodigoOperacion.setText("");
        }
    }

    private void paintDecor(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.setColor(new Color(191, 227, 255, 50));
        g2.fillOval(w - 220, -50, 260, 260);
        g2.setColor(new Color(11, 45, 91, 25));
        g2.fillOval(w - 340, 80, 220, 220);
        g2.setColor(new Color(11, 45, 91, 18));
        g2.setStroke(new BasicStroke(2f));
        g2.drawArc(-40, h - 140, 260, 180, 0, 180);
        g2.drawArc(40, h - 120, 260, 180, 0, 180);
    }

    public void setUsuario(String usuario) {
        lblUsuario.setText("Usuario: " + usuario);
    }

    public void setSaldo(String saldo) {
        lblSaldo.setText("$" + saldo);
    }

    public void setOnRecargar(RecargaListener l) {
        this.recargaListener = l;
    }

    public void setOnSaldoPana(SaldoPanaListener l) {
        this.saldoPanaListener = l;
    }
}
