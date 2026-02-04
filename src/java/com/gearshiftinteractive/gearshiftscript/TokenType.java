package com.gearshiftinteractive.gearshiftscript;

import java.util.Optional;

public enum TokenType {
    // literals
    STRING_LITERAL,
    NUMBER_LITERAL,
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    IDENTIFIER,

    // operators
    ASSIGN("="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    POWER_ASSIGN("^="),
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    POWER("^"),
    DOT("."),
    RANGE(".."),
    INCREMENT("++"),
    DECREMENT("--"),
    EQUALS("=="),
    NOT_EQUALS("!="),
    LESS("<"),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<="),
    AND("and"),
    OR("or"),
    NOT("not"),
    STEP("step"),

    // punctuation
    EOL("\n"),
    EOF,
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    COMMA(","),
    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),
    COLON(":"),

    // keywords
    LET("let"),
    END("end"),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    CONTINUE("continue"),
    BREAK("break"),
    FUNCTION("function"),
    IS("is"),
    RETURN("return"),
    FOR("for"),
    IN("in"),
    STRUCT("struct"),
    NEW("new"),
    STATIC("static"),
    ;

    private final Optional<String> value;

    TokenType(String value) {
        this.value = Optional.of(value);
    }
    TokenType() {
        this.value = Optional.empty();
    }

    public Optional<String> getLiteral() {
        return value;
    }
}
