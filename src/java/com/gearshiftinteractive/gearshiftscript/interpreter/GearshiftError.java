package com.gearshiftinteractive.gearshiftscript.interpreter;

public abstract class GearshiftError extends RuntimeException {

    protected GearshiftError(String message, String file, int line) {
        super(file + " line " + line + ": " + message);
    }
}
