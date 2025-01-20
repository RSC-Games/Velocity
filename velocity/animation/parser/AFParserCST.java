package velocity.animation.parser;

import java.util.ArrayList;

/**
 * Takes direct lexer output and builds a concrete syntax tree. It's not quite a 
 * tree yet, as that is the ASTParser's job. This just does type detection and 
 * converts some word tokens to type codes.
 */
class AFParserCST {
    /**
     * Whitespace characters. These are all ignored.
     */
    static ArrayList<TokenID> whitespace = new ArrayList<TokenID>();

    /**
     * The type codes used for parsing.
     */
    static ArrayList<String> typeCodes = new ArrayList<String>();

    /**
     * Allowed directives.
     */
    static ArrayList<String> directives = new ArrayList<String>();

    /**
     * The previous stage tokenizer.
     */
    AFTokenizer lexer;
    
    /**
     * Detected EOF. Stop parsing when this is hit.
     */
    boolean endOfFile = false;

    /**
     * Populate all of the parsing fields.
     */
    static void populate() {
        whitespace.add(TokenID.TOK_COMMENT);
        whitespace.add(TokenID.TOK_NEWLINE);
        whitespace.add(TokenID.TOK_SPACE);

        typeCodes.add("str");
        typeCodes.add("bool");
        typeCodes.add("float");
        typeCodes.add("int");

        directives.add("ANIM_ROOT");
        directives.add("FRAMES_PER_UPDATE");
        directives.add("PARAM");
        directives.add("VALUE");
        directives.add("USE_TEX");
        directives.add("ONE_SHOT");
    }

    /**
     * Init the CST.
     */
    static {
        populate();
    }

    /**
     * Create the CST Parser.
     * 
     * @param lexer The previous stage Lexer.
     */
    public AFParserCST(AFTokenizer lexer) {
        this.lexer = lexer;
    }

    /**
     * Is data still available in this parser?
     * 
     * @return Whether EOF has been hit yet or not.
     */
    public boolean available() {
        return !endOfFile;
    }

    /**
     * Parse the next token from this file.
     * 
     * @return The parsed token.
     */
    public Token parseNextToken() {
        if (this.endOfFile) return null;

        Token tToProcess = parseNextToken0();
        return parseToken(tToProcess);
    }

    /**
     * Get the next lexer token. A mess of a function that parses out a token
     * from a stream of user-provided content.
     * 
     * @return The token.
     */
    private Token parseNextToken0() {
        if (!lexer.available())
            return null;

        Token it = null;
        do {
            it = lexer.getNextToken();
            // Whitespace; try again.
            if (whitespace.contains(it.tok))
                it = null;
        }
        while (it == null);

        if (it.tok == TokenID.TOK_EOF)
            this.endOfFile = true;

        return it;
    }

    /**
     * Some tokens will be whitespace and can be trimmed. The whitespace is important 
     * mainly for the tokenizer. Parse this whitespace out of the token stream.
     * 
     * @return A token without whitespace that could interfere with parsing.
     */
    private Token parseToken(Token t) {
        Token outT = null;

        switch (t.tok) {
            case TOK_WORD: {
                TokenID tid = getType(t.data);
                outT = tokenFromID(tid, t);
                break;
            }
            default: {
                outT = t;
            }
        }

        return outT;
    }

    /**
     * Create a token from a provided TokenID. Useful for renaming
     * an already generated token.
     * 
     * @param t The token id.
     * @param tok A previously generated token.
     * @return A new token.
     */
    private Token tokenFromID(TokenID t, Token tok) {
        return new Token(t, tok.data);
    }

    /**
     * Get a token's data type, if applicable.
     * 
     * @param data The parsed data.
     * @return The datatype.
     */
    private TokenID getType(String data) {
        // Trim numeric entries first.
        if (isInt(data))
            return TokenID.TOK_INT;

        if (isFloat(data))
            return TokenID.TOK_FLOAT;

        // Then boolean type values.
        if (data.equals("true") || data.equals("false"))
            return TokenID.TOK_BOOL;

        // Then type codes (like str, int, bool, float)
        if (typeCodes.contains(data))
            return TokenID.TOK_TYPE_CODE;

        // Remaining symbols are probably directives.
        if (directives.contains(data))
            return TokenID.TOK_SYMBOL;

        // Symbol names are not currently supported.
        throw new BadParserTokenException("Found undefined symbol: " + data);
    }

    /**
     * Identify if a provided string is an integer.
     * 
     * @param d The string.
     * @return Whether it is an integer.
     */
    private boolean isInt(String d) {
        try {
            Integer.parseInt(d);
            return true;
        }
        catch (NumberFormatException ie) {
            return false;
        }
    }

    /**
     * Identify if a provided string is a float.
     * 
     * @param d The string.
     * @return Whether it is a float.
     */
    private boolean isFloat(String d) {
        try {
            Float.parseFloat(d);
            return true;
        }
        catch (NumberFormatException ie) {
            return false;
        }
    }
}
