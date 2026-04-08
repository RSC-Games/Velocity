package com.rsc_games.copperheadgl;

import com.rsc_games.velocity.util.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_6;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.opengl.GL11C.*;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import com.rsc_games.velocity.InputSystem;
import com.rsc_games.velocity.config.GlobalAppConfig;

class GLEventHandler {
    private static final HashMap<Integer, Integer> GLFW_MOUSE_TO_AWT = new HashMap<Integer, Integer>();

    static {
        fillMouseData();
    }

    private GLWindow window;
    private InputSystem inputSystem;

    private static void fillMouseData() {
        GLFW_MOUSE_TO_AWT.put(GLFW_MOUSE_BUTTON_1, MouseEvent.BUTTON1);
        GLFW_MOUSE_TO_AWT.put(GLFW_MOUSE_BUTTON_2, MouseEvent.BUTTON3);
        GLFW_MOUSE_TO_AWT.put(GLFW_MOUSE_BUTTON_3, MouseEvent.BUTTON2);
        GLFW_MOUSE_TO_AWT.put(GLFW_MOUSE_BUTTON_4, 4);
        GLFW_MOUSE_TO_AWT.put(GLFW_MOUSE_BUTTON_5, 5);
        GLFW_MOUSE_TO_AWT.put(GLFW_MOUSE_BUTTON_6, 6);
    }

    public GLEventHandler(GLWindow win) {
        this.window = win;

        // Set important event handler callbacks.
        glfwSetKeyCallback(window.getHwnd(), (window, key, scancode, action, mods) -> { 
            iKeyHandler(key, action);
        });
        glfwSetMouseButtonCallback(window.getHwnd(), (window, button, action, mods) -> {
            iMouseHandler(button, action);
        });
        glfwSetFramebufferSizeCallback(window.getHwnd(), (window, w, h) -> { 
            iUpdateResolution(w, h); 
        });

        this.inputSystem = InputSystem.createInputSystem();
    }

    /**
     * Register a key event with the input system.
     * 
     * @param key Key code.
     * @param action Whether it was pressed or released.
     */
    private void iKeyHandler(int key, int action) {
        if (action == GLFW_PRESS)
            this.inputSystem.keyPressed(key);
        else if (action == GLFW_RELEASE)
            this.inputSystem.keyReleased(key);
    }

    /**
     * Register a button event with the input system.
     * 
     * @param button Button code.
     * @param action Whether it was pressed or released.
     */
    private void iMouseHandler(int button, int action) {
        if (!GLFW_MOUSE_TO_AWT.containsKey(button)) {
            Logger.warn("copper", "Got unrecognized mouse button " + button);
            return;
        }

        int awtButton = GLFW_MOUSE_TO_AWT.get(button);

        if (action == GLFW_PRESS)
            this.inputSystem.mousePressed(awtButton);
        else if (action == GLFW_RELEASE)
            this.inputSystem.mouseReleased(awtButton);
    }

    /**
     * Set the internal window and swapchain resolution.
     * 
     * @param w New window width.
     * @param h New window height.
     */
    public void iUpdateResolution(int w, int h) {
        glViewport(0, 0, w, h);

        window.updateResolution(w, h);

        if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
            System.out.printf("[lvogl]: Resized window to %d, %d\n", w, h);
    }
}
