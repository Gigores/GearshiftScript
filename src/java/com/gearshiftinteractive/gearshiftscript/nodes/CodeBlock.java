package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record CodeBlock (
        Node[] statements,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        for (int i = 0; i < statements.length; i++) {
            statements[i].printOut(
                    furtherPrefix + ((i == (statements.length - 1) ? "└─ " : "├─ ")),
                    furtherPrefix + ((i == (statements.length - 1) ? "   " : "│  "))
            );
        }
    }
    @Override
    public String getType() {
        return "CodeBlock";
    }
}
