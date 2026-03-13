package faceaccess.view;

import view.ComeUCVView;
import view.FooterPanel;
import view.HeaderPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceAccessView extends JFrame {

    private final HeaderPanel header = new HeaderPanel("COMEUCV FACE ACCESS");
    private final JButton btnEscanear = new JButton("Escanear rostro");
    private final JButton btnAutorizar = new JButton("Comprar");
    private final JButton btnSalir = new JButton("Salir");
    private final JComboBox<String> cbServicio = new JComboBox<>(new String[]{"Desayuno", "Almuerzo"});
    private final JComboBox<String> cbDia = new JComboBox<>(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"});
    private final JComboBox<String> cbMenuDisponible = new JComboBox<>(new String[]{"Sin opciones disponibles"});
    private final JLabel lblFotoEscaneada = new JLabel("Sin imagen", JLabel.CENTER);
    private final JLabel lblFotoRegistrada = new JLabel("Sin coincidencia", JLabel.CENTER);
    private final JLabel lblEstadoEscaneo = new JLabel("Esperando escaneo facial...");
    private final JLabel lblCedula = new JLabel("—");
    private final JLabel lblTipo = new JLabel("—");
    private final JLabel lblSaldo = new JLabel("$0.00");
    private final JLabel lblCCB = new JLabel("$0.00");
    private final JLabel lblTarifa = new JLabel("$0.00");
    private final JTextArea txtDetalle = new JTextArea();
    private Runnable onSalir;

    public FaceAccessView() {
        super("ComeUCVFaceAccess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 760));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        header.setUsuarioText("Por reconocer");
        header.setRolText("CONTROL DE ACCESO FÍSICO");
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(14, 14, 14, 14));
        center.add(buildTopSection(), BorderLayout.NORTH);
        center.add(buildSeleccionCard(), BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(new FooterPanel(), BorderLayout.SOUTH);
        setContentPane(root);

        btnAutorizar.setEnabled(false);
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);

        stylePrimary(btnEscanear);
        stylePrimary(btnAutorizar);
        styleSecondary(btnSalir);

        btnSalir.addActionListener(e -> {
            if (onSalir != null) onSalir.run();
        });
    }

    private JPanel buildTopSection() {
        JPanel top = new JPanel(new GridLayout(1, 2, 14, 14));
        top.setOpaque(false);
        top.add(buildEscaneoCard());
        top.add(buildUsuarioCard());
        return top;
    }

    private JPanel buildEscaneoCard() {
        JPanel card = createCard("Escaneo facial");

        JPanel previews = new JPanel(new GridLayout(1, 2, 12, 0));
        previews.setOpaque(false);
        previews.add(buildImagePanel("Foto escaneada", lblFotoEscaneada));
        previews.add(buildImagePanel("Foto registrada", lblFotoRegistrada));

        lblEstadoEscaneo.setForeground(new Color(0x173B66));
        lblEstadoEscaneo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(btnEscanear);
        buttons.add(btnSalir);

        card.add(previews, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(Box.createVerticalStrut(8));
        south.add(lblEstadoEscaneo);
        south.add(Box.createVerticalStrut(10));
        south.add(buttons);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildUsuarioCard() {
        JPanel card = createCard("Usuario reconocido");
        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setOpaque(false);
        grid.add(labelTitle("Cédula:"));
        grid.add(lblCedula);
        grid.add(labelTitle("Tipo de usuario:"));
        grid.add(lblTipo);
        grid.add(labelTitle("Saldo actual:"));
        grid.add(lblSaldo);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSeleccionCard() {
        JPanel card = createCard("Selección de bandeja y cobro");

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(labelTitle("Servicio:"));
        filtros.add(cbServicio);
        filtros.add(labelTitle("Día:"));
        filtros.add(cbDia);
        filtros.add(labelTitle("Menú/plato:"));
        cbMenuDisponible.setPreferredSize(new Dimension(360, 28));
        filtros.add(cbMenuDisponible);
        filtros.add(btnAutorizar);

        JPanel resumen = new JPanel(new GridLayout(1, 2, 10, 10));
        resumen.setOpaque(false);
        resumen.add(buildInfoMiniCard("CCB actual", lblCCB));
        resumen.add(buildInfoMiniCard("Tarifa a cobrar", lblTarifa));

        JScrollPane menuScroll = new JScrollPane(txtDetalle);
        menuScroll.setBorder(BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true));
        menuScroll.setPreferredSize(new Dimension(300, 320));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(filtros);
        body.add(Box.createVerticalStrut(12));
        body.add(resumen);
        body.add(Box.createVerticalStrut(12));
        body.add(menuScroll);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildImagePanel(String title, JLabel target) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(title, JLabel.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
        lbl.setForeground(ComeUCVView.AZUL_OSCURO);
        target.setOpaque(true);
        target.setBackground(new Color(0xEDF5FF));
        target.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        target.setPreferredSize(new Dimension(220, 220));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(target, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildInfoMiniCard(String title, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(true);
        panel.setBackground(new Color(0xEDF5FF));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 40), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setForeground(ComeUCVView.AZUL_OSCURO);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 12.5f));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 15f));
        value.setForeground(ComeUCVView.TEXTO_OSCURO);
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(new Color(0xF7FBFF));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        JLabel lbl = new JLabel(title);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        lbl.setForeground(ComeUCVView.AZUL_OSCURO);
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private JLabel labelTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
        lbl.setForeground(ComeUCVView.AZUL_OSCURO);
        return lbl;
    }

    private void stylePrimary(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(ComeUCVView.AZUL_OSCURO);
        button.setForeground(Color.WHITE);
    }

    private void styleSecondary(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(ComeUCVView.TEXTO_OSCURO);
        button.setBorder(BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true));
    }

    public void setOnEscanear(ActionListener listener) {
        btnEscanear.addActionListener(listener);
    }

    public void setOnAutorizar(ActionListener listener) {
        btnAutorizar.addActionListener(listener);
    }

    public void setOnSeleccionCambio(ActionListener listener) {
        cbServicio.addActionListener(listener);
        cbDia.addActionListener(listener);
    }

    public void setOnMenuCambio(ActionListener listener) {
        cbMenuDisponible.addActionListener(listener);
    }

    public void setOnSalir(Runnable onSalir) {
        this.onSalir = onSalir;
    }

    public File solicitarArchivoImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona la foto del escaneo facial");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes JPG y PNG", "jpg", "jpeg", "png"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public void setImagenEscaneada(BufferedImage image) {
        setImageOnLabel(lblFotoEscaneada, image, "Sin imagen");
    }

    public void setImagenRegistrada(BufferedImage image) {
        setImageOnLabel(lblFotoRegistrada, image, "Sin coincidencia");
    }

    private void setImageOnLabel(JLabel label, BufferedImage image, String fallbackText) {
        if (image == null) {
            label.setIcon(null);
            label.setText(fallbackText);
            return;
        }
        Image scaled = image.getScaledInstance(210, 210, Image.SCALE_SMOOTH);
        label.setText("");
        label.setIcon(new javax.swing.ImageIcon(scaled));
    }

    public void actualizarEstadoEscaneo(String text, Color color) {
        lblEstadoEscaneo.setText(text == null ? "" : text);
        if (color != null) lblEstadoEscaneo.setForeground(color);
    }

    public void actualizarUsuario(String cedula, String tipo, String saldo) {
        lblCedula.setText(cedula == null ? "—" : cedula);
        lblTipo.setText(tipo == null ? "—" : tipo);
        lblSaldo.setText(saldo == null ? "$0.00" : saldo);
        header.setUsuarioText(cedula == null ? "Por reconocer" : cedula);
    }

    public void actualizarCobro(String ccb, String tarifa, String detalleMenu) {
        lblCCB.setText(ccb == null ? "$0.00" : ccb);
        lblTarifa.setText(tarifa == null ? "$0.00" : tarifa);
        txtDetalle.setText(detalleMenu == null ? "Sin menú disponible." : detalleMenu);
        txtDetalle.setCaretPosition(0);
    }

    public void setAccionHabilitada(boolean enabled) {
        btnAutorizar.setEnabled(enabled);
    }

    public String getServicioSeleccionado() {
        return String.valueOf(cbServicio.getSelectedItem());
    }

    public String getDiaSeleccionado() {
        return String.valueOf(cbDia.getSelectedItem());
    }

    public void setOpcionesMenu(String[] opciones, boolean resetSelection) {
        cbMenuDisponible.removeAllItems();
        if (opciones == null || opciones.length == 0) {
            cbMenuDisponible.addItem("Sin opciones disponibles");
            cbMenuDisponible.setSelectedIndex(0);
            cbMenuDisponible.setEnabled(false);
            return;
        }
        for (String opcion : opciones) {
            cbMenuDisponible.addItem(opcion);
        }
        cbMenuDisponible.setEnabled(true);
        if (resetSelection || cbMenuDisponible.getItemCount() == 1) {
            cbMenuDisponible.setSelectedIndex(0);
        }
    }

    public int getIndiceMenuSeleccionado() {
        return cbMenuDisponible.getSelectedIndex();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
