package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;
import com.gearshiftinteractive.gearshiftscript.Token;

public record PostfixOperatorExpression (
        Token operator,
        Node operand,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        operator.printOut(furtherPrefix + "├─ ");
        operand.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "PostfixOperatorExpression";
    }
}
