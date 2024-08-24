package velocity.util;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.channels.SeekableByteChannel;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.EnumSet;

import velocity.system.ResourceLoader;

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
     * Open a textfile inside a resource bundle. Read access is implicit; it's
     * not possible currently to write to a file insize of a ZIP file.
     * 
     * @param ldr Desired resource loader.
     * @param path File path within the resource loader.
     * @param access Access modifier.
     * @throws FileNotFoundException Cannot find the file.
     * @throws IOException Unspecified IO error.
     */
    public BytesFile(ResourceLoader ldr, String path, String access) throws FileNotFoundException, IOException {
        // If the passed-in path is absolute, load from the filesystem anyway.
        File fileRef = new File(path);
        if (fileRef.isAbsolute()) {
            Logger.log("velocity.system.TextFile", "Got absolute path " + fileRef
                       + "! Loading from filesystem!");
            
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
            return;
        }

        // NOTE! Case covered by the ResourceLoader! An input stream can be directly obtained!
        throw new UnsupportedOperationException("BytesFile does not support using ResourceLoaders! Directly use"
                                                + " the ResourceLoader system!");

        /*
        switch (access) {
            case "rb": 
                this.stream = ldr.load(path);
                this.canRead = true;
                break;
            case "wb":
                throw new UnsupportedOperationException("Cannot write to read-only input stream!");
            default: 
                throw new IllegalArgumentException("Bad access modifier: " + access);
        }*/
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
