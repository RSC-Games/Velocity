package velocity.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TextFile {
    FileReader r;
    FileWriter w;
    boolean canRead = false;
    boolean canWrite = false;

    public TextFile(String path, String access) throws FileNotFoundException, IOException {
        char am = access.charAt(0);

        switch (am) {
            case 'r': 
                this.r = new FileReader(path);
                this.canRead = true;
                break;
            case 'w': 
                this.w = new FileWriter(path);
                this.canWrite = true;
                break;
            default: { throw new IllegalArgumentException("Bad access modifier: " + access); }
        }
    }

    public String read() throws IOException {
        if (!this.canRead) {
            throw new IllegalStateException("Cannot read from a write-only file!");
        }

        String out = ""; // Use a StringBuilder for lower heap impact?
        int c = 0;
        while ((c = r.read()) != -1) {
            out += (char)c;
        }
        return out;
    }

    public String read(int size) throws IOException {
        if (!this.canRead) {
            throw new IllegalStateException("Cannot read from a write-only file!");
        }

        String out = ""; // Use a StringBuilder for lower heap impact?
        for (int i = 0; i < size; i++) {
            int c = r.read();

            if (c == -1) { return out; } // Out of bounds.
            out += (char)c;
        }

        return out;
    }

    public String readLine() throws IOException {
        if (!this.canRead) {
            throw new IllegalStateException("Cannot read from a write-only file!");
        }

        char ln = '\n';
        String out = "";
        int c = 0;

        while ((c = r.read()) != -1 && c != ln) {
            out += (char)c;
        }
        return out;
    }

    public String readLineSafe() {
        try {
            return this.readLine();
        }
        catch (IOException ie) {
            return null;
        }
    }

    public void write(String out) throws IOException {
        if (!this.canWrite) {
            throw new IllegalStateException("Cannot write to a read-only file!");
        }

        for (int i = 0; i < out.length(); i++) {
            w.write(out.charAt(i));
        }
        w.flush();
    }

    public void close() {
        try {
            if (this.canRead) { this.r.close(); }
            if (this.canWrite) { this.w.close(); }
        }
        catch (IOException ie) {/* fail silently */}
    }
}
