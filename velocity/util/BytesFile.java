package velocity.util;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.channels.SeekableByteChannel;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.EnumSet;

public class BytesFile {
    SeekableByteChannel stream;
    boolean canRead = false;
    boolean canWrite = false;

    public BytesFile(String path, String access) throws FileNotFoundException, IOException {
        switch (access) {
            case "rb": 
                this.stream = Files.newByteChannel(Path.of(path), EnumSet.of(StandardOpenOption.READ));
                this.canRead = true;
                break;
            case "wb": 
                this.stream = Files.newByteChannel(Path.of(path), EnumSet.of(StandardOpenOption.WRITE));
                this.canWrite = true;
                break;
            default: { throw new IllegalArgumentException("Bad access modifier: " + access); }
        }
    }

    public void seek(long pos) throws IOException {
        this.stream.position(pos);
    }

    public ByteBuffer read(int size) throws IOException {
        if (!this.canRead)
            throw new IllegalStateException("Cannot read from a write-only file!");

        ByteBuffer buf = ByteBuffer.allocate(size);
        this.stream.read(buf);
        return buf;
    }

    public void write(ByteBuffer out) throws IOException {
        if (!this.canWrite)
            throw new IllegalStateException("Cannot write to a read-only file!");

        this.stream.write(out);
    }

    public void close() {
        try {
            this.stream.close();
        }
        catch (IOException ie) {/* fail silently */}
    }
}
