package velocity;

import java.lang.reflect.*;
import java.util.ArrayList;

import velocity.config.GlobalAppConfig;
import velocity.config.GlobalSceneDefs;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.*;
import velocity.sprite.collision.*;
import velocity.sprite.ui.*;
import velocity.sprite.ui.internal.UnstableRendererWarning;
import velocity.util.*;

/**
 * Velocity's Scene system. Specifies a portable way to instantiate and load
 * scenes as well as how to define a scene itself.
 */
public class Scene {
    /**
     * The currently loaded scene. Allows fast access to scene data.
     */
    public static Scene currentScene;

    /**
     * The scene loading lookup table. Allows user defined scenes to be loaded
     * seamlessly.
     */
    // TODO: Make this private and write a function to set this value.
    public static GlobalSceneDefs sceneLUT = null;

    /**
     * A list of requested scenes. Only the last scene in the queue is loaded.
     */
    public static final ArrayList<String> sceneQueue = new ArrayList<String>();


    /**
     * Stored sprites. These are what tick, init, and delete calls are dispatched
     * to.
     */
    protected ArrayList<Sprite> sprites;

    /**
     * The main scene camera. Also stored in sprites but cached here for faster
     * lookups.
     */
    protected Camera camera;

    /**
     * The scene's name. Not guaranteed to be unique but is the same across game
     * executions.
     */
    public final String name;

    /**
     * The scene UUID. Guaranteed to be unique but may vary across game executions.
     */
    public final int uuid;

    /**
     * Schedule a scene to load on the next available tick.
     * 
     * @param name The name of the scene as listed in {@code SceneDefs}.
     */
    public static void scheduleSceneLoad(String name) {
        sceneQueue.add(name);
    }

    /**
     * Called by Velocity's tick system. Takes any queued scenes to load, unloads the
     * current scene, and instantiates a new one.
     */
    public static void INTERNAL_runSceneLoads() {
        Runtime r = Runtime.getRuntime();
        long preMemUse = (r.totalMemory() - r.freeMemory()) / 1024;

        // No scenes to load; don't waste time here.
        if (sceneQueue.size() == 0)
            return;
    
        // Get rid of the old scene, if any.
        if (currentScene != null) {
            System.out.println("[SceneMgr]: Unloading previous scene " + currentScene.name);
            currentScene.destroyAll();
            currentScene = null;
            System.gc(); // Force object destruction on scene unload.
            System.runFinalization(); // Force cleanup handlers in the JVM to run.
        }

        long lowMem = (r.totalMemory() - r.freeMemory()) / 1024;

        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.printMemoryStatistics();
    
        // Loading more than one scene can potentially cause issues..
        for (String s : sceneQueue) {
            currentScene = loadScene0(s);
        }
        
        // Force GPU texture heap to be cleaned out.
        VXRA.rp.forceGCRun();
    
        // Start the last scene in the queue on this pass.
        currentScene.init();
        sceneQueue.clear();

        // Inject the unstable renderer warning if the pop-up warning is suppressed.
        if (!VXRA.rp.getFeatureSet().FEAT_required && GlobalAppConfig.bcfg.SUPPRESS_UNSTABLE_RENDERER_WARNING)
            currentScene.addSprite(new UnstableRendererWarning("Unstable Renderer Warning", 
                                                       "./velocity/resources/bad_renderer.png"));

        // Scene loading memory diagnostics.
        if (GlobalAppConfig.bcfg.LOG_MEMORY) {
            long postMemUse = (r.totalMemory() - r.freeMemory()) / 1024;
            System.out.println("[SceneMgr]: Used memory (before: " + preMemUse + " kB, after "
                            + lowMem + " kB, diff " + (preMemUse - lowMem) + " kB)");
            System.out.println("[SceneMgr]: New scene memory (total: " + postMemUse 
                            + " kB, scenemem " + (postMemUse - lowMem) + " kB)");
        }
    }

    /**
     * Internal load function called by {@code Scene.INTERNAL_runSceneLoads()}
     * When called, it will instantiate a scene and return it.
     * 
     * @param name Name of scene to load.
     * @return Constructed scene.
     */
    static final Scene loadScene0(String name) {
        // Loading scene files from disk not supported.
        // NOTE: Assuming previous scene was unloaded prior.
        if (currentScene != null)
            throw new IllegalStateException("[SceneMgr]: (INTERNAL) Previous scene was never unloaded!");
    
        // Find and load the class reference for the provided scene name.
        Class<?> idClass = sceneLUT.getSceneClass(name);

        // No scene was found in the scene lookup table. Without a given class, no scene can be
        // instantiated. Inform the developer and assist them with patching it.
        if (idClass == null) {
            Warnings.warn("Scene.loadScene()", "No scene found with name " + name + ".\n" +
                          "The requested scene may be missing from SceneDefs or the name was " +
                          "typed incorrectly.");
            
            sceneLUT.printDefinedScenes();
            
            if (GlobalAppConfig.bcfg.SCENE_LOAD_FAILURE_FATAL) 
                throw new InvalidSceneException("Scene load failure.");

            Scene s = new Scene(GlobalAppConfig.bcfg.LOAD_FAILURE_SCENE, -9999);
            return s;
        }
    
        // Attempt to instantiate the provided class. While using c.getParameterCount() is
        // not bulletproof, it mostly works for attempting to generate a scene. It is possible,
        // however, to make a scene constructor with 2 parameters that aren't (String, int).
        // TODO: Scene class type verification.
        try {
            // Ensure the requested scene is a subclass of the Scene class.
            if (!Scene.class.isAssignableFrom(idClass))
                throw new InstantiationException("Provided scene class " + idClass.getName()
                                                 + "is not a subclass of velocity.Scene!");

            // Get the constructor required by the Scene contract.
            Constructor<?> c = idClass.getConstructor(String.class, int.class);
            Scene s = (Scene)c.newInstance(name, name.hashCode());
            System.out.println("[SceneMgr]: Loaded scene " + name);
            return s;
        }
        // {@code InstantiationException} has not been encountered thus far in generating scenes.
        // When it does happen, opening an issue online will assist greatly in debugging.
        catch (InstantiationException ie) {
            Warnings.warn("Scene.loadScene()", "Failed to construct scene " + name 
                          + ". Unknown reason.");
            ie.printStackTrace();
        }
        // {@code InvocationTargetException} is generally thrown when there's some error in the
        // scene code provided for loading, which can happen a lot. Generally happens when a class field
        // init fails or some precondition is violated... etc.
        catch (InvocationTargetException ie) { 
            Warnings.warn("Scene.loadScene()", "An exception occurred while instantiating scene "
                         + name);
            ie.getCause().printStackTrace();
        }
        catch (NoSuchMethodException ie) {
            // If there is no valid constructor (like if a given constructor doesn't have the
            // 2 required parameters), then assist the developer in trying to fix it.
            Warnings.warn("Scene.loadScene()", name + " missing constructor: (String, int).\n" +
                          "Is there a constructor in " + name + ".java with the signature public " +
                          name + "(String name, int uuid)?");
            ie.printStackTrace();
        }
        // Likely security manager related. Have not gotten it yet.
        catch (IllegalAccessException ie) { ie.printStackTrace(); }
    
        if (GlobalAppConfig.bcfg.SCENE_LOAD_FAILURE_FATAL) 
            throw new InvalidSceneException("Scene load failure.");

        Scene s = new Scene(GlobalAppConfig.bcfg.LOAD_FAILURE_SCENE, -9999);
        return s;
    }

    /**
     * Basic scene instantiation. Can and should be overridden by a subclass.
     * 
     * @param name The name of this scene.
     * @param uuid This scene's UUID (based on the hash of the name.)
     */
    public Scene(String name, int uuid) {
        this.name = name;
        this.uuid = uuid;
        this.sprites = new ArrayList<Sprite>();

        // Required for scene initialization (though may be removed eventually)
        sprites.add(new Camera(new Point(0, 0)));
    }

    /**
     * Get a sprite from this current scene context by its name.
     * Example usage: {@code (Camera)s.getSpriteByName("Camera");}
     * 
     * @param name The desired sprite's name.
     * @return The requested sprite or null if not found.
     */
    public final Sprite getSpriteByName(String name) {
        for (Sprite s : this.sprites) {
            if (name.equals(s.name)) return s;
        }
        return null;
    }

    /**
     * Get a sprite of type T from the current scene context.
     * Example usage: {@code s.getSprite(Camera.class);}
     * 
     * @param clazz Sprite class to search for.
     * @return An instance of the requested class or a subclass, or null if not found.
     */
    @SuppressWarnings("unchecked")
    public final <T extends Sprite> T getSprite(Class<T> clazz) {
        for (Sprite s : this.sprites) {
            Class<? extends Sprite> c = s.getClass();
            
            if (c.equals(clazz) || clazz.isAssignableFrom(c))
                // Unchecked operation; however is safe since T is always a subclass or instance of
                // Sprite.
                return (T)s;
        }
        return null;
    }

    /**
     * Get a list of sprites of type T from the current scene context.
     * Example usage: {@code s.getSprites(Camera.class);}
     * 
     * @param clazz Sprite class to search for.
     * @return An array of instances of the requested class or a subclass, or an empty 
     * list if none found.
     */
    @SuppressWarnings("unchecked")
    public final <T extends Sprite> ArrayList<T> getSprites(Class<T> clazz) {
        ArrayList<T> sprites = new ArrayList<T>();

        for (Sprite s : this.sprites) {
            Class<? extends Sprite> c = s.getClass();
            
            if (c.equals(clazz) || clazz.isAssignableFrom(c))
                // Unchecked operation; however is safe since T is always a subclass or instance of
                // Sprite.
                sprites.add((T)s);
        }
        return sprites;
    }

    /**
     * Register a given sprite for the rendering and tick loop. The sprite
     * must be created elsewhere.
     * 
     * @param s Sprite to add.
     */
    // TODO: Only allow init to occur once.
    public void addSprite(Sprite s) {
        this.sprites.add(s);
        s.init();
    }

    /**
     * Remove a given sprite from the rendering and tick loop.
     * 
     * @param s Sprite to add.
     */
    // TODO: Only allow deletion to occur once, and forbid usage of a sprite
    // post-deletion.
    public void removeSprite(Sprite s) {
        this.sprites.remove(s);
        s.delete();
    }

    /**
     * Transform a point in screen space back into world space.
     * 
     * @param screenP Position in screen space.
     * @return Position in world space.
     */
    public Point screenToWorldPoint(Point screenP) {
        Point cameraPos = this.camera.pos.getDrawLoc();
        return screenP.add(cameraPos);
    }

    /**
     * Transform a point in world space into screen space.
     * 
     * @param screenP Position in world space.
     * @return Position in screen space.
     */
    public Point worldToScreenPoint(Point worldP) {
        Point cameraPos = this.camera.pos.getPos();
        return worldP.sub(cameraPos);
    }

    /**
     * Return this scene's current rendering camera.
     * 
     * @return Scene's current rendering camera.
     */
    public Camera getCamera() {
        return this.camera;
    }

    /**
     * Initialize the scene and sprites. Scenes generally will not need to override
     * this.
     */
    public void init() {
        // Locate the rendering camera.
        for (Sprite s : this.sprites) {
            if (s instanceof Camera) {
                this.camera = (Camera)s;
                break;
            }
        }
    
        if (this.camera == null)
            throw new InvalidSceneException("Failed to init scene " + this.name 
                                            + " (missing Camera).");
    
        // VELOCITY BUGFIX: Cannot add and remove sprites at init time. Fixed
        // on v0.2.6.5
        // VELOCITY BUGFIX (v0.5.1.6): Removing sprites at init time causes some
        // sprites never to be initialized.
        forceInitAllSprites();
    }

    /**
     * Initialize all sprites in a scene regardless of whether scene objects
     * have been rearranged or not.
     */
    private void forceInitAllSprites() {
        ArrayList<Sprite> initSprites = new ArrayList<Sprite>();
        ArrayList<Sprite> remainingSprites = new ArrayList<Sprite>();

        while (!allSpritesInitialized(initSprites, remainingSprites)) {
            for (Sprite s : remainingSprites) {
                s.init();
                initSprites.add(s);
            }
        }
    }

    /**
     * Ensure all sprites have been initialized.
     * 
     * @param init Currently initialized sprites.
     * @param out A list of uninitialized sprites.
     * @return Whether all sprites have been initialized.
     */
    private boolean allSpritesInitialized(ArrayList<Sprite> init, ArrayList<Sprite> out) {
        out.clear();

        for (Sprite s : this.sprites) {
            if (!init.contains(s))
                out.add(s);
        }

        return out.size() == 0;
    }

    /**
     * Velocity scene game tick. Simulates collision and executes all sprite ticks.
     * Entity tick is final since no derivative scene should implement a different
     * tick.
     */
    public final void tick() {
        ArrayList<Sprite> collidables = new ArrayList<Sprite>();
        ArrayList<Sprite> triggerables = new ArrayList<Sprite>();

        // BUGFIX: Since the sprites array can be modified at any time by the internal
        // sprite, the array is cloned and that is operated on instead. Updates to new
        // sprites introduced into the scene context are deferred until the next tick.
        @SuppressWarnings("unchecked")
        ArrayList<Sprite> simObjects = (ArrayList<Sprite>)this.sprites.clone();
        for (Sprite s : simObjects) {
            s.tick();
        }

        // Build collision geometry (probably doesn't need to happen every frame)
        // Doesn't use simObjects since no context modification will occur and allows
        // more up-to-date geometry generation and simulation. Colliders could also
        // have been added this frame, and we don't want to miss those.
        for (Sprite s : this.sprites) {
            if (s instanceof Collidable)
                collidables.add(s);
            else if (s instanceof Triggerable)
                triggerables.add(s);
        }
    
        // Collision sim after update to prevent weird collision glitching errors stemming
        // from out of date geometry. Currently operates on the generated simulation array
        // since any deleted geometry still won't be simulated and prevents concurrency issues.
        for (Sprite s : simObjects) {
            if (s instanceof DynamicSprite) {
                DynamicSprite ds = (DynamicSprite)s;
                ds.simCollide(collidables);
                ds.simTrigger(triggerables);
            }
        }
    }
    
    /**
     * VXRA API REFERENCE:
     * Callback from the currently available VXRA renderer. The renderer calls this
     * once it is ready to accept drawcalls for this frame.
     * 
     * @param fb Supplied game scene framebuffer.
     * @param uifb Supplied UI framebuffer
     */
    public void render(FrameBuffer fb, FrameBuffer uifb) {
        Point cPos = camera.pos.getDrawLoc();
    
        for (Sprite s : this.sprites) {
            // Note: Camera position will be important later on.
            // Also try to collapse this loop and only render Renderables.
            if (s instanceof UIRenderable) {
                UIRenderable uis = (UIRenderable) s;
                DrawInfo info = new DrawInfo(
                    new Rect(uis.pos.getPos(), uis.pos.getW(), uis.pos.getH()), 
                    uis.rot, 
                    Point.one, 
                    uis.sortOrder // Sort layer default 0.
                );

                uis.renderUI(info, uifb);
            }
            else if (s instanceof Renderable) {
                Renderable is = (Renderable) s;
                DrawInfo info = new DrawInfo(
                    new Rect(is.pos.getPos().sub(cPos), is.pos.getW(), is.pos.getH()),
                    is.rot,
                    Point.one,
                    is.sortOrder
                );

                is.render(info, fb);
            }
        }
    }

    /**
     * #deprecated This function was originally meant for debug hooks, but its very
     * inflexible and outdated. A new function will be written that will do a full render
     * pass.
     * 
     * Only meant for debug hooks or {@code DebugRenderer}. Doesn't run any shading code.
     * It's a hell of a function that needs to be cleaned up. Cleanup performed 2/15/2024.
     * When an individual sprite's DEBUG_render is called, only the camera transform is
     * passed in, so the world to screen space transformation needs to be done manually.
     * 
     * @param fb Input framebuffer to draw to.
     * @param cp Debug camera position.
     * @param sf Scale factor (not implemented).
     */
    //@Deprecated(since="v5.2.0.0", forRemoval=true)
    // TODO: Deprecate this function and rewrite the debug renderer system.
    public void DEBUG_render(FrameBuffer fb, Point cp, float[] sf) {    
        // Since this runs in parallel with the main thread we'll operate on a duplicate.
        @SuppressWarnings("unchecked")
        ArrayList<Sprite> dups = (ArrayList<Sprite>)this.sprites.clone();

        for (Sprite s : dups) {
            if (s == null) System.out.println("WARNING! Provided sprite for debug render is NULL!");
            Rect inRect = s.pos.copy();
            inRect.setPos(inRect.getPos().sub(cp));

            DrawInfo info = new DrawInfo(
                inRect, 
                0f, 
                Point.zero, 
                0
            );

            // Renderable support not yet implemented.
            s.DEBUG_render(fb, info);
        }
    }

    /**
     * Called implicitly by the scene loading machinery. Calls the {@code delete}
     * function on all attached sprites and deallocates all used Scene memory.
     */
    private void destroyAll() {
        for (Sprite s : this.sprites) {
            s.delete();
        }
        this.sprites = null;
        this.camera = null;
    }

    /** 
     * Destructor. Should be called when the scene is unloaded or close.
     * Replace with java.lang.ref.Cleaner or something eventually.
     * 
     * Currently used for logging purposes.
     * @throws Throwable Any exception may occur in a finalizer.
     */
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();

        if (GlobalAppConfig.bcfg.LOG_GC)
            System.out.println("[Scene.GC]: GC'ing Scene " + this.name);
         
        // Should never occur but is a good debug test case for now.
        if (this.sprites != null) {
            System.out.println("[Scene.GC]: SCENE WARNING! Sprites list never cleared!");
            this.destroyAll();
        }
    }
}
