package velocity.animation.parser;

/**
 * A token representation used by the parser.
 */
public class Token {
    /**
     * The token ID.
     */
    public final TokenID tok;

    /**
     * The associated data flagged under the token.
     */
    public String data;

    /**
     * Create a token. 
     * 
     * @param tok The token representation.
     * @param data The associated data.
     */
    public Token(TokenID tok, String data) {
        this.tok = tok;
        this.data = data;
    }

    /**
     * Used for debugging.
     * 
     * @return The string representation.
     */
    public String toString() {
        return "tok " + tok + " " + data;
    }
}
