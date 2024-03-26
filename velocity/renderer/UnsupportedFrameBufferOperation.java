package velocity.renderer;

public class UnsupportedFrameBufferOperation extends RuntimeException {
    public UnsupportedFrameBufferOperation(String source, String info) {
        super(source + ": Unsupported Operation: " + info);
    }
}
