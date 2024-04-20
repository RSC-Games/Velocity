package velocity.animation.parser;

import java.io.IOException;
import java.util.HashMap;

import velocity.system.ResourceLoader;
import velocity.util.TextFile;
import velocity.util.Warnings;

/**
 * The first stage of parsing. Takes an input file and converts it into an
 * input token stream.
 */
class AFTokenizer {
    /**
     * Allowed parsable tokenes.
     */
    static HashMap<String, TokenID> tokTable = new HashMap<String, TokenID>();

    /**
     * Input file to parse.
     */
    TextFile inputf;

    /**
     * The current token. Used for building a token that is longer than
     * one character.
     */
    TokenBuilder cToken;

    /**
     * Read the next character from a file.
     */
    boolean read = true;

    /**
     * Current character read from disk.
     */
    String c = "";

    /**
     * Populate the tokens to parse.
     */
    static void populateTable() {
        tokTable.put("@", TokenID.TOK_DIRECTIVE);
        tokTable.put("#", TokenID.TOK_COMMENT);
        tokTable.put("\n", TokenID.TOK_NEWLINE);
        tokTable.put(" ", TokenID.TOK_SPACE);
        tokTable.put(":", TokenID.TOK_COLON);
        // Word token not recognized by this table.
        tokTable.put(";", TokenID.TOK_SEMICOLON);
        tokTable.put("{", TokenID.TOK_BRACE_OPEN);
        tokTable.put("}", TokenID.TOK_BRACE_CLOSE);
        tokTable.put("\"", TokenID.TOK_QUOTE);
        tokTable.put("", TokenID.TOK_EOF);
    }

    /**
     * Set up the tokenizer.
     */
    static {
        populateTable();
    }
    
    /**
     * Try to parse an animator.
     * 
     * @param inputFile Animator file.
     */
    public AFTokenizer(String inputFile) {
        try {
            this.inputf = new TextFile(ResourceLoader.getAppLoader(), inputFile, "r");
        }
        catch (IOException ie) {
            Warnings.warn("AFTokenizer", "Cannot open file " + inputFile);
        }
    }

    /**
     * Is more text available in the file?
     * 
     * @return Whether the file has been closed (because of EOF) or not.
     */
    public boolean available() {
        return this.inputf != null;
    }

    /**
     * Generate the next token. Relies on external global state. Not re-entrant.
     * 
     * @return The next token.
     */
    public Token getNextToken() {
        Token t = null;

        while (t == null) {
            if (this.inputf == null)
                return null;

            if (read) c = readSrc(1);

            t = getNextToken0(c);
            if (t != null && t.tok == TokenID.TOK_WORD)
                read = false;
            else
                read = true;
        }
        
        return t;
    }

    /**
     * Parse a token from a provided character.
     * 
     * @param c The current character.
     * @return The parsed token.
     */
    private Token getNextToken0(String c) {
        // Carriage return unsupported.
        if (c.equals("\r"))
            return null;
        
        TokenID tokType = getTokType(c);

        // No recognized token.
        if (tokType == null)
            return null;

        // String detection and reading.
        if (isCurrentToken(TokenID.TOK_STRING)) {
            if (tokType == TokenID.TOK_QUOTE)
                return endToken();

            this.cToken.append(c);
            return null;
        }

        // Comment trimming.
        else if (isCurrentToken(TokenID.TOK_COMMENT)) {
            if (tokType == TokenID.TOK_NEWLINE)
                return endToken();

            this.cToken.append(c);
            return null;
        }

        // Word reading
        else if (isCurrentToken(TokenID.TOK_WORD)) {
            if (tokType != TokenID.TOK_WORD)
                return endToken();

            this.cToken.append(c);
            return null;
        }

        // Minimal token processing needs to be done, so only one step will yield a result.
        switch (tokType) {
            case TOK_QUOTE: {
                startNewToken(TokenID.TOK_STRING);
                break;
            }
            case TOK_COMMENT: {
                startNewToken(TokenID.TOK_COMMENT);
                break;
            }
            case TOK_WORD: {
                startNewToken(TokenID.TOK_WORD);
                this.cToken.append(c);
                break;
            }
            case TOK_EOF: {
                this.inputf.close();
                this.inputf = null;
                throwIfTokenDefined();
                return new Token(tokType, "EOF");
            }
            default: {
                // TOK_DIRECTIVE
                // TOK_NEWLINE
                // TOK_SPACE
                // TOK_COLON
                // TOK_SEMICOLON
                // TOK_BRACE_OPEN
                // TOK_BRACE_CLOSE
                throwIfTokenDefined();
                return new Token(tokType, c);
            }
        }

        return null;
    }

    /**
     * Generate a token ID from a provided character.
     * 
     * @param c The input character.
     * @return The token ID.
     */
    private TokenID getTokType(String c) {
        TokenID t = tokTable.get(c);

        // Probably just a number or character.
        if (t != null)
            return t;

        // Unrecognized word.
        if (!isCurrentToken(TokenID.TOK_COMMENT) && !isCurrentToken(TokenID.TOK_STRING) &&
            !c.matches("[A-Za-z0-9]+") && !c.equals("_"))
            throw new BadParserTokenException("Got bad char: " + c);
        return TokenID.TOK_WORD;
    }

    /**
     * Update the global state for parsing a new longer token.
     * 
     * @param tokType The new token type.
     */
    private void startNewToken(TokenID tokType) {
        throwIfTokenDefined();
        this.cToken = new TokenBuilder(tokType);
    }

    /**
     * Update the global state to end tokenizing a long token.
     * 
     * @return The generated token.
     */
    private Token endToken() {
        Token t = new Token(this.cToken.id, this.cToken.data);
        this.cToken = null;
        return t;
    }

    /**
     * Prevent continued tokenization on a new token before the last token is finished.
     */
    private void throwIfTokenDefined() {
        if (this.cToken != null)
            throw new IllegalStateException("New directive found before last one terminated!");
    }

    /**
     * Compare a requested token type against the currently processing token.
     * 
     * @param tok The token id to compare.
     * @return Whether the id is the current token.
     */
    private boolean isCurrentToken(TokenID tok) {
        return (this.cToken != null && this.cToken.id == tok);
    }

    /**
     * Read data from the input fie.
     * 
     * @param l How many characters to read.
     * @return The read data.
     */
    private String readSrc(int l) {
        try {
            return inputf.read(l);
        }
        catch (IOException ie) {
            this.inputf.close();
            this.inputf = null;
            return "";
        }
    }
}

/**
 * Can't parse a token properly or an unrecognized token.
 */
class BadParserTokenException extends RuntimeException {
    public BadParserTokenException(String message) {
        super(message);
    }
}
