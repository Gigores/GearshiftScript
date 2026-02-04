package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;
import com.gearshiftinteractive.gearshiftscript.Token;

public record ExpressionAtom (
        Token identifier,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        identifier.printOut(furtherPrefix + "└─ ");
    }
    @Override
    public String getType() {
        return "ExpressionAtom";
    }
}
