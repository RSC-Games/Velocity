package velocity.animation.parser;

import java.util.ArrayList;

// Takes direct lexer output and builds a concrete syntax tree.
// It's not quite a tree yet, as that is the ASTParser's job.
// This just does type detection and converts some word tokens
// to type codes.
public class AFParserCST {
    AFTokenizer lexer;
    static ArrayList<TokenID> whitespace = new ArrayList<TokenID>();
    static ArrayList<String> typeCodes = new ArrayList<String>();
    static ArrayList<String> directives = new ArrayList<String>();
    static boolean populated = false;

    boolean endOfFile = false;

    public AFParserCST(AFTokenizer lexer) {
        this.lexer = lexer;
        populate();
    }

    static void populate() {
        if (populated) return;

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

        populated = true;
    }

    public boolean available() {
        return !endOfFile;
    }

    public Token parseNextToken() {
        if (this.endOfFile) return null;

        Token tToProcess = nextLexerToken();
        return parseToken(tToProcess);
    }

    private Token nextLexerToken() {
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

    // Some tokens will be whitespace and can be trimmed.
    // The whitespace is important mainly for the tokenizer.
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

    private Token tokenFromID(TokenID t, Token tok) {
        return new Token(t, tok.data);
    }

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

    private boolean isInt(String d) {
        try {
            Integer.parseInt(d);
            return true;
        }
        catch (NumberFormatException ie) {
            return false;
        }
    }

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
