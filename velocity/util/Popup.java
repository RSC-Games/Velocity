package velocity.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Show popups on screen for warnings and errors. Generally used more for
 * Velocity error cases than anything else.
 */
public class Popup {
    /**
     * Show an error message on screen.
     * 
     * @param title The window title.
     * @param message The error message to show.
     */
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

    /**
     * Show a warning on screen.
     * 
     * @param title The window title.
     * @param message The warning message to display.
     */
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
