package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

public class AnimRoot extends Directive {

    public AnimRoot(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        super(opcode, terminator, args, height);
    }

    public boolean isBranch() { return false; }

    protected void verifyArgs() {
        int argCount = 0;
        TokenID[] argType = new TokenID[0];

        verifyArgTypeAndCount(argCount, argType);
    }
}
