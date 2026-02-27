package com.gearshiftinteractive.gearshiftscript.interpreter;

public class ConstantFieldError extends GearshiftError {
    protected ConstantFieldError(String fieldName, String file, int line) {
        super("Field \"" + fieldName + "\" is constant and cannot be reassigned", file, line);
    }
}
