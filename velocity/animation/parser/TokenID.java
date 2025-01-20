package velocity.animation.parser;

/**
 * Represents many different token types and opcodes.
 * Written badly; should be split into multiple ENUMs.
 */
public enum TokenID {
    // Syntax tokens.
    TOK_DIRECTIVE,
    TOK_COMMENT,  // Whitespace
    TOK_NEWLINE,  // Whitespace
    TOK_SPACE,  // Whitespace
    TOK_COLON,  // Line terminator.
    TOK_WORD,  // CST Parser type detects these only.
    TOK_SEMICOLON,  // Line terminator.
    TOK_BRACE_OPEN,
    TOK_BRACE_CLOSE,
    TOK_QUOTE,  // Never makes it out of the lexer.
    TOK_STRING,
    TOK_EOF,

    // Type tokens (used by the type detection system.)
    TOK_TYPE_CODE,
    TOK_TYPE_DATA,
    TOK_INT,
    TOK_FLOAT,
    TOK_BOOL,
    // TOK_STRING already defined
    TOK_SYMBOL,

    // More parser tokens (for commands and ops)
    // May be used in the AST.
    TOK_OP_ANIM_ROOT,
    TOK_OP_FRAMES_PER_UPDATE,
    TOK_OP_PARAM,
    TOK_OP_VALUE,
    TOK_OP_USE_TEX,
    TOK_OP_ONE_SHOT
}
