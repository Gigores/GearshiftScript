package com.gearshiftinteractive.gearshiftscript.nodes;

import com.gearshiftinteractive.gearshiftscript.Node;

public record Continue (
        String file,
        int line
) implements Node {
    @Override
    public void printOut(String prefix, String furtherPrefix) {
        System.out.println(prefix + getType());
    }
    @Override
    public String getType() {
        return "Continue";
    }
}
