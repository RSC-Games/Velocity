package velocity.sprite.ui;

import java.awt.Color;
import java.awt.Font;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.Point;

public class UIText extends UIRenderable {
    Color color = Color.red;
    protected int size;
    protected Font font;
    protected String text;

    public UIText(Point pos, float rot, String name, String fontPath, Color c) {
        super(pos, rot, name);
        this.color = c;
        this.size = 12;
        this.font = new Font(fontPath, Font.PLAIN, size);
        this.text = "";
    }

    public void setSize(int size) {
        this.size = size;
        this.font = this.font.deriveFont(Font.PLAIN, (float) size);
    }

    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        Point coords = d.drawRect.getDrawLoc();
        fb.drawText(coords, this.text, this.font, this.color);
    }
}
