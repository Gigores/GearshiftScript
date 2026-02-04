package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

import java.util.LinkedHashMap;

public record FunctionDeclaration (
        Node name,
        LinkedHashMap<Node, Node> parameters,
        Node body,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        if (name != null)
            name.printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
        for (var i : parameters.sequencedKeySet()) {
            i.printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
            if (parameters.get(i) != null)
                parameters.get(i).printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
        }
        body.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "FunctionDeclaration";
    }
}
