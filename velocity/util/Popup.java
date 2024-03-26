package velocity.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Popup {
    // Never returns.
    public static void showError(String title, String message) {
        JOptionPane diag = new JOptionPane(
            message,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION
        );

        JDialog error = diag.createDialog(title);
        error.setVisible(true);
        System.exit(0);
    }

    public static void showWarning(String title, String message) {
        JOptionPane diag = new JOptionPane(
                message,
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.DEFAULT_OPTION
            );
        
        JDialog warning = diag.createDialog(title);
        warning.setVisible(true);
    }
}
