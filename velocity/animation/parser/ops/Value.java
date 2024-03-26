package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

public class Value extends Directive {

    public Value(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        super(opcode, terminator, args, height);
    }

    public boolean isBranch() { return true; }

    protected void verifyArgs() {
        int argCount = 1;
        TokenID[] argType = {TokenID.TOK_TYPE_DATA};

        verifyArgTypeAndCount(argCount, argType);
    }
}
