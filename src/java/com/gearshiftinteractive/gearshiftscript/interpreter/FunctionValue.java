package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public abstract class FunctionValue extends GearshiftValue {

    @Override
    public String getTypeName() {
        return "Function";
    }
    public abstract GearshiftValue call(List<GearshiftValue> args, String file, int line);
    protected Scope buildScope(List<GearshiftValue> args) { return new Scope(); }

    protected void checkArgs(String functionName, List<GearshiftValue> args, int expected, String file, int line) {
        if (args.size() != expected) throw new CallArgumentError(functionName, expected, args.size(), file, line);
    }
    protected void checkArgs(String functionName, List<GearshiftValue> args, int min, int max, String file, int line) {
        if (args.size() < min || args.size() > max) throw new CallArgumentError(functionName, min, max, args.size(), file, line);
    }
    @Override
    public int hashCode() {
        return 1225;
    }
}
