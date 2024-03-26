package velocity.animation;

import java.util.ArrayList;

import velocity.Images;
import velocity.animation.parser.ops.*;
import velocity.renderer.RendererImage;

public class AnimState {
    int framesPerUpdate;
    ArrayList<RendererImage> imgs;

    int cntr = 0;
    int frameCounter = 0;

    // Find the essential texture loading and frame update
    // information for state update.
    public AnimState(Directive d) {
        this.framesPerUpdate = findFPUVal(d);
        this.imgs = loadAllImages(d);
    }

    // Get the Frames Per Update value required for proper state update
    // and drawing.
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

    // Update the image to render.
    public void tick() {
        this.cntr++;

        if (this.cntr == this.framesPerUpdate) {
            this.frameCounter = (this.frameCounter != imgs.size() - 1) ? 
                frameCounter + 1 : 0;
            this.cntr = 0;
        }
    }

    public RendererImage getDrawFrame() {
        return imgs.get(this.frameCounter);
    }
}
