package velocity.util;

import velocity.GlobalAppConfig;

public class Warnings {
    public static void warn(String module, String message) {
        String msg = "[" + module + "]: warning: " + message;
        
        try {
            throw new WarningException(msg);
        }
        catch (WarningException ie) {
            ie.printStackTrace();
        }

        if (GlobalAppConfig.bcfg.WARNINGS_FATAL) System.exit(1);
    }

    static class WarningException extends RuntimeException {
        public WarningException(String message) {
            super(message);
        } 
    }
}
