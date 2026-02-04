package com.gearshiftinteractive.gearshiftscript.interpreter;

public class ControlFlowError extends GearshiftError {
    public ControlFlowError(String message, String file, int line) {
        super(message, file, line);
    }
}
