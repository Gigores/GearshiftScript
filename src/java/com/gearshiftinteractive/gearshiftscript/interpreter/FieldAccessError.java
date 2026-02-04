package com.gearshiftinteractive.gearshiftscript.interpreter;

public class FieldAccessError extends GearshiftError {
    protected FieldAccessError(String fieldName, String className, String file, int line) {
        super("There is no field \"" + fieldName + "\" in \"" + className + "\" type value.", file, line);
    }
}
