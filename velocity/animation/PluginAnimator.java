package velocity.animation;

import velocity.renderer.RendererImage;

public interface PluginAnimator {
    // Used by the renderer. Returns an image based on the internal animator states and timers.
    public RendererImage getDrawFrame();

    // Updates the internal animator counter for frame gen.
    public void animTick();
}
