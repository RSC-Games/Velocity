package velocity;

import java.io.IOException;
import java.time.LocalDateTime;

import velocity.util.TextFile;

/**
 * Internal Velocity crash handler. Writes all diagnostic data to
 * disk in the event of a crash.
 */
class CrashHandler {
    /**
     * Print the crash dump info of a provided exception on disk.
     * 
     * @param ie The thrown exception.
     * @param message An associated error message.
     */
    public static void writeCrashInfo(Throwable ie, String message) {
        try {
            TextFile f = new TextFile("./velocity_crash_info.txt", "w");
            String velocityVersionCode = VelocityMain.VELOCITY_VER + "-" + VelocityMain.VELOCITY_EXT;

            f.write("VELOCITY " + velocityVersionCode + " CRASH LOG " + LocalDateTime.now() + "\n\n");
            f.write("A fatal exception has been detected in the Velocity Player.\n");
            f.write("Generated message: " + message + "\n");
            f.write("Exception Details:\n");

            writeStackTrace(ie, f);

            f.write("\n\nEnd of stack trace.\n");
            f.write("Please contact the application developer and provide them this file.\n");
            f.write("If this is not an application issue, please head to "
                    + "https://github.com/RSC-Games/Velocity and file this error as a new issue "
                    + "report, or if one exists, add this to that issue.\n");

            f.close();
        }
        catch (IOException e) {
            System.err.println("[crash_handler]: Unable to create crash log!");
            e.printStackTrace();
            System.exit(-512);
        }
    }

    /**
     * Print an exception backtrace to the Velocity dump file.
     * 
     * @param exc The exception to read.
     * @param outFile The file to write the data to.
     */
    private static void writeStackTrace(Throwable exc, TextFile outFile) throws IOException {
        outFile.write("Exception in thread main " + exc.getClass().getName() + ": " + exc.getMessage() + "\n");

        for (StackTraceElement e : exc.getStackTrace()) {
            outFile.write("\tat " + e.toString() + "\n");
        }

        if (exc.getCause() != null) {
            outFile.write("\nWas caused by:\n\n");
            writeStackTrace(exc.getCause(), outFile);
        }
    }
}
