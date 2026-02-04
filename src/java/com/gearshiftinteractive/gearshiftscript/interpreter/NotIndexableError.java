package com.gearshiftinteractive.gearshiftscript.interpreter;

public class NotIndexableError extends GearshiftError {
    public NotIndexableError(String className, String file, int line) {
        super(className + " is not indexable", file, line);
    }
}
