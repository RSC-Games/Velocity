package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL20.glDeleteProgram;

import com.rsc_games.velocity.util.Logger;

class GLShader {
    private int progID;

    // Store shader pointer and linked program here
    // when constructed.
    public GLShader(int progID) {
        this.progID = progID;
    }

    public int getID() {
        return this.progID;
    }

    public void delete() {
        Logger.log("copper", "Deleting shader " + progID);
        glDeleteProgram(this.progID);
    }
}
