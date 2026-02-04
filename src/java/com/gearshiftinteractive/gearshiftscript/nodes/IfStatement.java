package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record IfStatement (
        Node[] conditions,
        CodeBlock[] codeBlocks,
        CodeBlock elseCodeBlock,
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType() + "/");
        for (var i = 0; i != codeBlocks.length; i++) {
            conditions[i].printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
            codeBlocks[i].printOut(furtherPrefix + "├─ ", furtherPrefix + "│  ");
        }
        elseCodeBlock.printOut(furtherPrefix + "└─ ", furtherPrefix + "   ");
    }
    @Override
    public String getType() {
        return "IfStatement";
    }
}
