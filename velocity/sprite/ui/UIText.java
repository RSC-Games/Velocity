package velocity.sprite.ui;

import java.awt.Color;
import java.awt.Font;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.Point;
import velocity.util.Transform;

/**
 * Basic text renderer for the UI Canvas.
 */
public class UIText extends UIRenderable {
    /**
     * Text color. Defaults to red text.
     */
    Color color = Color.red;

    /**
     * Text size (in pixels).
     */
    protected int size;

    /**
     * The current font for rendering.
     */
    protected Font font;

    /**
     * String to render.
     */
    protected String text;

    /**
     * Create the UI Text renderable.
     * 
     * @param pos The offset position for drawing the text.
     * @param rot The rotation angle of the text.
     * @param name The sprite name.
     * @param fontPath The path of the font file.
     * @param c The text color 
     */
    public UIText(Transform transform, String name, String fontPath, Color c) {
        super(transform, name);
        this.color = c;
        this.size = 12;
        this.font = new Font(fontPath, Font.PLAIN, size);
        this.text = "";
    }

    /**
     * Set the rendered text size.
     * 
     * @param size New text size.
     */
    public void setSize(int size) {
        this.size = size;
        this.font = this.font.deriveFont(Font.PLAIN, (float) size);
    }

    /**
     * Render the text in the compositing stage.
     * 
     * @param d Draw transform.
     * @param fb Rendering frame buffer.
     */
    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        Point coords = d.drawRect.getDrawLoc();
        fb.drawText(coords, this.text, this.font, this.color);
    }
}
