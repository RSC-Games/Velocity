package velocity.animation.parser;

import java.util.Stack;

import velocity.animation.parser.ops.*;

import java.util.ArrayList;
import java.util.HashMap;

// The AST builds a true syntax tree as well as reads
// the provided syntax. By far it has the most powerful
// error detection.
public class AFParserAST {
    static HashMap<String, TokenID> ops = new HashMap<String, TokenID>();
    static boolean populated = false;

    AFParserCST pp;
    Stack<Directive> wstack = new Stack<Directive>();
    AnimRoot root;

    public static void populate() {
        if (populated) return;

        ops.put("ANIM_ROOT", TokenID.TOK_OP_ANIM_ROOT);
        ops.put("FRAMES_PER_UPDATE", TokenID.TOK_OP_FRAMES_PER_UPDATE);
        ops.put("PARAM", TokenID.TOK_OP_PARAM);
        ops.put("VALUE", TokenID.TOK_OP_VALUE);
        ops.put("USE_TEX", TokenID.TOK_OP_USE_TEX);

        populated = true;
    }

    public AFParserAST(AFParserCST preparser) {
        this.pp = preparser;
        populate();
    }

    public AnimRoot buildTree() {
        AnimRoot base = null;

        // Build the stack and accumulate the AST as we get nodes.
        // NOT YET IMPLEMENTED!
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

    // Assumes beginning of line.
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

            // Need to refactor at some point so this isn't necessary for linting.
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

    private boolean lineIsValidOrCloser() {
        Token t = pp.parseNextToken();

        if (t.tok == TokenID.TOK_BRACE_CLOSE || t.tok == TokenID.TOK_EOF)
            return true;

        if (t.tok != TokenID.TOK_DIRECTIVE)
            throw new InvalidLineException("At line <>, col 0: Expected @, got " + t.data);
        
        return false;
    }

    private Token getDirective() {
        Token t = pp.parseNextToken();

        if (t.tok != TokenID.TOK_SYMBOL)
            throw new InvalidLineException("At line <>, col 1: Expected <SYMBOL>, got " + t.data);

        return new Token(ops.get(t.data), t.data);
    }

    private Token getBrace(TokenID type) {
        Token t = pp.parseNextToken();

        if (t.tok != type)
            throw new InvalidLineException("At line <>: Expected {, got " + t.data);

        return t;
    }
}

class InvalidLineException extends RuntimeException {
    public InvalidLineException(String message) {
        super(message);
    }
}
