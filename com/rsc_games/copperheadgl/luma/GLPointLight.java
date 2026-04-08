package com.rsc_games.copperheadgl.luma;

import java.awt.Color;

import com.rsc_games.velocity.util.Point;

import com.rsc_games.copperheadgl.GLLightingEngine;
import com.rsc_games.velocity.Rect;

public class GLPointLight extends GLLightSource {
    float r;
    Rect rect;

    public GLPointLight(GLLightingEngine le, Point p, float r, float intensity, Color c) {
        super(le, intensity, c);
        this.rect = new Rect(p, (int)(r * 2), (int)(r * 2));
        this.r = r;
    }

    @Override
    public boolean canCull(Rect other) {
        return !this.rect.overlaps(other); // Don't cull if touching tile.
    }

    @Override
    public void setPos(Point pos) {
        this.rect.setPos(pos);
    }

    public Point getPos() {
        return rect.getPos();
    }

    public float getRadius() {
        return r;
    }

    @Override
    public void setRadius(float radius) {
        this.r = radius;
    }

    public Rect getRect() {
        return this.rect;
    }
}
