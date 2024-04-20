package velocity.animation.parser;

/**
 * Mutable version of the token. Used for building a token representation which
 * can then be converted to the immutable Token representation.
 */
public class TokenBuilder {
    /**
     * The token ID.
     */
    public TokenID id;

    /**
     * The data.
     */
    public String data = "";

    /**
     * Prepare for generating a token.
     * 
     * @param id The token ID.
     */
    public TokenBuilder(TokenID id) {
        this.id = id;
    }

    /**
     * Add a character to the current data representation.
     * 
     * @param c A character.
     */
    public void append(String c) {
        this.data += c;
    }
}
