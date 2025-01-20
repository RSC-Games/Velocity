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
import velocity.util.Transform;

/**
 * UITextBox allows additional operations that UIText doesn't, like setting bounding boxes for
 * text.
 */
public class UITextBox extends UIText {
    /**
     * Internal graphics helper for determining font sizes and metrics.
     */
    static BufferedImage gHelper = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    /**
     * Font measurement metrics for rendering.
     */
    protected FontMetrics metrics;

    /**
     * Create a UI Text Box.
     * 
     * @param pos Text box position.
     * @param wh The textbox's width and height.
     * @param name The sprite name.
     * @param fontPath The path of the font to use.
     * @param c The text color.
     */
    public UITextBox(Transform transform, String name, String fontPath, Color c) {
        super(transform, name, fontPath, c);
        
        Graphics g = gHelper.getGraphics();
        this.metrics = g.getFontMetrics(this.font);
    }

    /**
     * Set the text size and get the new metrics.
     * 
     * @param size New size.
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

    /**
     * Split the text into lines for displaying on screen.
     * 
     * @param text The string to split.
     * @param pxGap The gap between each line.
     * @return The formatted array of drawable strings.
     */
    // TODO: Current split system uses large amounts of heap and wastes a lot of alloc
    // /dealloc cycles. Reduce the impact of this.
    private String[] separateLinesByWidth(String text, int pxGap) {
        String[] words = this.text.split("[ ]");
        ArrayList<String> outWords = new ArrayList<String>();

        int startIndex = 0;
        int endIndex = words.length;

        // Parse lines as long as there are remaining words.
        int rowIndex = 0;
        while (startIndex < endIndex && rowIndex * pxGap < this.transform.location.getH()) {
            // Parse a line.
            startIndex = parseLine0(startIndex, words, outWords);
        }
        
        return outWords.toArray(new String[0]);
    }

    /**
     * Parse a line from a list of words. The output is modified in place.
     * 
     * @param start The start index.
     * @param words All provided words.
     * @param out The list of output words. Modified in-place.
     * @return The next index to read a character from.
     */
    private int parseLine0(int start, String[] words, ArrayList<String> out) {
        String lastString = "";
        int i;

        for (i = start; i <= words.length; i++) {
            String testString = buildString(words, start, i);
            
            // Found the longest string for that line.
            if (this.metrics.stringWidth(testString) > this.transform.location.getW()) {
                i -= 1; // Fix offset since it's 1 above the last word.
                break;
            }
            
            lastString = testString;
        }

        out.add(lastString);
        return i;
    }

    /**
     * Build a string from two provided bounds, end exclusive
     * 
     * @param words The list of words.
     * @param start The starting index for copies
     * @param end The ending index.
     * @return The built string.
     */
    private String buildString(String[] words, int start, int end) {
        String[] subWords = Arrays.copyOfRange(words, start, end);
        return String.join(" ", subWords);
    }
}
