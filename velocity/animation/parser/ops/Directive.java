package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

/**
 * The abstract Directive. Represents most directive types.
 */
public abstract class Directive {
    /**
     * Allowed type values.
     */
    protected static ArrayList<TokenID> typeVals = new ArrayList<TokenID>();

    /**
     * Children directives of this one. Should be empty if the represented
     * directive doesn't branch.
     */
    protected ArrayList<Directive> children = new ArrayList<Directive>();

    /**
     * Operation code of this directive.
     */
    protected Token opcode;

    /**
     * The line terminator.
     */
    protected Token terminator;

    /**
     * Arguments associated with this directive.
     */
    protected ArrayList<Token> args;

    /**
     * The stack height of this directive. Higher values are higher up
     * the stack.
     */
    public final int height;
    
    /**
     * Populate the type values for allowed datatypes.
     */
    static void populate() {      
        typeVals.add(TokenID.TOK_INT);
        typeVals.add(TokenID.TOK_FLOAT);
        typeVals.add(TokenID.TOK_STRING);
        typeVals.add(TokenID.TOK_BOOL);
    }

    /**
     * Populate the type values with the required.
     */
    static {
        populate();
    }

    /**
     * Create a directive.
     * 
     * @param opcode Operation code.
     * @param terminator Line terminator.
     * @param args Other arguments for the directive.
     * @param height Stack height of this directive.
     */
    public Directive(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        this.opcode = opcode;
        this.terminator = terminator;
        this.args = args;
        this.height = height;
        populate();
        this.verifyArgs();
    }

    /**
     * Identify whether this directive is a branch.
     * 
     * @return Whether it's a branch or not.
     */
    public abstract boolean isBranch();

    /**
     * Verify the arguments list passed into this object and validate
     * all types. An exception will be thrown in the event of a failure.
     */
    protected abstract void verifyArgs();

    /**
     * Add a child directive to this one (for branched directives)
     * 
     * @param d The directive to add.
     */
    public void addChild(Directive d) {
        this.children.add(d);
    }

    /**
     * Get the arguments associated with this directive.
     * 
     * @return The arguments list.
     */
    public ArrayList<Token> getArgs() {
        return args;
    }

    /**
     * Get this opcode.
     * 
     * @return The opcode.
     */
    public Token getOp() {
        return opcode;
    }

    /**
     * Get this directive's TokenID.
     * 
     * @return The TokenID.
     */
    public TokenID getID() {
        return this.opcode.tok;
    }

    /**
     * Get the child directives of this directive.
     * 
     * @return The list of child directives.
     */
    public ArrayList<Directive> getChildren() {
        return children;
    }

    /**
     * Identify whether any of the immediate children contain anim state
     * like loading textures and using them.
     * 
     * @return Whether any state information is present in the immediate
     * children.
     */
    public boolean hasStateInfo() {
        for (Directive child : children) {
            if (child.opcode.tok == TokenID.TOK_OP_USE_TEX) return true;
        }
        return false;
    }

    /**
     * Validate all args and parameters.
     * 
     * @param argC Argument count.
     * @param argT Argument types (as an array of TokenIDs).
     */
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

    /**
     * Output this object's string representation. Useful for debugging
     * and unnecessary for normal use.
     * 
     * @return The String representation.
     */
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

/**
 * Thrown when an arguments list does not match what was supplied.
 */
class InvalidArgsListException extends RuntimeException {
    /**
     * Create the exception to be thrown.
     * 
     * @param message The error message.
     */
    public InvalidArgsListException(String message) {
        super(message);
    }
}
