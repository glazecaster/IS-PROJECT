package app;

import javax.swing.SwingUtilities;
import view.LoginView;
import controller.LoginController;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }
}
