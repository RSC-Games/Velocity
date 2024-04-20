package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

/** 
 * One of multiple different directives for parsing the animator tree files.
 * This represents the frame count between each successive image.
 */
public class FramesPerUpdate extends Directive {
    /**
     * Create the representation. 
     * 
     * @param opcode Opcode (should be @)
     * @param terminator Terminating character for the line (generally a ;)
     * @param args Args for the opcode (should be FRAMES_PER_UPDATE).
     * @param height Nested height? For parsing nested code.
     */
    public FramesPerUpdate(Token opcode, Token terminator, ArrayList<Token> args, int height) {
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
        int argCount = 1;
        TokenID[] argType = {TokenID.TOK_TYPE_DATA};

        verifyArgTypeAndCount(argCount, argType);
    }
}
