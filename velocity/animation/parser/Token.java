package velocity.animation.parser;

public class Token {
    public final TokenID tok;
    public String data;

    public Token(TokenID tok, String data) {
        this.tok = tok;
        this.data = data;
    }

    public String toString() {
        return "tok " + tok + " " + data;
    }
}
