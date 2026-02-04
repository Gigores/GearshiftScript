package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;
import com.gearshiftinteractive.gearshiftscript.Token;

public record InfixOperatorExpression(
        Token operator,
        Node leftOperand,
        Node rightOperand,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        operator.printOut(furtherPrefix + "├─ ");
        leftOperand.printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
        rightOperand.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "BinaryOperatorExpression";
    }
}
