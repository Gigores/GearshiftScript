package com.gearshiftinteractive.gearshiftscript.interpreter;

public class SyntaxError extends GearshiftError {
    public SyntaxError(String message, String file, int line) {
        super(message, file, line);
    }
}
