package velocity.sprite.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.Point;

/**
 * UITextBox allows additional operations that UIText doesn't, like setting bounding boxes for
 * text.
 */
public class UITextBox extends UIText {
    static BufferedImage gHelper = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    protected FontMetrics metrics;

    public UITextBox(Point pos, Point wh, String name, String fontPath, Color c) {
        super(pos, 0f, name, fontPath, c);
        this.pos.setWH(wh);
        
        Graphics g = gHelper.getGraphics();
        this.metrics = g.getFontMetrics(this.font);
    }

    /**
     * Set the text size and get the new metrics.
     */
    @Override
    public void setSize(int size) {
        super.setSize(size);
        Graphics g = gHelper.getGraphics();
        this.metrics = g.getFontMetrics(this.font);
    }
    
    /**
     * Draw this text box on screen.
     * 
     * @param d Location to draw.
     * @param fb Renderable framebuffer.
     */
    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        Point p = d.drawRect.getDrawLoc();
        int pxGap = this.size;
        String[] lines = separateLinesByWidth(this.text, pxGap);

        for (int l = 0; l < lines.length; l++) {
            fb.drawText(p.add(new Point(0, l * pxGap)), lines[l], this.font, this.color);
        }
    }

    /**
     * Only calculate for one line strings.
     * 
     * @return The approximated rect.
     */
    public Point approxWidth() {
        return new Point(this.metrics.stringWidth(this.text), this.size);
    }

    // Get the lines that need to be drawn onto the screen.
    private String[] separateLinesByWidth(String text, int pxGap) {
        String[] words = this.text.split("[ ]");
        ArrayList<String> outWords = new ArrayList<String>();

        int startIndex = 0;
        int endIndex = words.length;

        // Parse lines as long as there are remaining words.
        int rowIndex = 0;
        while (startIndex < endIndex && rowIndex * pxGap < this.pos.getH()) {
            // Parse a line.
            startIndex = parseLine0(startIndex, words, outWords);
        }
        
        return outWords.toArray(new String[0]);
    }

    // Parse a line from a list of words. The output is modified in place.
    private int parseLine0(int start, String[] words, ArrayList<String> out) {
        String lastString = "";
        int i;

        for (i = start; i <= words.length; i++) {
            String testString = buildString(words, start, i);
            
            // Found the longest string for that line.
            if (this.metrics.stringWidth(testString) > this.pos.getW()) {
                i -= 1; // Fix offset since it's 1 above the last word.
                break;
            }
            
            lastString = testString;
        }

        out.add(lastString);
        return i;
    }

    // Build a string from two provided bounds, end exclusive
    private String buildString(String[] words, int start, int end) {
        String[] subWords = Arrays.copyOfRange(words, start, end);
        return String.join(" ", subWords);
    }
}
