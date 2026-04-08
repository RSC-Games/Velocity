package com.rsc_games.velocity.system;

import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.rsc_games.velocity.VXRA;

import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.RendererImage;
import com.rsc_games.velocity.util.Warnings;

/**
 * Velocity's image handling class. Handles all image wrapping and loading
 * from either the local filesystem or the provided JARfile.
 */
public class Images {
    /**
     * Velocity's standard issue loading mechanic. Load an image from disk at
     * the provided path, send it to the renderer, and provide a wrapped handle
     * to that image.
     * 
     * @param path Image path on disk.
     * @return A handle to that image.
     */
    public static RendererImage loadImage(String path) {
        ResourceLoader ldr = ResourceLoader.getAppLoader();
        return loadImage(ldr, path);
    }

    /**
     * Velocity's standard issue loading mechanic. Load an image from disk at
     * the provided path, send it to the renderer, and provide a wrapped handle
     * to that image.
     * 
     * @implNote When configured to continue on image load failure this function will
     *  return a default image with a path of "__XX_VELOCITY_DEFAULT_TEXTURE__". It is
     *  the VXRA renderer's job to provide this texture.
     * 
     * @param ldr Specific resource loader to grab this resource from.
     * @param path Image path on disk.
     * @return A handle to that image.
     */
    public static RendererImage loadImage(ResourceLoader ldr, String path) {
        // Determine if the requested image is already cached or if it needs to be loaded.
        if (!VXRA.rp.isTextureCached(path)) {
            BufferedImage img = loadRawImage(ldr, path);

            if (!VXRA.rp.registerTexture(img, path)) {
                if (GlobalAppConfig.bcfg.MISSING_IMAGE_FATAL)
                    throw new RuntimeException("Failed to load image at path " + path);
    
                Warnings.warn("velocity.system.Images", "Failed to load provided image (" + path + ")");
                return VXRA.rp.getTextureHandleFromPath("__XX_VELOCITY_DEFAULT_TEXTURE__");
            }
        }

        return VXRA.rp.getTextureHandleFromPath(path);
    }

    /**
     * Load a {@code BufferedImage} directly from disk without renderer
     * caching and loading done first. This is not recommended since the image
     * cache significantly reduces memory use but is useful in some cases.
     * Memory usage may increase since the returned image is not tracked by any 
     * Texture GC. This function forces use of the System Resource Loader. To 
     * use another one, @see loadRawImage(ResourceLoader, String)
     * 
     * @param path Image path.
     * @return a loaded BufferedImage.
     */
    @Deprecated(since="v0.3.2.2", forRemoval=false)
    public static BufferedImage loadRawImage(String path) {
        ResourceLoader ldr = SystemResourceLoader.getSystemResourceLoader();

        try {
            return convert(ImageIO.read(ldr.load(path)));
        }
        catch (IOException ie) {
            ie.printStackTrace();
            throw new RuntimeException(ie);
        }
    }

    /**
     * Load a {@code BufferedImage} directly from disk without renderer
     * caching and loading done first. This is not recommended since the image
     * cache significantly reduces memory use but is useful in some cases.
     * @deprecated This function is not recommended due to potential high memory 
     * usage since the returned image is not tracked by any Texture GC.
     * 
     * @param path Image path.
     * @return a loaded BufferedImage.
     */
    @Deprecated(since="v0.5.2.0", forRemoval=false)
    public static BufferedImage loadRawImage(ResourceLoader ldr, String path) {
        try {
            return convert(ImageIO.read(ldr.load(path)));
        }
        catch (IOException ie) {
            throw new NullPointerException("Could not load image " + path);
        }
    }

    /**
     * Internal. Takes a provided image of an unknown type and converts it into a 
     * standardized form that Velocity's shaders can handle. Velocity currently
     * only supports TYPE_3BYTE_BGR and TYPE_4BYTE_ABGR internally.
     * 
     * @param src Source image
     * @param forceAlpha Require alpha support even on images without alpha.
     * @return The converted image.
     */
    public static BufferedImage convert(BufferedImage src, boolean forceAlpha) {
        int sw = src.getWidth();
        int sh = src.getHeight();

        int imageFormat = (src.getTransparency() == Transparency.OPAQUE && !forceAlpha) 
                           ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_4BYTE_ABGR;
        BufferedImage dest = new BufferedImage(sw, sh, imageFormat);

        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                int pix = src.getRGB(x, y);
                dest.setRGB(x, y, pix);
            }
        }

        return dest;
    }

    /**
     * Internal helper. Defaults to exclude alpha if alpha isn't present.
     * See {@code convert(src, forceAlpha)} for more info.
     * 
     * @param src Source image in any format.
     * @return Converted image.
     */
    public static BufferedImage convert(BufferedImage src) {
        return convert(src, false);
    }
}
