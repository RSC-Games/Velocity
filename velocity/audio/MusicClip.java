package velocity.audio;

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class MusicClip {
    private Clip line;

    // TODO: Convert the current file system to use the Velocity JARfileloader.
    public MusicClip(String path) {
        this.line = (Clip)getLine0(path);
    }

    public MusicClip(String path, boolean loop) {
        this(path);
        if (loop) this.line.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private Clip getLine0(String path) {
        try {
            // Get stream info.
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File(path));
            Clip.Info sndInfo = new DataLine.Info(Clip.class, soundStream.getFormat(), 512);
            
            // Start the stream.
            Clip line = (Clip) AudioSystem.getLine(sndInfo);
            line.open(soundStream);
            return line;
        }
        catch (Exception ie) {
            ie.printStackTrace();
            System.exit(0);
        }

        return null;
    }

    public void play() {
        this.line.start();
    }

    public void stop() {
        this.line.stop();
    }

    public void remove() {
        this.line.stop();
        this.line.close();
    }

    public void finalize() {
        System.out.println("[velocity.audio]: MusicClip deallocated, freeing memory.");
    }
}
