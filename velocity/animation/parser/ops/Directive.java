package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

public abstract class Directive {
    protected static ArrayList<TokenID> typeVals = new ArrayList<TokenID>();
    static boolean populated = false;

    protected ArrayList<Directive> children = new ArrayList<Directive>();
    protected Token opcode;
    protected Token terminator;
    protected ArrayList<Token> args;
    public final int height;
    
    static void populate() {
        if (populated) return;
        
        typeVals.add(TokenID.TOK_INT);
        typeVals.add(TokenID.TOK_FLOAT);
        typeVals.add(TokenID.TOK_STRING);
        typeVals.add(TokenID.TOK_BOOL);
        
        populated = true;
    }

    public Directive(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        this.opcode = opcode;
        this.terminator = terminator;
        this.args = args;
        this.height = height;
        populate();
        this.verifyArgs();
    }

    public void addChild(Directive d) {
        this.children.add(d);
    }

    public ArrayList<Token> getArgs() {
        return args;
    }

    public Token getOp() {
        return opcode;
    }

    public TokenID getID() {
        return this.opcode.tok;
    }

    public ArrayList<Directive> getChildren() {
        return children;
    }

    public boolean hasStateInfo() {
        for (Directive child : children) {
            if (child.opcode.tok == TokenID.TOK_OP_USE_TEX) return true;
        }
        return false;
    }

    public abstract boolean isBranch();

    protected abstract void verifyArgs();

    protected void verifyArgTypeAndCount(int argC, TokenID[] argT) {
        if (args.size() != argC)
            throw new InvalidArgsListException(opcode.data + " expects " + argC +
                " args, got " + args.size());

        for (int i = 0; i < argC; i++) {
            if (argT[i] == TokenID.TOK_TYPE_DATA && typeVals.contains(args.get(i).tok))
                continue;
            else if (argT[i] == args.get(i).tok)
                continue;
            throw new InvalidArgsListException(opcode.data + ": at position " + i + ", expected " 
                + argT[i] + ", got " + args.get(i).tok);
        }
    }

    public String toString() {
        String out = "Directive " + opcode.data + " args ";

        for (Token arg : args) {
            out += arg.data + " ";
        }

        out += ", children " + children.size() + "\n";

        String indent = "";

        for (int i = 0; i < height; i++) {
            indent += "  ";
        }

        for (Directive child : children) {
            out += indent + child.toString();
        }
        
        return out;
    }
}

class InvalidArgsListException extends RuntimeException {
    public InvalidArgsListException(String message) {
        super(message);
    }
}
