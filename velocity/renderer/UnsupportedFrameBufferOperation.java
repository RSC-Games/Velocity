package velocity.renderer;

/**
 * Allows detecting calls to not yet implemented renderer features and detecting
 * that failure. Useful for test case development.
 */
public class UnsupportedFrameBufferOperation extends RuntimeException {
    /**
     * Create a message to show.
     * 
     * @param source The source file causing the issue.
     * @param info Exception details.
     */
    public UnsupportedFrameBufferOperation(String source, String info) {
        super(source + ": Unsupported Operation: " + info);
    }
}
