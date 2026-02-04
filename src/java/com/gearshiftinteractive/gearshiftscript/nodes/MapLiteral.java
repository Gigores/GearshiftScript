package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

import java.util.List;
import java.util.Map;

public record MapLiteral(
        List<Map.Entry<Node, Node>> elements,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).getKey().printOut(
                    furtherPrefix + "├─ ",
                    furtherPrefix + "│  "
            );
            elements.get(i).getValue().printOut(
                    furtherPrefix + ((i == (elements.size() - 1) ? "└─ " : "├─ ")),
                    furtherPrefix + ((i == (elements.size() - 1) ? "   " : "│  "))
            );
        }
    }
    @Override
    public String getType() {
        return "MapLiteral";
    }
}
