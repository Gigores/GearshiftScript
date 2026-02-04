package com.gearshiftinteractive.gearshiftscript.interpreter;

public class CallArgumentError extends GearshiftError {
    public CallArgumentError(String functionName, int expectedArgs, int gottenArgs, String file, int line) {
        super("Function " + functionName + "() requires " + expectedArgs + " arguments, but " + gottenArgs + " were given", file, line);
    }
    public CallArgumentError(String functionName, int min, int max, int gottenArgs, String file, int line) {
        super("Function " + functionName + "() requires " + min + " to " + max + " arguments, but " + gottenArgs + " were given", file, line);
    }
}
