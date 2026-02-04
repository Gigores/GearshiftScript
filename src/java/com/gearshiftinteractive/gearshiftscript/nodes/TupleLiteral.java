package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

import java.util.List;

public record TupleLiteral(
        List<Node> elements,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).printOut(
                    furtherPrefix + ((i == (elements.size() - 1) ? "└─ " : "├─ ")),
                    furtherPrefix + ((i == (elements.size() - 1) ? "   " : "│  "))
            );
        }
    }
    @Override
    public String getType() {
        return "TupleLiteral";
    }
}
