package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL33C.*;

import java.awt.image.BufferedImage;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.DrawInfo;
import com.rsc_games.velocity.renderer.RendererImage;
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

public class GLTextureBatchRenderer {
    private static final int MAX_LIGHT_SOURCES = 128;

    private static final int MAX_QUADS = 1000;
    private static final int MAX_VERTICES = MAX_QUADS * 4;
    private static final int MAX_INDICES = MAX_QUADS * 6;

    // Some devices have a higher texture limit than others.
    private static int TEXTURE_LIMIT = 0;

    GLRendererContext rendererContext;
    GLTextureTrackingSystem textureSystem;

    // Main shaders.
    GLShader currentShader;
    GLShader quadShader;
    GLShader litShader;

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

    public GLTextureBatchRenderer(GLRendererContext rendererContext) {
        this.rendererContext = rendererContext;

        int[] out = new int[1];
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, out);
        TEXTURE_LIMIT = out[0];
        glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, out);

        System.out.println("[copper]: Found max combined tex count " + out[0]);
        System.out.println("[copper]: Render pass max tex count " + TEXTURE_LIMIT);
    }

    public void init() {
        quadVAO = glGenVertexArrays();
        glBindVertexArray(quadVAO);

        quadVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        
        // Preallocate the space required for the later vertices.
        currentVertices = new float[TexturedVertexInfo.FLOAT_CNT * MAX_VERTICES];
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

        glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertexInfo.SIZEOF, TexturedVertexInfo.POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 1, GL_FLOAT, false, TexturedVertexInfo.SIZEOF, TexturedVertexInfo.ALPHA_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertexInfo.SIZEOF, TexturedVertexInfo.TEX_COORD_OFFSET);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 1, GL_FLOAT, false, TexturedVertexInfo.SIZEOF, TexturedVertexInfo.TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        this.textureSystem = new GLTextureTrackingSystem();
        textureSlots = new int[TEXTURE_LIMIT];
        texQty = 0;

        glBindVertexArray(0);

        // Init shaders.
        quadShader = ShaderUtil.compileShader("./shader/textured/textured.frag", "./shader/textured/textured.vert");
        litShader = ShaderUtil.compileShader("./shader/lit/lit.frag", "./shader/lit/lit.vert");
        currentShader = litShader;

        int[] samplers = genSamplers();

        ShaderUtil.bind(quadShader);
        ShaderUtil.SetIntegerArr("u_Textures", samplers);
        ShaderUtil.bind(litShader);
        ShaderUtil.SetIntegerArr("u_Textures", samplers);
        ShaderUtil.unbind();
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

    /**
     * Upload lighting data to the GPU.
     */
    public void updateLightUniforms() {
        ShaderUtil.bind(litShader);
        GLLightingEngine lightingEngine = (GLLightingEngine)rendererContext.renderPipeline.le;
        lightingEngine.writePipelineData(litShader, MAX_LIGHT_SOURCES);
    }

    public void start() {
        this.vertexQty = 0;
    }

    public void commit() {
        // Flush quads.
        if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
            Logger.log("copper", "Batching " + texQty + " unique textures.");
        
        for (int i = 0; i < texQty; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textureSlots[i]);
        }

        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, currentVertices);

        ShaderUtil.bind(currentShader);
        glBindVertexArray(quadVAO);
        glDrawElements(GL_TRIANGLES, indexQty, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        indexQty = 0;
        texQty = 0;
    }

    /**
     * Render an unshaded texture on screen.
     * 
     * @param image Texture to draw.
     * @param drawInfo Texture transforms.
     */
    public void drawTexture(GLRendererImage image, DrawInfo drawInfo) {
        GLTexture2D tex = textureSystem.getGLImage(image);

        // Flush any leftover calls if a shader state change is required.
        changeShaderSafe(quadShader);
        drawQuad(tex, drawInfo);
    }

    /**
     * Render a shaded texture on screen.
     * 
     * @param image Texture to draw.
     * @param drawInfo Texture transforms.
     */
    // TODO: CURRENTLY NO EXTERNAL SHADER SUPPORT!
    public void drawShaded(GLRendererImage image, DrawInfo drawInfo) {
        GLTexture2D tex = textureSystem.getGLImage(image);

        // Flush any leftover calls if a shader state change is required.
        changeShaderSafe(litShader);
        drawQuad(tex, drawInfo);
    }

    private void changeShaderSafe(GLShader desiredShader) {
        boolean changeRequired = desiredShader != currentShader;
        pipelineFlushIfRequired(changeRequired, desiredShader);
    } 

    /**
     * Flush the current batched mesh if adding to them is no longer possible.
     * 
     * @param shaderWasChanged If the active shader is being swapped.
     */
    private void pipelineFlushIfRequired(boolean shaderWasChanged, GLShader desiredShader) {
        if (indexQty >= MAX_INDICES || texQty >= TEXTURE_LIMIT || shaderWasChanged) {            
            commit();
            start();

            currentShader = desiredShader;
        }
    }

    private void drawQuad(GLTexture2D texture, DrawInfo drawInfo) {
        // Find the texture if already present.
        float texID = -1f;

        for (int i = 0; i < texQty; i++) {
            if (textureSlots[i] == texture.getHandle()) {
                texID = (float)i;
                break;
            }
        }

        pipelineFlushIfRequired(false, null);

        // No texture found
        if (texID == -1f) {
            texID = (float)texQty;
            textureSlots[texQty++] = texture.getHandle();
        }

        // Calculate rect corner locations (and apply transforms!)
        Point centerPoint = drawInfo.drawRect.getPos();
        Vector3f center = new Vector3f(centerPoint.x, centerPoint.y, 0f);

        // BUGFIX: Eliminate tile gaps from rounding error when using virtual resolution.
        // GL_CLAMP_TO_EDGE required too.
        Vector2 radius = new Vector2(drawInfo.drawRect.getWH()).div(2);
        //radius = new Vector2((float)Math.ceil(radius.x), (float)Math.ceil(radius.y));

        Vector3f scale = new Vector3f(drawInfo.scale.x, drawInfo.scale.y, 1);
        float rot = (float)Math.toRadians(drawInfo.rot);
        //int layer = drawInfo.drawLayer;

        Vector3f[] corners = {
            new Vector3f(-radius.x, -radius.y, 0).mul(scale).rotateZ(rot).add(center),
            new Vector3f(-radius.x, radius.y + 1, 0).mul(scale).rotateZ(rot).add(center),
            new Vector3f(radius.x + 1, radius.y + 1, 0).mul(scale).rotateZ(rot).add(center),
            new Vector3f(radius.x + 1, -radius.y, 0).mul(scale).rotateZ(rot).add(center)
        };

        Vector2f[] texCoords = new Vector2f[] {
            // top left
            new Vector2f(0f, 0f),
            new Vector2f(0f, 1f),
            new Vector2f(1f, 1f),
            new Vector2f(1f, 0f)
        };

        for (int i = 0; i < 4; i++) {
            Vector3f current = rendererContext.toNDC(corners[i]);
            TexturedVertexInfo vi = new TexturedVertexInfo(
                current, 
                1f, // alpha
                texCoords[i], 
                texID
            );

            float[] viFloats = vi.ToFloatArray();

            for (int j = 0; j < viFloats.length; j++)
                currentVertices[vertexQty * TexturedVertexInfo.FLOAT_CNT + j] = viFloats[j];

            vertexQty++;
        }

        indexQty += 6;
    }

    public RendererImage loadTexture(BufferedImage image, String path) {
        return this.textureSystem.getInternedReference(image, path);
    }

    public void textureGC() {
        this.textureSystem.textureGC();
    }
}

class TexturedVertexInfo {
    /**
     * Vertex size. Comprised of multiple components.
     * 3: SIZEOF pos struct. The pos[] has 3 floats.
     * 1: Global texture alpha (assuming lack of color key).
     * 2: SIZEOF texcoords. Texture data has 2 floats.
     * 1: SIZEOF texid. Texture ID (for lookup) is 1 float.
     */
    public static final int FLOAT_CNT = 3 + 1 + 2 + 1;
    public static final int SIZEOF = FLOAT_CNT * Float.BYTES;

    // Attribute offset of vertex info
    // These values are expressed in total bytes -> (n*Float.BYTES)
    public static final int POS_OFFSET = 0;
    public static final int ALPHA_OFFSET = 3 * Float.BYTES;
    public static final int TEX_COORD_OFFSET = (3 + 1) * Float.BYTES;
    public static final int TEX_ID_OFFSET = (3 + 1 + 2) * Float.BYTES;

    public final float[] pos;
    public final float alpha;
    public final float[] texCoords;
    public final float texID;

    public TexturedVertexInfo(Vector3f pos, float alpha, Vector2f texCoords, float texID) {
        this.pos = new float[] { pos.x, pos.y, pos.z };
        this.alpha = alpha;
        this.texCoords = new float[] { texCoords.x, texCoords.y };
        this.texID = texID;
    }

    public float[] ToFloatArray() {
        float[] output = new float[TexturedVertexInfo.FLOAT_CNT];
        output[0] = pos[0];
        output[1] = pos[1];
        output[2] = pos[2];

        output[3] = alpha;

        output[4] = texCoords[0];
        output[5] = texCoords[1];

        output[6] = texID;

        return output;
    }
}