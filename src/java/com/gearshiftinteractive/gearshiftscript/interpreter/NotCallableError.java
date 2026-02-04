package com.gearshiftinteractive.gearshiftscript.interpreter;

public class NotCallableError extends GearshiftError {
    public NotCallableError(String className, String file, int line) {
        super(className + " is not callable", file, line);
    }
}
