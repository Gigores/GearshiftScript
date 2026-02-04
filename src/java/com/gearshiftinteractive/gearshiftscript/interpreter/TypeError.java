package com.gearshiftinteractive.gearshiftscript.interpreter;

public class TypeError extends GearshiftError {
    public TypeError(String message, String file, int line) {
        super(message, file, line);
    }
}
