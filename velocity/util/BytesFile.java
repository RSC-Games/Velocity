package velocity.util;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.channels.SeekableByteChannel;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.EnumSet;

/**
 * Easy to use File I/O wrapper for reading and writing to a file with binary data.
 */
public class BytesFile {
    /**
     * File I/O stream.
     */
    SeekableByteChannel stream;

    /**
     * Allowed read permissions.
     */
    boolean canRead = false;

    /**
     * Allowed write permissions.
     */
    boolean canWrite = false;

    /**
     * Open a readable byte stream on a randomly accessable file. The file can either
     * be opened with read or write permissions ("rb" or "wb", respectively).
     * 
     * @param path The relative file path to open.
     * @param access The file access modifier.
     * @throws FileNotFoundException Cannot find the file at the specified path.
     * @throws IOException Some unspecified I/O issue is preventing file load.
     */
    // TODO: Hook this up to the resource loader system (if a resource is requested).
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

    /**
     * Seek through the file and move the read/write pointer.
     * 
     * @param pos The current file position.
     * @throws IOException An unspecified I/O error is preventing this.
     */
    public void seek(long pos) throws IOException {
        this.stream.position(pos);
    }

    /**
     * Read the file data of a given length.
     * 
     * @param size The length to read.
     * @return The read data.
     * @throws IOException An unspecified I/O error prevented the operation.
     */
    public ByteBuffer read(int size) throws IOException {
        if (!this.canRead)
            throw new IllegalStateException("Cannot read from a write-only file!");

        ByteBuffer buf = ByteBuffer.allocate(size);
        this.stream.read(buf);
        return buf;
    }

    /**
     * Write data to the file.
     * 
     * @param out The bytes to write.
     * @throws IOException An unspecified I/O error is preventing the operation.
     */
    public void write(ByteBuffer out) throws IOException {
        if (!this.canWrite)
            throw new IllegalStateException("Cannot write to a read-only file!");

        this.stream.write(out);
    }

    /**
     * Close this file stream.
     */
    public void close() {
        try {
            this.stream.close();
        }
        catch (IOException ie) {/* fail silently */}
    }
}
