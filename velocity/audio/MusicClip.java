package velocity.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import velocity.system.ResourceLoader;
import velocity.util.Logger;

/**
 * Velocity MusicClip representation. Streams an audioclip in memory. Useful for
 * fast playback of large audio files (for like music or something.)
 */
public class MusicClip {
    /**
     * Dataline for audio playback.
     */
    private Clip line;

    /**
     * Load a music clip.
     * 
     * @param path The audio file path.
     */
    public MusicClip(String path) {
        this.line = (Clip)getLine0(path);
    }

    /**
     * Load a music clip and play it repeatedly.
     * 
     * @param path The audio file path.
     * @param loop Whether it loops.
     */
    public MusicClip(String path, boolean loop) {
        this(path);
        if (loop) this.line.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Open the audio clip data line for playback.
     * 
     * @param path Audio clip path.
     * @return The audio clip.
     */
    private Clip getLine0(String path) {
        ResourceLoader ldr = ResourceLoader.getAppLoader();

        try {
            // Get stream info.
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(ldr.load(path));
            Clip.Info sndInfo = new DataLine.Info(Clip.class, soundStream.getFormat(), 512);
            
            // Start the stream.
            Clip line = (Clip) AudioSystem.getLine(sndInfo);
            line.open(soundStream);
            return line;
        }
        catch (Exception ie) {
            throw new RuntimeException(ie);
        }
    }

    /**
     * Play the audio file.
     */
    public void play() {
        this.line.start();
    }

    /**
     * Stop the audio file.
     */
    public void stop() {
        this.line.stop();
    }

    /**
     * Deallocate the audio file from memory.
     */
    public void remove() {
        this.line.stop();
        this.line.close();
    }

    /**
     * Finalize the audio clip.
     */
    // TODO: Improve audio clip deallocation and playback.
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.log("velocity.audio", "MusicClip deallocated, freeing memory.");
    }
}
