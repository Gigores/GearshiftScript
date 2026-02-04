package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record FunctionCall (
        Node callWhat,
        Node[] args,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        if (args.length > 0) {
            callWhat.printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
            for (int i = 0; i < args.length; i++) {
                args[i].printOut(
                        furtherPrefix + ((i == (args.length - 1) ? "└─ " : "├─ ")),
                        furtherPrefix + ((i == (args.length - 1) ? "   " : "│  "))
                );
            }
        } else {
            callWhat.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
        }
    }
    @Override
    public String getType() {
        return "FunctionCall";
    }
}
