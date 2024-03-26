package velocity.animation.parser.ops;

import java.util.ArrayList;

import velocity.animation.parser.*;

public class Param extends Directive {

    public Param(Token opcode, Token terminator, ArrayList<Token> args, int height) {
        super(opcode, terminator, args, height);
    }

    public boolean isBranch() { return true; }

    protected void verifyArgs() {
        int argCount = 2;
        TokenID[] argType = {TokenID.TOK_TYPE_CODE, TokenID.TOK_TYPE_DATA};

        verifyArgTypeAndCount(argCount, argType);
    }
}
