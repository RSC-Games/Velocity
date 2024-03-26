package velocity.animation.parser;

public class TokenBuilder {
    public TokenID id;
    public String data = "";

    public TokenBuilder(TokenID id) {
        this.id = id;
    }

    public void append(String c) {
        this.data += c;
    }
}
