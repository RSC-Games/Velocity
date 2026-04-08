package com.rsc_games.velocity;

import com.rsc_games.copperheadgl.CopperheadGL;
import com.rsc_games.velocity.renderer.RenderPipeline;
import com.rsc_games.velocity.renderer.window.WindowConfig;

/**
 * Modern replacement to VXRA. Designed to have a simple API and retain limited
 * renderer independence (in case CopperheadGL has to be gutted at some point).
 */
public class PipelineManager {
    private static RenderPipeline internalRP;

    /**
     * Create the new active renderer (notice how it's only one line long compared
     * to VXRA).
     * 
     * @param config Requested window configuration.
     * @return The new pipeline instance.
     */
    public static RenderPipeline newPipeline(WindowConfig config) {
        if (internalRP == null)
            internalRP = new CopperheadGL(config);

        return internalRP;
    }

    /**
     * Get the active render pipeline (frequently for management tasks)
     * 
     * @return The active render pipeline.
     */
    public static RenderPipeline getPipeline() {
        return internalRP;
    }

    /**
     * Run the render part of the game loop.
     */
    public static void render() {
        internalRP.render();
    }
}
