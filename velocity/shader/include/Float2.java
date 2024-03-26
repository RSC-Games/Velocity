package velocity.shader.include;

public class Float2 {
    public float x;
    public float y;

    public Float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "float2(" + this.x + ", " + this.y + ")";
    }
}
