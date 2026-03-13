package faceaccess;

import javax.swing.SwingUtilities;

import faceaccess.controller.FaceAccessController;
import faceaccess.view.FaceAccessView;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FaceAccessView view = new FaceAccessView();
            FaceAccessController controller = new FaceAccessController(view);
            controller.init();
            view.setVisible(true);
            SwingUtilities.invokeLater(controller::solicitarEscaneoInicial);
        });
    }
}
