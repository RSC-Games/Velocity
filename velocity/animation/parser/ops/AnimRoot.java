package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

/** 
 * One of multiple different directives for parsing the animator tree files.
 * This represents the top of the file (denoted with @ANIM_ROOT).
 */
public class AnimRoot extends Directive {
    /**
     * Create the representation. 
     * 
     * @param opcode Opcode (should be @)
     * @param terminator Terminating character for the line (generally a ;)
     * @param args Args for the opcode (should be ANIM_ROOT).
     * @param height Nested height? For parsing nested code.
     */
    public AnimRoot(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        super(opcode, terminator, args, height);
    }

    /**
     * Determine whether this is a branch or not.
     * Non-branching directives are parsed differently.
     * 
     * @return Whether this is a branch or not.
     */
    @Override
    public boolean isBranch() { return false; }

    /**
     * Validate the args count and type.
     */
    @Override
    protected void verifyArgs() {
        int argCount = 0;
        TokenID[] argType = new TokenID[0];

        verifyArgTypeAndCount(argCount, argType);
    }
}
