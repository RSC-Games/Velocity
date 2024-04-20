package velocity.shader.include;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Legacy image operating class. Functions faster than AWT direct pixel writing functions,
 * but significantly slower than OpenGL.
 */
public class PixelArray {
    /**
     * The pixel data of the provided raster.
     */
    private byte[] px;

    /**
     * How many bytes must be stepped per pixel.
     */
    private int step;

    /**
     * The image width.
     */
    private int w;

    /**
     * The image height.
     */
    private int h;

    /**
     * Dynamically generated luminance calculation.
     */
    public byte[] lum;

    /**
     * Create the pixel array. Must be of type {@code BufferedImage.TYPE_3BYTE_BGR} or
     * {@code BufferedImage.TYPE_4BYTE_ABGR}.
     * 
     * @param img The image to expose the raster for.
     */
    public PixelArray(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_3BYTE_BGR)
            this.step = 3;
        else if (img.getType() == BufferedImage.TYPE_4BYTE_ABGR)
            this.step = 4;
        else
            throw new IllegalArgumentException("Wrong image type; expected TYPE_(3/4)BYTE_(A)BGR");

        this.px = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
        this.w = img.getWidth();
        this.h = img.getHeight();
    }

    /**
     * Generate the luminance data for this raster.
     */
    public void buildLumData() {
        this.lum = new byte[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Currently inexact, uses green as simple luminance.
                // Adjusting to use all colors as weighted sum.
                // OLD CODE: lum[(y * w) + x] = (byte)((image.getRGB(x, y) & 0x0000FF00) >> 8);

                // Optimization: inline.
                // Optimization: Reduce wasteful operations.
                int offset = (y * w + x) * step;        
                float out = 
                    (px[offset + 2] & 0xFF) * 0.2126729f + 
                    (px[offset + 1] & 0xFF) * 0.7151522f + 
                    (px[offset] & 0xFF) * 0.0721750f;

                lum[(y * w) + x] = (byte)(out + 0.5f); // Round the pixel luminance for best effect.
            }
        }
    }

    /**
     * Get a pixel's values in a color array.
     * 
     * @param x X location on the texture.
     * @param y Y location on the texture.
     * @param b Output byte array.
     */
    public void getPixel(int x, int y, byte[] b) {
        int offset = (y * w + x) * step;
        byte alpha = (step == 4 ? px[offset++] : (byte)128);

        b[0] = alpha;
        b[1] = px[offset + 2]; // RED pixel is offset + 2 for tex.
        b[2] = px[offset + 1]; // GREEN stays the same.
        b[3] = px[offset]; // Blue pixel.
    }

    /**
     * Get a pixel's values in an integer.
     * 
     * @param x X location.
     * @param y Y location.
     * @return The pixel data merged into an int23.
     */
    public int getPixel(int x, int y) {
        int offset = (y * w + x) * step;
        int alpha = 128;

        if (step == 4)
            alpha = px[offset++] & 0xFF;

        //if (step == 4 && alpha != 255) System.out.println("Alpha detected: " + alpha);
        return (
            (alpha << 24) |
            (px[offset] & 0xFF) | // Blue
            ((px[offset + 1] & 0xFF) << 8) | // Green
            ((px[offset + 2] & 0xFF) << 16) // Red
        );
    }

    /**
     * Set a pixel from a passed in integer.
     * Pixel passed in looks like this:
     *   Alpha   Red     Green   Blue
     * 0b00010001000100010001000100010001
     * 
     * @param x X location on screen.
     * @param y Y location on screen.
     * @param col The color data.
     */
    public void setPixel(int x, int y, int col) {
        int offset = (y * w + x) * step;

        // BUGFIX (Velocity v0.3.0.1): Alpha values are not copied.
        if (step == 4) px[offset++] = (byte)(col >>> 24 & 0xFF);
        px[offset] = (byte)(col & 0xFF); // Red
        px[offset + 1] = (byte)(col >>> 8 & 0xFF); // Green
        px[offset + 2] = (byte)(col >>> 16 & 0xFF); // Blue
    }

    /**
     * Set a pixel with an input value array.
     * 
     * @param x Texel X location.
     * @param y Texel Y location.
     * @param col Array of colors.
     */
    public void setPixel(int x, int y, byte[] col) {
        int offset = (y * w + x) * step;

        if (step == 4) px[offset++] = col[0];
        px[offset + 2] = col[1]; // RED
        px[offset + 1] = col[2]; // GREEN
        px[offset] = col[3]; // Blue
    }

    /**
     * Get the internal raster data buffer.
     * 
     * @return The data buffer (px data).
     */
    public byte[] getDataBuffer() {
        return this.px;
    }
}
