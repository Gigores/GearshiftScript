package com.gearshiftinteractive.gearshiftscript;

public record Token(
        TokenType type,
        String content,
        int lineNumber,
        String file
) {
    @Override
    public String toString() {
        return "Token(" + type.name() + ", [" + content + "])";
    }
    public void printOut(String prefix) {
        System.out.println(prefix + this);
    }
}
