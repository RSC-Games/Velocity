package velocity.util;

import velocity.config.GlobalAppConfig;

/**
 * Velocity's print stream warning system. Prints warnings and a stack trace on the console.
 */
public class Warnings {
    /**
     * Print a warning on standard error.
     * 
     * @param module The failing module.
     * @param message The error message.
     */
    public static void warn(String module, String message) {
        String msg = "[" + module + "]: " + message;
        
        try {
            throw new WarningException(msg);
        }
        catch (WarningException ie) {
            System.out.print("Warning in thread " + Thread.currentThread().getName() + " ");
            ie.printStackTrace();
        }

        if (GlobalAppConfig.bcfg.WARNINGS_FATAL) System.exit(1);
    }

    /**
     * Used for getting the stack trace.
     */
    static class WarningException extends RuntimeException {
        public WarningException(String message) {
            super(message);
        } 
    }
}
