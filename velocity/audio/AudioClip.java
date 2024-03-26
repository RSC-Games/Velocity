package velocity.audio;

import javax.sound.sampled.Clip;
//import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;

import java.io.File;

public class AudioClip {
    private Clip clip;  // Targetdataline for streamed.

    // TODO: Convert the current file system to use the Velocity JARfileloader.
    // TODO: BUG: Multiple instance of audio files are currently loaded into memory for the
    // same clip. This wastes a TON of memory!
    public AudioClip(String path) {
        this.clip = (Clip) getLine0(path);
    }

    public AudioClip(String path, boolean loop) {
        this(path);
        if (loop) this.clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private Clip getLine0(String path) {
        try {
            // Get stream info.
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File(path));
            DataLine.Info sndInfo = new DataLine.Info(Clip.class, soundStream.getFormat());
            
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
        this.clip.setFramePosition(0);
        this.clip.start();
    }

    public void stop() {
        this.clip.stop();
    }

    public void remove() {
        //System.out.println("Ending clip playback");
        this.clip.stop();
        this.clip.close();
    }

    public void finalize() {
        System.out.println("[velocity.audio]: AudioClip deallocated, freeing memory.");
    }
}   
 