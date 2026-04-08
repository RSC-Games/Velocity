package com.rsc_games.copperheadgl;

import java.util.Arrays;

/**
 * Basic drawcall implementation for LVOGL. Based on the reference
 * LVCPU drawcall system.
 */
class GLDrawCall {
    public final GLCallType type;
    private Object[] parameters;

    public GLDrawCall(GLCallType type, Object[] params) {
        this.type = type;
        this.parameters = params;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public String toString() {
        return type + " args " + Arrays.toString(parameters);
    }
}

