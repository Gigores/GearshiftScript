package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public class BooleanValue extends GearshiftValue {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
        declareField("__tostring", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Boolean.__tostring", args, 0, file, line);
                return new StringValue(Boolean.toString(value));
            }
        });
    }

    public boolean checkBoolean() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "Boolean";
    }
    public boolean getValue() {
        return value;
    }
    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }
}
