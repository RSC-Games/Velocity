package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL33C.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import com.rsc_games.velocity.util.Logger;
import com.rsc_games.velocity.config.GlobalAppConfig;

class GLTexture2D {
    private int width;
    private int height;
    private int handle;

    public GLTexture2D(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.handle = iTexFromBufImg(image);
    }

    public int getHandle() {
        return handle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Allocate GPU texture memory from a buffered image.
     * 
     * @param image The image to pass.
     * @return The image handle.
     */
    private static int iTexFromBufImg(BufferedImage image) {
        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        // Set texture wrapping mode.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE/*_BORDER*/);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE/*_BORDER*/);

        // And filtering parameters.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // TODO: Send image data as RGBA.
        if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
            System.out.println("[lvogl]: PERFORMANCE WARNING: Converting image pixels to floats!");
        
        float[] imgPx = imageToPx(image);

        int internalFormat = GL_RGBA8;
        int outputFormat = GL_BGRA;
        int dataType = GL_FLOAT;
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, image.getWidth(), 
                     image.getHeight(), 0, outputFormat, dataType, imgPx);

        return handle;
    }

    /**
     * Convert a buffered image to a color array.
     * @param image
     * @return
     */
    private static float[] imageToPx(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        // TODO: Speedup: directly pipe the texture data into OpenGL without converting it.
        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
            return imageToPx0_NOALPHA(image, w, h);
        return imageToPx1_ALPHA(image, w, h);
    }

    /**
     * Internal function. Convert a buffered image without alpha support.
     * 
     * @param image Image to convert.
     * @param w Image width.
     * @param h Image height.
     * @return Pixel array (in float form)
     */
    private static float[] imageToPx0_NOALPHA(BufferedImage image, int w, int h) {
        float[] out = new float[w * h * 4];
        byte[] px = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
            int outOffset = (y * w + x) * 4;
            int offset = (y * w + x) * 3;

            // OUTPUT FORMAT: BGRA (for OpenGL).
            // Input format: BGR (from AWT).
            out[outOffset] = (px[offset] & 0xFF) / 255f; // Blue channel
            out[outOffset + 1] = (px[offset + 1] & 0xFF) / 255f; // Green channel.
            out[outOffset + 2] = (px[offset + 2] & 0xFF) / 255f; // Red channel.
            out[outOffset + 3] = 1.0f; // Alpha channel.
        }
        }

        return out;
    }

    /**
     * Internal function. Convert a buffered image with the alpha channel.
     * 
     * @param image Image to convert.
     * @param w Image width.
     * @param h Image height.
     * @return Pixel array (in float form)
     */
    private static float[] imageToPx1_ALPHA(BufferedImage image, int w, int h) {
        float[] out = new float[w * h * 4];
        byte[] px = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
            int offset = (y * w + x) * 4;

            // OUTPUT FORMAT: BGRA (for OpenGL).
            // Input format: ABGR (from AWT).
            out[offset] = (px[offset + 1] & 0xFF) / 255f; // Blue channel
            out[offset + 1] = (px[offset + 2] & 0xFF) / 255f; // Green channel.
            out[offset + 2] = (px[offset + 3] & 0xFF) / 255f; // Red channel.
            out[offset + 3] = (px[offset] & 0xFF) / 255f; // Alpha channel.
        }
        }

        return out;
    }

    /**
     * Deallocate this texture and remove it from memory.
     */
    public void free() {
        if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
            Logger.log("copper", "Deallocated texture on GPU memory");

        glDeleteTextures(handle);
    }
}
