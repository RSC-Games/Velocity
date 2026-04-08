package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL33C.*;

import java.io.IOException;

import com.rsc_games.velocity.system.ResourceLoader;
import com.rsc_games.velocity.util.TextFile;

public class ShaderUtil {
    // Contains utility functions like glLink and glCompile
    static GLShader active;

    /**
     * Bind a new shader.
     * 
     * @param shader
     */
    public static void bind(GLShader shader) {
        active = shader;
        //System.out.println("[lvogl.shader]: Binding shader " + shader.getID());
        glUseProgram(shader.getID());
    }

    /**
     * Get the currently bound shader.
     * 
     * @return The current shader
     */
    public static GLShader getCurrent() {
        return active;
    }

    /**
     * Unbind all shaders.
     */
    public static void unbind() {
        active = null;
        //System.out.println("[lvogl.shader]: Unbound all shaders");
        glUseProgram(0);
    }

    /**
     * Compile a shader from two input paths.
     * 
     * @param fragShader Fragment shader path.
     * @param vertShader Vertex shader path.
     * @return A high-level compiled shader representation.
     */
    public static GLShader compileShader(String fragShader, String vertShader) {
        // Compile the shader pieces.
        int vertShaderID = iCompile(vertShader, GL_VERTEX_SHADER);
        int fragShaderID = iCompile(fragShader, GL_FRAGMENT_SHADER);

        // Link the pieces into a program.
        int progID = glCreateProgram();
        glAttachShader(progID, vertShaderID);
        glAttachShader(progID, fragShaderID);
        glLinkProgram(progID);

        // Verify proper shader compilation.
        int[] success = new int[1];
        glGetProgramiv(progID, GL_LINK_STATUS, success);
        if (success[0] != GL_TRUE) {
            System.err.println(glGetProgramInfoLog(progID));
            throw new IllegalStateException("Failed to link shaders!");
        }

        System.out.println("[copper]: Successfully built shader program " + fragShader
                           + ", " + vertShader);

        // Free memory.
        glDeleteShader(vertShaderID);
        glDeleteShader(fragShaderID);
        return new GLShader(progID);
    }

    /**
     * Internally compile a shader piece.
     */
    private static int iCompile(String path, int shaderType) {
        if (path == null) 
            throw new IllegalArgumentException("Provided shader path is null!");

        int shaderID = glCreateShader(shaderType);

        // Load the shader code.
        String src = null;

        try {
            TextFile tf = new TextFile(ResourceLoader.getAppLoader(), path, "r");
            src = tf.read();
            tf.close();
        }
        catch (IOException ie) {
            throw new IllegalStateException("Could not load " + path);
        }

        // Compile the shader.
        glShaderSource(shaderID, src);
        glCompileShader(shaderID);

        // Ensure it built.
        int[] success = new int[1];
        glGetShaderiv(shaderID, GL_COMPILE_STATUS, success);

        if (success[0] != GL_TRUE) {
            System.err.println(glGetShaderInfoLog(shaderID));
            throw new IllegalStateException("Failed to compile shader " + path);
        }

        return shaderID;
    }

    /* ========== UNIFORM SETTING METHODS ========== */
    public static void SetFloat(String name, float v0) {
        int loc = glGetUniformLocation(active.getID(), name);
        glUniform1f(loc, v0);
    }

    public static void SetV2f(String name, float v0, float v1) {
        int loc = glGetUniformLocation(active.getID(), name);
        glUniform2f(loc, v0, v1);
    }

    public static void SetV3f(String name, float v0, float v1, float v2) {
        int loc = glGetUniformLocation(active.getID(), name);
        glUniform3f(loc, v0, v1, v2);
    }

    public static void SetIntegerArr(String name, int[] arr) {
        int loc = glGetUniformLocation(active.getID(), name);
        glUniform1iv(loc, arr);
    }
}
