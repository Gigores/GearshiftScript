package com.gearshiftinteractive.gearshiftscript.interpreter;

public class NameError extends GearshiftError {
    public NameError(String message, String file, int line) {
        super(message, file, line);
    }
}
