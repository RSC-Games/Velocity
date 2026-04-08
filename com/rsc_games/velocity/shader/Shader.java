package com.rsc_games.velocity.shader;

import java.awt.image.BufferedImage;

/**
 * Outdated shader representation. Left here for compatibility reasons.
 */
@Deprecated(forRemoval=true)
public interface Shader {
    /**
     * Shade a fragment. (Should really be called frag).
     * 
     * @param src The source image.
     * @return The shaded image.
     */
    public BufferedImage shade(BufferedImage src);
}
