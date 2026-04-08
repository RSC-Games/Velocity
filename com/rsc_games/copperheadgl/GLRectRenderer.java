package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL33C.*;

import java.awt.Color;

import org.joml.Vector3f;

import com.rsc_games.velocity.Rect;

import com.rsc_games.velocity.util.Point;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class GLRectRenderer {
    private static final int MAX_QUADS = 1000;
    private static final int MAX_VERTICES = MAX_QUADS * 8;

    GLRendererContext rendererContext;

    // Main shaders.
    GLShader primitiveShader;

    // Render buffers.
    private int quadVAO = 0;
    private int quadVBO = 0;

    // Mesh data.
    private float[] currentVertices = null;
    private int vertexQty = 0;

    public GLRectRenderer(GLRendererContext rendererContext) {
        this.rendererContext = rendererContext;
    }

    public void init() {
        quadVAO = glGenVertexArrays();
        glBindVertexArray(quadVAO);

        quadVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        
        // Preallocate the space required for the later vertices.
        currentVertices = new float[RectVertexInfo.FLOAT_CNT * MAX_VERTICES];
        glBufferData(GL_ARRAY_BUFFER, currentVertices, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, RectVertexInfo.SIZEOF, RectVertexInfo.POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, RectVertexInfo.SIZEOF, RectVertexInfo.COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);

        // Init shaders.
        primitiveShader = ShaderUtil.compileShader("./shader/primitive/primitive.frag", "./shader/primitive/primitive.vert");
    }

    public void deinit() {
        glBindVertexArray(0);
        glDeleteVertexArrays(quadVAO);
        glDeleteBuffers(quadVBO);
    }

    public void start() {
        this.vertexQty = 0;
    }

    public void commit() {      
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, currentVertices);

        ShaderUtil.bind(primitiveShader);
        glBindVertexArray(quadVAO);
        glDrawArrays(GL_LINES, 0, vertexQty);
        glBindVertexArray(0);
    }

    /**
     * Render an unshaded texture on screen.
     * 
     * @param image Texture to draw.
     * @param drawInfo Texture transforms.
     */
    public void drawRectangle(Rect r, int weight, Color c) {
        pipelineFlushIfRequired();

        Point corner = r.getDrawLoc();
        Point wh = r.getWH();

        Vector3f[] corners = {
            new Vector3f(corner.x, corner.y, 0),
            new Vector3f(corner.x, corner.y + wh.y, 0),
            new Vector3f(corner.x, corner.y + wh.y, 0),
            new Vector3f(corner.x + wh.x, corner.y + wh.y, 0),
            new Vector3f(corner.x + wh.x, corner.y + wh.y, 0),
            new Vector3f(corner.x + wh.x, corner.y, 0),
            new Vector3f(corner.x + wh.x, corner.y, 0),
            new Vector3f(corner.x, corner.y, 0)
        };

        for (int i = 0; i < 8; i++) {
            Vector3f current = rendererContext.toNDC(corners[i]);
            RectVertexInfo vi = new RectVertexInfo(current, c);

            float[] viFloats = vi.ToFloatArray();

            for (int j = 0; j < viFloats.length; j++)
                currentVertices[vertexQty * RectVertexInfo.FLOAT_CNT + j] = viFloats[j];

            vertexQty++;
        }
    }

    /**
     * Flush the current batched mesh if adding to them is no longer possible.
     * 
     * @param shaderWasChanged If the active shader is being swapped.
     */
    private void pipelineFlushIfRequired() {
        if (vertexQty >= MAX_VERTICES) {            
            commit();
            start();
        }
    }
}

class RectVertexInfo {
    /**
     * Vertex size. Comprised of multiple components.
     * 3: SIZEOF pos struct. The pos[] has 3 floats.
     * 1: Global texture alpha (assuming lack of color key).
     * 2: SIZEOF texcoords. Texture data has 2 floats.
     * 1: SIZEOF texid. Texture ID (for lookup) is 1 float.
     */
    public static final int FLOAT_CNT = 3 + 4;
    public static final int SIZEOF = FLOAT_CNT * Float.BYTES;

    // Attribute offset of vertex info
    // These values are expressed in total bytes -> (n*Float.BYTES)
    public static final int POS_OFFSET = 0;
    public static final int COLOR_OFFSET = 3 * Float.BYTES;

    public final float[] pos;
    public final float[] color;

    public RectVertexInfo(Vector3f pos, Color color) {
        this.pos = new float[] { pos.x, pos.y, pos.z };
        this.color = new float[] { color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() };
    }

    public float[] ToFloatArray() {
        float[] output = new float[TexturedVertexInfo.FLOAT_CNT];
        output[0] = pos[0];
        output[1] = pos[1];
        output[2] = pos[2];

        output[3] = color[0];
        output[4] = color[1];
        output[5] = color[2];
        output[6] = color[3];

        return output;
    }
}