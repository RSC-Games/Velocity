package velocity.shader.include;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class PixelArray {
    private byte[] px;
    private int step;
    private int w;
    private int h;
    public byte[] lum;

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

    // Calculate luminance of all pixels.
    public void buildLumData() {
        this.lum = new byte[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Currently inexact, uses green as simple luminance.
                // Adjusting to use all colors as weighted sum.
                // OLD CODE: lum[(y * w) + x] = (byte)((image.getRGB(x, y) & 0x0000FF00) >> 8);

                // Optimization: inline.
                int offset = (y * w + x) * 3;        
                int col =  ((px[offset + 2] & 0xFF) << 16) | ((px[offset + 1] & 0xFF) << 8) | (px[offset] & 0xFF);
                float out = 
                    ((col & 0x00FF0000) >>> 16) * 0.2126729f + 
                    ((col & 0x0000FF00) >>> 8) * 0.7151522f + 
                    ((col & 0x000000FF) * 0.0721750f);

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

    // Pixel passed in looks like this:
    //   Alpha   Red     Green   Blue
    // 0b00010001000100010001000100010001
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

    public byte[] getDataBuffer() {
        return this.px;
    }
}
