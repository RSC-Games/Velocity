package velocity.system;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.Transparency;
import java.io.IOException;
import javax.imageio.ImageIO;

import velocity.VXRA;
import velocity.config.GlobalAppConfig;
import velocity.renderer.RendererImage;
import velocity.util.Warnings;

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
        RendererImage outImg = null;

        try {
            BufferedImage img = loadRawImage(ldr, path);
            outImg = VXRA.rp.INTERNAL_loadImage(img, path);
        }
        catch (NullPointerException ie) {
            Warnings.warn("velocity.Images", "Could not load provided image (" + path + ")");
            ie.printStackTrace();

            if (GlobalAppConfig.bcfg.MISSING_IMAGE_FATAL) 
                throw new RuntimeException("Could not load requested image!");
        }
        return outImg;
    }

    /**
     * Velocity's standard issue loading mechanic. Load an image from disk at
     * the provided path, send it to the renderer, and provide a wrapped handle
     * to that image.
     * 
     * @param ldr Specific resource loader to grab this resource from.
     * @param path Image path on disk.
     * @return A handle to that image.
     */
    public static RendererImage loadImage(ResourceLoader ldr, String path) {
        RendererImage outImg = null;

        try {
            BufferedImage img = loadRawImage(ldr, path);
            outImg = VXRA.rp.INTERNAL_loadImage(img, path);
        }
        catch (NullPointerException ie) {
            //ie.printStackTrace();
            Warnings.warn("velocity.Images", "Could not load provided image (" + path + ")");

            if (GlobalAppConfig.bcfg.MISSING_IMAGE_FATAL) 
                throw new RuntimeException("Could not load requested image!");
        }
        return outImg;
    }

    /**
     * Load a {@code BufferedImage} directly from disk without renderer
     * caching and loading done first. This is not recommended since the image
     * cache significantly reduces memory use but is useful in some cases.
     * Memory usage may increase since the returned image is not tracked by any 
     * Texture GC. This function forces use of the Velocity Resource Loader. To 
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
     * DEPRECATED! Being re-added as a render pipeline feature.
     */
    @Deprecated(since="v0.2.1.0", forRemoval=true)
    public static BufferedImage scaleFastPercent(float x, float y, BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage dest = new BufferedImage(
                (int)(width * x + 0.9f), 
                (int)(height * y + 0.9f), 
                BufferedImage.TYPE_4BYTE_ABGR // Later allow adaptive conversion.
        );

        //byte[] buf = ((DataBufferByte)src.getRaster().getDataBuffer()).getData(0);
        //byte[] destbuf = ((DataBufferByte)dest.getRaster().getDataBuffer()).getData(0);

        int nWidth = dest.getWidth();
        //int nHeight = dest.getHeight();

        float xScale = 1 / x;
        float yScale = 1 / y;

        for (int ix = 0; ix < nWidth; ix++) {
            for (int iy = 0; iy < nWidth; iy++) {
                int fx = (int)(xScale * ix + 0.5f);
                int fy = (int)(yScale * iy + 0.5f);

                int col = src.getRGB(fx, fy);
                dest.setRGB(ix, iy, col);
            }
        }

        return dest;
    }

    /**
     * DEPRECATED! Being re-added as a render pipeline feature.
     */
    @Deprecated(since="v0.2.1.0", forRemoval=true)
    public static BufferedImage scaleFast(int w, int h, BufferedImage src) {
        int swidth = src.getWidth();
        int sheight = src.getHeight();

        boolean useAlphaExt = (src.getType() == BufferedImage.TYPE_4BYTE_ABGR);
        
        // For debugging purposes
        BufferedImage dest = new BufferedImage(w, h, useAlphaExt ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
        byte[] buf = ((DataBufferByte)src.getRaster().getDataBuffer()).getData(0);
        byte[] destbuf = ((DataBufferByte)dest.getRaster().getDataBuffer()).getData(0);
        
        int dlen = destbuf.length;
        int slen = buf.length;
        
        float xScale = (float)swidth / w;
        float yScale = (float)sheight / h;

        // Allow use of alpha images.
        int pmult = useAlphaExt ? 4 : 3;

        for (int iy = 0; iy < h; iy++) {
            for (int ix = 0; ix < w; ix++) {
                int fx = (int)(xScale * ix + 0.5f);
                int fy = (int)(yScale * iy + 0.5f);

                //int col = src.getRGB(fx, fy);
                //dest.setRGB(ix, iy, col);

                int srcIndex = ((fy * swidth) + fx) * pmult;
                int destIndex = ((iy * w) + ix) * pmult;
                      
                // BUGFIX: Rounding error correction.
                if (srcIndex >= slen) continue;
                if (destIndex >= dlen) continue;

                // Optimized pixel r/w. (for 3BYTE specifically)
                destbuf[destIndex] = buf[srcIndex];
                destbuf[destIndex + 1] = buf[srcIndex + 1];
                destbuf[destIndex + 2] = buf[srcIndex + 2];

                if (useAlphaExt) // Allow 4BYTE alpha too.
                    destbuf[destIndex + 3] = buf[srcIndex + 3];
            }
        }

        return dest;
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
     * Internal helper. Defaults to no forced conversion.
     * See {@code convert(src, forceAlpha)} for more info.
     * 
     * @param src Source image in any format.
     * @return Converted image.
     */
    public static BufferedImage convert(BufferedImage src) {
        return convert(src, false);
    }
}
