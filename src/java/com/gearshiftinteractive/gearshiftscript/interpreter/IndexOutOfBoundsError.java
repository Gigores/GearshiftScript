package com.gearshiftinteractive.gearshiftscript.interpreter;

public class IndexOutOfBoundsError extends GearshiftError {
    public IndexOutOfBoundsError(String message, String file, int line) {
        super(message, file, line);
    }
}
