package velocity;

/**
 * A provided scene could not be loaded since it doesn't support or extend
 * the general contract supplied by Scene.
 */
public class InvalidSceneException extends RuntimeException {
    /**
     * Give the error log and backtrace.
     * 
     * @param message Exception details.
     */
    public InvalidSceneException(String message) {
        super(message);
    } 
}
