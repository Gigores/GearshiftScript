package com.gearshiftinteractive.gearshiftscript.interpreter;

public class NotInstantiableError extends GearshiftError {
    public NotInstantiableError(String className, String file, int line) {
        super("Object of type " + className + " cannot be instantiated", file, line);
    }
}
