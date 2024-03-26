package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

public class FramesPerUpdate extends Directive {

    public FramesPerUpdate(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        super(opcode, terminator, args, height);
    }

    public boolean isBranch() { return false; }

    protected void verifyArgs() {
        int argCount = 1;
        TokenID[] argType = {TokenID.TOK_TYPE_DATA};

        verifyArgTypeAndCount(argCount, argType);
    }
}
