package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record IndexAccessExpression (
        Node accessFrom,
        Node accessWhat,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        accessFrom.printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
        accessWhat.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "IndexAccessExpression";
    }
}
