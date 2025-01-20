package velocity.animation.parser;

import java.util.Stack;

import velocity.animation.parser.ops.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The AST builds a true syntax tree as well as reads the provided syntax. By far 
 * it has the most powerful error detection.
 */
class AFParserAST {
    /**
     * Parsable operations.
     */
    static HashMap<String, TokenID> ops = new HashMap<String, TokenID>();

    /**
     * The previous cst parser.
     */
    AFParserCST pp;

    /**
     * The current stack of directives.
     */
    Stack<Directive> wstack = new Stack<Directive>();

    /**
     * The root directive.
     */
    AnimRoot root;

    /**
     * Populate the tokens for parsing the grammar.
     */
    static void populate() {
        ops.put("ANIM_ROOT", TokenID.TOK_OP_ANIM_ROOT);
        ops.put("FRAMES_PER_UPDATE", TokenID.TOK_OP_FRAMES_PER_UPDATE);
        ops.put("PARAM", TokenID.TOK_OP_PARAM);
        ops.put("VALUE", TokenID.TOK_OP_VALUE);
        ops.put("USE_TEX", TokenID.TOK_OP_USE_TEX);
        ops.put("ONE_SHOT", TokenID.TOK_OP_ONE_SHOT);
    }

    /**
     * Initialize the parser.
     */
    static {
        populate();
    }

    /**
     * Create a new AST parser.
     * 
     * @param preparser The previous CST parser.
     */
    public AFParserAST(AFParserCST preparser) {
        this.pp = preparser;
    }

    /**
     * Parse the AST from the previous CST.
     * 
     * @return The parsed CST.
     */
    public AnimRoot buildTree() {
        AnimRoot base = null;

        // Build the stack and accumulate the AST as we get nodes.
        while (pp.available()) {
            Directive d = getLogicalLine();

            // Null is only seen when a closing bracket is found.
            if (d == null) {
                if (wstack.size() == 0) 
                    throw new InvalidLineException("At line <>, col 0: Too many closing braces.");

                wstack.pop();
                continue;
            }

            else if (d.getID() == TokenID.TOK_OP_ANIM_ROOT) {
                if (wstack.size() != 0)
                    throw new InvalidLineException("At line <>, col 0: Invalid statement: ANIM_ROOT");

                wstack.push(d);
                base = (AnimRoot)d;
                continue;
            }

            Directive current = wstack.peek();
            current.addChild(d);

            if (d.isBranch())
                wstack.push(d);
        }

        if (wstack.size() > 0)
            throw new InvalidLineException("Parsed EOF before end of directive.");
        
        this.root = base;
        return base;
    }

    /**
     * Assumes the beginning of a line. Parses out an entire line.
     * 
     * @return The logical line.
     */
    private Directive getLogicalLine() {
        ArrayList<Token> line = new ArrayList<Token>();

        // Only runs if a closer is detected.
        // If the line is not valid otherwise an exception is thrown.
        if (lineIsValidOrCloser())
            return null;

        Token op = getDirective();
        Token term = null;

        // Read up to the end of the line.
        // Line terminators are either colons or semicolons.
        Token ct = null;
        while (true) {
            ct = pp.parseNextToken();

            TokenID terminator = (op.tok == TokenID.TOK_OP_PARAM || 
                op.tok == TokenID.TOK_OP_VALUE) ? TokenID.TOK_COLON : TokenID.TOK_SEMICOLON;

            if (ct.tok == TokenID.TOK_COLON || ct.tok == TokenID.TOK_SEMICOLON) {
                if (ct.tok != terminator) 
                    throw new InvalidLineException("At line <>: Expected " + terminator + ", got " + ct.data);
                term = ct;
                break;
            }

            if (ct.tok == TokenID.TOK_DIRECTIVE)
                throw new InvalidLineException("At line <>: Expected terminator, got " + ct.data);

            line.add(ct);
        }

        /*
        for (Token tok : line) {
            System.out.println(tok);
        }
        */

        Directive d = getDirectiveObject(op, term, line);

        // Parsing's not finished yet if this is a multilined statement.
        if (d.isBranch())
            getBrace(TokenID.TOK_BRACE_OPEN);
        
        return d;
    }

    /**
     * Generate a directive corresponding to the parsed data.
     * 
     * @param op The opcode.
     * @param term The line terminator.
     * @param args Arguments of the directive.
     * @return The generated directive.
     */
    private Directive getDirectiveObject(Token op, Token term, ArrayList<Token> args) {
        switch (op.tok) {
            // This is all before a new node is pushed on the stack so
            // the add is implicit.
            case TOK_OP_ANIM_ROOT:
                return new AnimRoot(op, term, args, wstack.size());
            case TOK_OP_FRAMES_PER_UPDATE:
                return new FramesPerUpdate(op, term, args, wstack.size());
            case TOK_OP_PARAM:
                return new Param(op, term, args, wstack.size());
            case TOK_OP_VALUE:
                return new Value(op, term, args, wstack.size());
            case TOK_OP_USE_TEX:
                return new UseTex(op, term, args, wstack.size());
            case TOK_OP_ONE_SHOT:
                return new OneShot(op, term, args, wstack.size());

            // Bad code: Linter got mad about this. Create a different ENUM at some point.
            case TOK_BOOL: {}
            case TOK_BRACE_CLOSE: {}
            case TOK_BRACE_OPEN: {}
            case TOK_COLON: {}
            case TOK_COMMENT: {}
            case TOK_DIRECTIVE: {}
            case TOK_EOF: {}
            case TOK_FLOAT: {}
            case TOK_INT: {}
            case TOK_NEWLINE: {}
            case TOK_QUOTE: {}
            case TOK_SEMICOLON: {}
            case TOK_SPACE: {}
            case TOK_STRING: {}
            case TOK_SYMBOL: {}
            case TOK_TYPE_CODE: {}
            case TOK_TYPE_DATA: {}
            case TOK_WORD: {}
        }
        return null;
    }

    /**
     * Validate a line and ensure it's not incorrectly parsed or incorrectly
     * written.
     * 
     * @return Whether the line is valid or is a closer.
     */
    private boolean lineIsValidOrCloser() {
        Token t = pp.parseNextToken();

        if (t.tok == TokenID.TOK_BRACE_CLOSE || t.tok == TokenID.TOK_EOF)
            return true;

        if (t.tok != TokenID.TOK_DIRECTIVE)
            throw new InvalidLineException("At line <>, col 0: Expected @, got " + t.data);
        
        return false;
    }

    /**
     * Try to parse a directive out of the file.
     * 
     * @return A directive, if any.
     */
    private Token getDirective() {
        Token t = pp.parseNextToken();

        if (t.tok != TokenID.TOK_SYMBOL)
            throw new InvalidLineException("At line <>, col 1: Expected <SYMBOL>, got " + t.data);

        return new Token(ops.get(t.data), t.data);
    }

    /**
     * Attempt to parse a curly bracket out of the line for a multi-line statement.
     * 
     * @param type Token type.
     * @return The parsed token.
     */
    private Token getBrace(TokenID type) {
        Token t = pp.parseNextToken();

        if (t.tok != type)
            throw new InvalidLineException("At line <>: Expected {, got " + t.data);

        return t;
    }
}

/**
 * Bad line representation.
 */
class InvalidLineException extends RuntimeException {
    /**
     * Cannot parse a line.
     * 
     * @param message The error message.
     */
    public InvalidLineException(String message) {
        super(message);
    }
}
