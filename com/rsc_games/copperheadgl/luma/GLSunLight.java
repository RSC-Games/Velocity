package com.rsc_games.copperheadgl.luma;

import java.awt.Color;

import com.rsc_games.copperheadgl.GLLightingEngine;
import com.rsc_games.velocity.Rect;

import com.rsc_games.velocity.util.Point;

public class GLSunLight extends GLLightSource {

    public GLSunLight(GLLightingEngine le, float intensity, Color c) {
        super(le, intensity, c);
    }

    @Override
    public boolean canCull(Rect other) {
        return false; // Cannot ever cull a sunlight.
    }

    @Override
    public void setPos(Point pos) {
        throw new UnsupportedOperationException("Cannot set position on a SunLight!");
    }

    @Override
    public void setRadius(float radius) {
        throw new UnsupportedOperationException("Cannot set radius on a SunLight!");
    }
    
    public Rect getRect() {
        return null;
    }
}
