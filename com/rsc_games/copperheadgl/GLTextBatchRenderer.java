package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL33C.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.rsc_games.velocity.Rect;

import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.DrawInfo;
import com.rsc_games.velocity.util.Logger;
import com.rsc_games.velocity.util.Point;
import com.rsc_games.velocity.util.Vector2;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL11C.glGetIntegerv;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

// TODO: Switch to textmesh-based renderer.
public class GLTextBatchRenderer {
    private static final Graphics __g = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();

    private static final int MAX_QUADS = 1000;
    private static final int MAX_VERTICES = MAX_QUADS * 4;
    private static final int MAX_INDICES = MAX_QUADS * 6;

    // Some devices have a higher texture limit than others.
    private static int TEXTURE_LIMIT = 0;

    GLRendererContext rendererContext;
    ArrayList<GLTexture2D> textureList;

    // Main shaders.
    GLShader quadShader;

    // Render buffers.
    private int quadVAO = 0;
    private int quadVBO = 0;
    private int quadIBO = 0;

    // Mesh data.
    private float[] currentVertices = null;
    private int vertexQty = 0;
    private int indexQty = 0;

    private int[] textureSlots = null;
    private int texQty = 0;

    public GLTextBatchRenderer(GLRendererContext rendererContext) {
        this.rendererContext = rendererContext;
        this.textureList = new ArrayList<>();

        int[] out = new int[1];
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, out);
        TEXTURE_LIMIT = out[0];
        glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, out);
    }

    public void init() {
        quadVAO = glGenVertexArrays();
        glBindVertexArray(quadVAO);

        quadVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        
        // Preallocate the space required for the later vertices.
        currentVertices = new float[TextVertexInfo.FLOAT_CNT * MAX_VERTICES];
        glBufferData(GL_ARRAY_BUFFER, currentVertices, GL_DYNAMIC_DRAW);

        // EBO pregeneration.
        int[] indices = new int[MAX_INDICES];
        int vertexOffset = 0;

        for (int i = 0; i < indices.length; i += 6) {
            indices[i + 0] = 0 + vertexOffset;
            indices[i + 1] = 1 + vertexOffset;
            indices[i + 2] = 2 + vertexOffset;

            indices[i + 3] = 0 + vertexOffset;
            indices[i + 4] = 2 + vertexOffset;
            indices[i + 5] = 3 + vertexOffset;
            vertexOffset += 4;
        }

        quadIBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quadIBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, TextVertexInfo.SIZEOF, TextVertexInfo.POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, TextVertexInfo.SIZEOF, TextVertexInfo.COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, TextVertexInfo.SIZEOF, TextVertexInfo.TEX_UV_OFFSET);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 1, GL_FLOAT, false, TextVertexInfo.SIZEOF, TextVertexInfo.TEX_IDX_OFFSET);
        glEnableVertexAttribArray(3);

        textureSlots = new int[TEXTURE_LIMIT];
        texQty = 0;

        glBindVertexArray(0);

        // Init shaders.
        quadShader = ShaderUtil.compileShader("./shader/text/text.frag", "./shader/text/text.vert");

        int[] samplers = genSamplers();

        ShaderUtil.bind(quadShader);
        ShaderUtil.SetIntegerArr("u_Textures", samplers);
        ShaderUtil.unbind();
    }

    /**
     * Erase all registered text textures from last frame, as they're wasting VRAM
     * and will never be used again.
     */
    public void onRenderNextFrame() {
        for (GLTexture2D texture2d : textureList) {
            texture2d.free();
        }

        textureList.clear();
    }

    public void deinit() {
        glBindVertexArray(0);
        glDeleteVertexArrays(quadVAO);
        glDeleteBuffers(quadIBO);
        glDeleteBuffers(quadVBO);
    }

    private int[] genSamplers() {
        int[] samplers = new int[TEXTURE_LIMIT];

        for (int i = 0; i < samplers.length; i++)
            samplers[i] = i;

        return samplers;
    }

    public void start() {
        this.vertexQty = 0;
    }

    public void commit() {
        // Flush quads.
        if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
            Logger.log("copper", "Batching " + texQty + " text instances.");
        
        for (int i = 0; i < texQty; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textureSlots[i]);
        }

        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, currentVertices);

        ShaderUtil.bind(quadShader);
        glBindVertexArray(quadVAO);
        glDrawElements(GL_TRIANGLES, indexQty, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        indexQty = 0;
        texQty = 0;
    }

    /**
     * Draw text on screen. Currently uses AWT and uploads a texture every frame
     * per text line drawn. Also requires a batch flush every time for bug workarounds.
     * Generally just a hell of a function.
     * 
     * @param loc Text location.
     * @param text Text string.
     * @param font Font to use.
     * @param color Text color.
     */
    public void drawText(Point loc, String text, Font font, Color color) {
        // Determine supersampling factor for rendering text at the display resolution
        // rather than the virtual resolution.
        Point virtualResolution = this.rendererContext.getVirtualResolution();
        Point realResolution = this.rendererContext.getRenderResolution();

        Vector2 superSampleFactor = new Vector2(realResolution).div(new Vector2(virtualResolution));

        // Prescale the font to compensate for the true backbuffer resolution.
        Font scaledFont = font.deriveFont(font.getSize() * superSampleFactor.x);
        __g.setFont(scaledFont);

        FontMetrics metrics = __g.getFontMetrics();
        int fontWidth = metrics.stringWidth(text);
        Rectangle2D fontRect = metrics.getStringBounds(text, __g);

        BufferedImage textRaster = new BufferedImage(
            Math.max(fontWidth, 1),
            Math.max((int)fontRect.getHeight(), 1),
            BufferedImage.TYPE_4BYTE_ABGR
        );
        Point rasterSize = new Point(textRaster.getWidth(), textRaster.getHeight());

        // Prevent supersampled text from being upsampled by the graphics engine.
        __g.setFont(font);

        FontMetrics origMetrics = __g.getFontMetrics();
        int origWidth = origMetrics.stringWidth(text);
        Rectangle2D origRect = origMetrics.getStringBounds(text, __g);

        Point trueRasterSize = new Point(
            Math.max(origWidth, 1),
            Math.max((int)origRect.getHeight(), 1)
        );
        
        Graphics textRenderer = textRaster.getGraphics();
        textRenderer.setFont(scaledFont);
        textRenderer.setColor(color);

        // AWT centers text awkwardly.
        textRenderer.drawString(text, 0, rasterSize.y - metrics.getDescent());

        // Queue the texture and track it for deletion next frame.
        DrawInfo info = new DrawInfo(
            new Rect(loc.add(trueRasterSize.div(2)), trueRasterSize), 
            0f, 
            Point.zero, 
            1
        );
        info.drawRect.translate(new Point(0, trueRasterSize.y - origMetrics.getDescent()).mult(-1));

        GLTexture2D fontTexture = new GLTexture2D(textRaster);
        drawQuad(fontTexture, info, color);
        this.textureList.add(fontTexture);
    }

    /**
     * Flush the current batched mesh if adding to them is no longer possible.
     * 
     * @param shaderWasChanged If the active shader is being swapped.
     */
    private void pipelineFlushIfRequired() {
        if (indexQty >= MAX_INDICES || texQty >= TEXTURE_LIMIT) {            
            commit();
            start();
        }
    }

    private void drawQuad(GLTexture2D texture, DrawInfo drawInfo, Color color) {
        // Find the texture if already present.
        float texID = -1f;

        for (int i = 0; i < texQty; i++) {
            if (textureSlots[i] == texture.getHandle()) {
                texID = (float)i;
                break;
            }
        }

        pipelineFlushIfRequired();

        // No texture found
        if (texID == -1f) {
            texID = (float)texQty;
            textureSlots[texQty++] = texture.getHandle();
        }

        // Calculate rect corner locations (and apply transforms!)
        //Point centerPoint = drawInfo.drawRect.getPos();
        //Vector3f center = new Vector3f(centerPoint.x, centerPoint.y, 0f);

        // BUGFIX: Eliminate tile gaps from rounding error when using virtual resolution.
        // GL_CLAMP_TO_EDGE required too.
        //Vector2 radius = new Vector2(drawInfo.drawRect.getWH()).div(2);
        //radius = new Vector2((float)Math.ceil(radius.x), (float)Math.ceil(radius.y));

        //Vector3f scale = new Vector3f(drawInfo.scale.x, drawInfo.scale.y, 1);
        //float rot = (float)Math.toRadians(drawInfo.rot);
        //int layer = drawInfo.drawLayer;

        Point pos = drawInfo.drawRect.getDrawLoc();
        int w = drawInfo.drawRect.getW();
        int h = drawInfo.drawRect.getH();

        Vector3f[] corners = {
            new Vector3f(pos.x, pos.y, 0.0f),
            new Vector3f(pos.x, pos.y + h, 0.0f),
            new Vector3f(pos.x + w, pos.y + h, 0.0f),
            new Vector3f(pos.x + w, pos.y, 0.0f)
        };
        /*Vector3f[] corners = {
            new Vector3f(-radius.x, -radius.y, 0).mul(scale).rotateZ(rot).add(center),
            new Vector3f(-radius.x, radius.y + 1, 0).mul(scale).rotateZ(rot).add(center),
            new Vector3f(radius.x + 1, radius.y + 1, 0).mul(scale).rotateZ(rot).add(center),
            new Vector3f(radius.x + 1, -radius.y, 0).mul(scale).rotateZ(rot).add(center)
        };*/

        Vector2f[] texCoords = new Vector2f[] {
            // top left
            new Vector2f(0f, 0f),
            new Vector2f(0f, 1f),
            new Vector2f(1f, 1f),
            new Vector2f(1f, 0f)
        };

        for (int i = 0; i < 4; i++) {
            Vector3f current = rendererContext.toNDC(corners[i]);
            TextVertexInfo vi = new TextVertexInfo(
                current, 
                color,
                texCoords[i],
                texID
            );

            float[] viFloats = vi.ToFloatArray();

            for (int j = 0; j < viFloats.length; j++)
                currentVertices[vertexQty * TextVertexInfo.FLOAT_CNT + j] = viFloats[j];

            vertexQty++;
        }

        indexQty += 6;
    }
}

class TextVertexInfo {
    /**
     * Vertex size. Comprised of multiple components.
     * 3: SIZEOF pos struct. The pos[] has 3 floats.
     * 4: Global texture alpha (assuming lack of color key).
     */
    public static final int FLOAT_CNT = 3 + 4 + 2 + 1;
    public static final int SIZEOF = FLOAT_CNT * Float.BYTES;

    // Attribute offset of vertex info
    // These values are expressed in total bytes -> (n*Float.BYTES)
    public static final int POS_OFFSET = 0;
    public static final int COLOR_OFFSET = 3 * Float.BYTES;
    public static final int TEX_UV_OFFSET = (3 + 4) * Float.BYTES;
    public static final int TEX_IDX_OFFSET = (3 + 4 + 2) * Float.BYTES;

    public final float[] pos;
    public final float[] color;
    public final float[] tex_uv;
    public final float tex_idx;

    public TextVertexInfo(Vector3f pos, Color color, Vector2f tex_uv, float tex_idx) {
        this.pos = new float[] { pos.x, pos.y, pos.z };
        this.color = new float[] { color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f };
        this.tex_uv = new float[] { tex_uv.x, tex_uv.y };
        this.tex_idx = tex_idx;
    }

    public float[] ToFloatArray() {
        float[] output = new float[TextVertexInfo.FLOAT_CNT];
        /*
        System.arraycopy(pos, 0, output, POS_OFFSET, 3);
        System.arraycopy(color, 0, output, 3, 4);
        System.arraycopy(tex_uv, 0, output, 3 + 4, 2);
        output[8] = tex_idx;*/
        output[0] = pos[0];
        output[1] = pos[1];
        output[2] = pos[2];

        output[3] = color[0];
        output[4] = color[1];
        output[5] = color[2];
        output[6] = color[3];

        output[7] = tex_uv[0];
        output[8] = tex_uv[1];

        output[9] = tex_idx;

        return output;
    }
}