package velocity.renderer.window;

import velocity.util.Point;

/**
 * Configure a window for use. VXRA-compliant renderers must listen to these flags
 * when they initialize their own frame context.
 */
public class WindowConfig {
    private boolean[] config = new boolean[WindowOption.values().length];
    private String title;
    private String iconPath;
    private Point winRes;

    /**
     * Create a window configuration with the default settings pre-populated.
     * 
     * @param title Window title to use.
     * @param winRes Window resolution (x, y as w, h)
     */
    public WindowConfig(String title, Point winRes, String iconPath) {
        this.title = title;
        this.winRes = winRes;
        this.iconPath = iconPath;
        setDefaults();
    }

    /**
     * Populates the configuration data with the default properties for window
     * creation.
     */
    private void setDefaults() {
        this.config[WindowOption.HINT_FULLSCREEN.ordinal()] = false;
        this.config[WindowOption.HINT_RESIZABLE.ordinal()] = true;
        this.config[WindowOption.HINT_ALWAYS_ON_TOP.ordinal()] = false;
    }

    /**
     * Set the window setup parameter specified by option.
     * 
     * @param option Window config param to set the value of.
     * @param valuw New configuration param value.
     */
    public void setOption(WindowOption option, boolean value) {
        this.config[option.ordinal()] = value;
    }

    /**
     * Get the window setup parameter specified by option.
     * 
     * @param option Window config param to get the value of.
     */
    public boolean getOption(WindowOption option) {
        return this.config[option.ordinal()];
    }

    /**
     * VXRA-facing API! Get the set title for this window.
     * 
     * @return This window's title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * VXRA-facing API! Get the set icon path for this window.
     * 
     * @return This window's requested icon.
     */
    public String getIconPath() {
        return this.iconPath;
    }

    /**
     * VXRA-facing API! Get the requested window resolution.
     * 
     * @return The window resolution.
     */
    public Point getWindowResolution() {
        return this.winRes;
    }
}
