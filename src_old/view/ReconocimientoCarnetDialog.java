package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class ReconocimientoCarnetDialog extends JDialog {

    private File selectedFile;

    private final JLabel lblPreview = new JLabel();
    private final JLabel lblFileName = new JLabel("(sin foto seleccionada)");
    private final JButton btnAdjuntar = new JButton("Simular toma de foto (adjuntar JPG/PNG)");
    private final JButton btnContinuar;
    private final JButton btnCancelar = new JButton("Cancelar");

    public ReconocimientoCarnetDialog(Window owner, String cedula, boolean modoRegistro) {
        super(owner, "Reconocimiento facial", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 470);
        setLocationRelativeTo(owner);

        btnContinuar = new JButton(modoRegistro ? "Registrar" : "Validar");
        btnContinuar.setEnabled(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel(modoRegistro ? "Registro de foto para reconocimiento" : "Verificación de identidad", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(ComeUCVView.AZUL_OSCURO);

        JLabel subtitle = new JLabel("Simulación de captura: adjunta una imagen JPG/PNG.");
        subtitle.setForeground(new Color(0x173B66));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12.5f));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = buildCarnetCard(cedula);
        center.add(card);

        root.add(center, BorderLayout.CENTER);

        stylePrimary(btnContinuar);
        styleSecondary(btnCancelar);
        styleSecondary(btnAdjuntar);

        btnAdjuntar.addActionListener(e -> seleccionarArchivo());
        btnCancelar.addActionListener(e -> {
            selectedFile = null;
            dispose();
        });
        btnContinuar.addActionListener(e -> {
            dispose();
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnAdjuntar);
        actions.add(btnCancelar);
        actions.add(btnContinuar);

        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildCarnetCard(String cedula) {
        JPanel carnet = new JPanel(new BorderLayout(12, 0));
        carnet.setBackground(new Color(0xF7FBFF));
        carnet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 55), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        carnet.setPreferredSize(new Dimension(620, 280));

        JPanel photoBox = new JPanel(new BorderLayout());
        photoBox.setBackground(Color.WHITE);
        photoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 25), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        photoBox.setPreferredSize(new Dimension(200, 240));

        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblPreview.setText("\uD83D\uDCF7\nSin foto");
        lblPreview.setFont(lblPreview.getFont().deriveFont(Font.PLAIN, 16f));
        lblPreview.setForeground(new Color(0x173B66));

        photoBox.add(lblPreview, BorderLayout.CENTER);
        carnet.add(photoBox, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel org = new JLabel("COMEUCV • COMEDOR UNIVERSITARIO");
        org.setFont(org.getFont().deriveFont(Font.BOLD, 14f));
        org.setForeground(ComeUCVView.AZUL_OSCURO);

        JLabel type = new JLabel("CARNET DE ACCESO (SIMULACIÓN)");
        type.setFont(type.getFont().deriveFont(Font.BOLD, 12.5f));
        type.setForeground(new Color(0x173B66));

        JLabel lblCed = new JLabel("Cédula: " + (cedula == null ? "" : cedula));
        lblCed.setFont(lblCed.getFont().deriveFont(Font.BOLD, 13f));
        lblCed.setForeground(ComeUCVView.TEXTO_OSCURO);

        JLabel hint = new JLabel("\u2022 Adjunta una foto (JPG/PNG) para validar identidad.");
        hint.setForeground(new Color(0x173B66));
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12.5f));

        lblFileName.setForeground(new Color(0x173B66));
        lblFileName.setFont(lblFileName.getFont().deriveFont(Font.PLAIN, 12f));

        info.add(org);
        info.add(Box.createVerticalStrut(6));
        info.add(type);
        info.add(Box.createVerticalStrut(14));
        info.add(lblCed);
        info.add(Box.createVerticalStrut(10));
        info.add(hint);
        info.add(Box.createVerticalStrut(10));
        info.add(new JSeparator());
        info.add(Box.createVerticalStrut(10));
        info.add(new JLabel("Archivo seleccionado:"));
        info.add(lblFileName);
        info.add(Box.createVerticalGlue());

        carnet.add(info, BorderLayout.CENTER);

        return carnet;
    }

    private void seleccionarArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
        int opt = chooser.showOpenDialog(this);
        if (opt != JFileChooser.APPROVE_OPTION) return;

        File f = chooser.getSelectedFile();
        if (f == null) return;

        String name = f.getName().toLowerCase();
        if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))) {
            JOptionPane.showMessageDialog(this, "Formato no válido. Selecciona una imagen JPG o PNG.");
            return;
        }

        selectedFile = f;
        lblFileName.setText(f.getName());
        actualizarPreview(f);
        btnContinuar.setEnabled(true);
    }

    private void actualizarPreview(File f) {
        try {
            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(160, 210, Image.SCALE_SMOOTH);
            lblPreview.setIcon(new ImageIcon(scaled));
            lblPreview.setText("");
        } catch (Exception e) {
            lblPreview.setIcon(null);
            lblPreview.setText("(no se pudo cargar)");
        }
    }

    private void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(ComeUCVView.AZUL_OSCURO);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
    }

    private void styleSecondary(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setForeground(ComeUCVView.TEXTO_OSCURO);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 55), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
    }

    public File showAndGetFile() {
        setVisible(true);
        return selectedFile;
    }
}
