package velocity.animation;

import java.util.ArrayList;

import velocity.animation.parser.ops.*;
import velocity.renderer.RendererImage;
import velocity.system.Images;
import velocity.util.Logger;

/**
 * High level state abstraction.
 */
class AnimState {
    /**
     * Frames to wait between each change in the character's drawn frame.
     */
    int framesPerUpdate;

    /**
     * Whether this animation repeats or stops playing at the end.
     */
    boolean oneShot = false;

    /**
     * The images to use to draw the character.
     */
    ArrayList<RendererImage> imgs;

    /**
     * Current counter between each frame transition.
     */
    int cntr = 0;

    /**
     * Current frame to render.
     */
    int frameCounter = 0;

    /**
     * Find the essential texture loading and frame update information 
     * for state update.
     * 
     * @param d The provided directive to parse.
     */
    public AnimState(Directive d) {
        this.framesPerUpdate = findFPUVal(d);
        this.oneShot = determineOneShot(d);
        this.imgs = loadAllImages(d);
    }

    /**
     * Get the Frames Per Update value required for proper state update
     * and drawing.
     * 
     * @param d The directive to parse.
     * @return The identified Frames per Update.
     */
    private int findFPUVal(Directive d) {
        FramesPerUpdate fpu = null;

        for (Directive cd : d.getChildren()) {
            if (cd instanceof FramesPerUpdate) {
                fpu = (FramesPerUpdate)cd;
                break;
            }
        }

        if (fpu == null) { throw new IllegalStateException("Found no @FRAMES_PER_UPDATE value to parse in provided case!"); }
        
        return Integer.parseInt(fpu.getArgs().get(0).data);
    }


    /**
     * Get the Frames Per Update value required for proper state update
     * and drawing.
     * 
     * @param d The directive to parse.
     * @return The identified Frames per Update.
     */
    private boolean determineOneShot(Directive d) {
        OneShot oneShot = null;

        for (Directive cd : d.getChildren()) {
            if (cd instanceof OneShot) {
                oneShot = (OneShot)cd;
                break;
            }
        }

        // Debugging info is terrible throughout this entire parser, so the user will have to guess.
        if (oneShot == null) { 
            Logger.log("anim", "found no @ONE_SHOT in current case. assuming looping animation.");
            return false;
        }
        
        return Boolean.parseBoolean(oneShot.getArgs().get(0).data);
    }
    
    /**
     * Load all of the required images for the animator up front.
     * 
     * @param d The directive to parse.
     * @return The loaded images.
     */
    private ArrayList<RendererImage> loadAllImages(Directive d) {
        ArrayList<String> paths = new ArrayList<String>();

        for (Directive cd : d.getChildren()) {
            if (cd instanceof UseTex) {
                //System.out.println("found img path " + cd.getArgs().get(0).data);
                paths.add(cd.getArgs().get(0).data);
            }
        }

        ArrayList<RendererImage> imgArr = new ArrayList<RendererImage>();

        for (String path : paths) {
            imgArr.add(Images.loadImage(path));
        }

        return imgArr;
    }

    /**
     * Update the counters in the animator, and change the frame if the
     * counter hits the FPU value.
     */
    public void tick() {
        this.cntr++;

        if (this.cntr == this.framesPerUpdate) {
            if (this.frameCounter == imgs.size() - 1) {
                if (oneShot) return;  // Don't loop the animation.
                this.frameCounter = 0;
            }
            else
                this.frameCounter++;

            //this.frameCounter = (this.frameCounter != imgs.size() - 1) ? 
            //    frameCounter + 1 : 0;
            this.cntr = 0;
        }
    }

    /**
     * Get the current frame to draw.
     * 
     * @return The current frame outlined by the Frame Counter.
     */
    public RendererImage getDrawFrame() {
        return imgs.get(this.frameCounter);
    }
}
