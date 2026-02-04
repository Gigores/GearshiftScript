package com.gearshiftinteractive.gearshiftscript;

public interface Node {
    String file();
    int line();
    void printOut(String prefix, String furtherPrefix);
    String getType();
}
