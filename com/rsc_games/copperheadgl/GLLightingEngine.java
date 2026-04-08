package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL33C.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

//import com.rsc_games.velocity.Scene;
import com.rsc_games.velocity.renderer.InternalLightSource;
import com.rsc_games.velocity.renderer.LightingEngine;
import com.rsc_games.velocity.sprite.Camera;
import com.rsc_games.velocity.util.Point;

import com.rsc_games.copperheadgl.luma.GLLightSource;
import com.rsc_games.copperheadgl.luma.GLPointLight;
import com.rsc_games.copperheadgl.luma.GLSunLight;
import com.rsc_games.velocity.Rect;
import com.rsc_games.velocity.Scene;

// TODO: Rewrite and clean up.
public class GLLightingEngine extends LightingEngine {
    long lightIDCntr = 0L;
    CopperheadGL rp;

    /**
     * Internal. Hook up the render pipeline to the lighting engine.
     * 
     * @param rp The render pipeline.
     */
    public void setiRP(CopperheadGL rp) {
        this.rp = rp;
    }

    /**
     * Dump the current lighting data to a float[], after culling all
     * offscreen lights.
     * 
     * @param litShader The shader representing the lighting pipeline.
     * @param max_lights Maximum supported lights.
     * @return The dumped data array representing the lights.
     */
    public void writePipelineData(GLShader litShader, int max_lights) {
        Point cOffset = Scene.currentScene.getCamera().transform.location.getDrawLoc();

        // TODO: Perform light culling based on screen resolution.
        int index = 0;
        for (InternalLightSource source : this.lights.values()/*culledLights(this.lights.values(), cOffset, max_lights)*/) {
            setLight(litShader, index, (GLLightSource)source);
            index++;
        }

        int litID = litShader.getID();

        // Set the current light count.
        int loc = glGetUniformLocation(litID, "u_lightCount");
        glUniform1i(loc, index);

        // Set screen resolution.
        Point screenResolution = rp.window.getResolution();
        loc = glGetUniformLocation(litID, "u_sres");
        glUniform2f(loc, screenResolution.x, screenResolution.y);

        // Set virtual resolution.
        Point virtualResolution = rp.window.getVirtualResolution();
        loc = glGetUniformLocation(litID, "u_vres");
        glUniform2f(loc, virtualResolution.x, virtualResolution.y);

        // Set current camera location (top left?)
        loc = glGetUniformLocation(litID, "u_cameraOffset");
        glUniform2f(loc, cOffset.x, cOffset.y);
    }

    /**
     * Cull a list of light sources from the current resolved screen resolution.
     * 
     * @param in Input lights.
     * @return The unculled lights.
     */
    @SuppressWarnings("deprecation")
    private ArrayList<GLLightSource> culledLights(Collection<InternalLightSource> in,
                                                   Point cameraOffset, int max_lights) {
        ArrayList<GLLightSource> out = new ArrayList<GLLightSource>();

        Point res = Camera.res;
        Rect crect = new Rect(new int[] {
            cameraOffset.x, 
            cameraOffset.y, 
            cameraOffset.x + res.x, 
            cameraOffset.y + res.y
        }, true);

        int cLength = 0;
        for (InternalLightSource ol : in) {
            GLLightSource light = (GLLightSource)ol;
            Rect lrect = light.getRect();

            // Don't add any more lights than the renderer supports.
            if (cLength >= max_lights)
                break;

            // Only add unculled lights.
            if (lrect == null || crect.overlaps(crect))
                out.add(light);

            cLength++;
        }

        return out;
    }

    /**
     * Set lighting data for a single light.
     * 
     * @param index
     */
    private void setLight(GLShader shader, int i, GLLightSource light) {
        String lookupName = "u_lightArray[" + i + "]";
        final int sid = shader.getID();

        // Sunlights require less information to set.
        if (light instanceof GLSunLight) {
            setLightAttrf(sid, lookupName + ".type", 0f);
            setLightAttrf(sid, lookupName + ".intensity", light.getIntensity());
        }
        else {
            // Light source alignment issue fixed.
            GLPointLight pLight = (GLPointLight)light;
            //Point cpos = pLight.getPos().sub(Scene.currentScene.getCamera().pos.getPos());
            
            setLightAttrf(sid, lookupName + ".type", 1f);
            setLightAttrf(sid, lookupName + ".intensity", light.getIntensity());
            setLightAttr2f(sid, lookupName + ".pos", pLight.getPos());//cpos);
            setLightAttrf(sid, lookupName + ".radius", pLight.getRadius());
        }
    }

    /**
     * Set a light attribute.
     * 
     * @param sid Shader id.
     * @param name Uniform name.
     * @param value New value to set.
     */
    private void setLightAttrf(int sid, String name, float value) {
        int loc = glGetUniformLocation(sid, name);
        glUniform1f(loc, value);
    }

    /**
     * Set a light attribute with 2 floats.
     * 
     * @param sid Shader id.
     * @param name Uniform name.
     * @param value New value to set.
     */
    private void setLightAttr2f(int sid, String name, Point data) {
        int loc = glGetUniformLocation(sid, name);
        glUniform2f(loc, data.x, data.y);
    }

    /**
     * Create a new light.
     */
    @Override
    public long newPointLight(Point p, float r, float intensity, Color c) {
        GLPointLight light = new GLPointLight(this, p, r, intensity, c);
        return light.getLightID();
    }

    @Override
    public long newSunLight(float intensity, Color c) {
        GLSunLight light = new GLSunLight(this, intensity, c);
        return light.getLightID();
    }

    @Override
    public InternalLightSource getLightSourceFromID(long lightid) {
        return this.lights.get(lightid);
    }

    /**
     * Only LVCPU needs this function. All other renderers should ignore it.
     */
    @Override
    @Deprecated(since="v0.5.0.1", forRemoval=true)
    public BufferedImage illuminate(Point loc, BufferedImage in) {
        throw new UnsupportedOperationException("Unimplemented method 'illuminate'");
    }

    /**
     * Register a light source.
     * 
     * @param light Light source to register.
     * @return Light ID.
     */
    @Override
    public long registerLightSource(InternalLightSource light) {
        long id = lightIDCntr++;
        lights.put(id, light);
        return id;
    }

    /**
     * Delete a registered light source.
     * 
     * @param lightid Light ID.
     */
    @Override
    public void deleteLightSource(long lightid) {
        // /System.out.println("[lvogl]: Deallocating light source of ID " + lightid);
        this.lights.remove(lightid);
    }
    
}
