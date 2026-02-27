package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record VariableDeclaration (
        Node expr,
        boolean isConstant,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "." + (isConstant ? "let" : "const") + "/");
        expr.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "VariableDeclaration";
    }
}
