package velocity;

import java.util.HashMap;

import velocity.scene.DefaultScene;
import velocity.scene.ErrorScene;

/**
 * Global scene defines. This class must be inherited in the application code
 * and passed into {@code VelocityMain.app_main} for scene loading.
 */
public abstract class GlobalSceneDefs {
    /**
     * Stores the scene classes in a LUT.
     * To add an entry, use sceneDefs.put(<name>, <class_name>.class);
     */
    protected HashMap<String, Class<? extends Scene>> sceneDefs = new HashMap<String, Class<? extends Scene>>();

    /**
     * Override this constructor in your own code and define scenes in it.
     * Do not forget the super() call!
     * @see sceneDefs.
     */
    public GlobalSceneDefs() {
        sceneDefs.put("DefaultScene", DefaultScene.class);
        sceneDefs.put("ErrorScene", ErrorScene.class);
    }

    /**
     * Internal facing API. Get a scene class from the internal registry list.
     * 
     * @param name Scene name to find.
     * @return The registered Scene class.
     */
    public final Class<? extends Scene> getSceneClass(String name) {
        return sceneDefs.get(name);
    }

    /**
     * Internal facing API. Print all of the defined scenes into the console for
     * load failure debugging.
     */
    public final void printDefinedScenes() {
        System.out.println("Currently defined Scenes: ");

        for (String sceneName : sceneDefs.keySet()) {
            System.out.println(sceneName + " : " + sceneDefs.get(sceneName).getSimpleName());
        }

        System.out.println();
    }
}
