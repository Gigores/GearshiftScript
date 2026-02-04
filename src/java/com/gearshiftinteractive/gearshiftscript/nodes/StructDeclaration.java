package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record StructDeclaration (
        Node name,
        Node body,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        name.printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
        body.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "StructDeclaration";
    }
}
