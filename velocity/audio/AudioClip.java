package velocity.audio;

import javax.sound.sampled.Clip;
//import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;

import velocity.system.ResourceLoader;
import velocity.util.Logger;

/**
 * Velocity AudioClip representation. Stores a deduplicated audioclip in memory. Useful for
 * fast playback of small audio files (for like SFX or something.)
 */
public class AudioClip {
    /**
     * The internal clip.
     */
    private Clip clip;  // Targetdataline for streamed.

    /**
     * Create an audio clip for playback.
     * 
     * @param path The audio file path.
     */
    public AudioClip(String path) {
        /* TODO: BUG: Multiple instance of audio files are currently loaded into memory for the
         * same clip. This wastes a TON of memory! 
         */
        this.clip = (Clip) getLine0(path);
    }

    /**
     * Alternate constructor. No longer used due to the existence of 
     * {@code MusicClip}
     * @param path
     * @param loop
     */
    @Deprecated(since="v0.5.2.4", forRemoval=true)
    public AudioClip(String path, boolean loop) {
        this(path);
        if (loop) this.clip.loop(Clip.LOOP_CONTINUOUSLY);
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
            DataLine.Info sndInfo = new DataLine.Info(Clip.class, soundStream.getFormat());
            
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
     * Start playing the audio clip.
     */
    public void play() {
        this.clip.setFramePosition(0);
        this.clip.start();
    }

    /**
     * Stop playing the audio clip.
     */
    public void stop() {
        this.clip.stop();
    }

    /**
     * Delete this audioclip from memory.
     */
    public void remove() {
        this.clip.stop();
        this.clip.close();
    }

    /**
     * Track audio clip deletion.
     */
    // TODO: Improve audio clip deallocation and playback.
    @SuppressWarnings("deprecation")
    public void finalize() throws Throwable {
        super.finalize();
        Logger.log("velocity.audio", "AudioClip deallocated, freeing memory.");
    }
}   
 