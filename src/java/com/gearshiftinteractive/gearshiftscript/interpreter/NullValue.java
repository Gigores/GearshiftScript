package com.gearshiftinteractive.gearshiftscript.interpreter;

import javax.lang.model.type.NullType;
import java.util.List;

public class NullValue extends GearshiftValue {

    public NullValue() {
        declareField("__tostring", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Null.__tostring", args, 0, file, line);
                return new StringValue("null");
            }
        });
    }

    @Override
    public String getTypeName() {
        return "Null";
    }
    @Override
    public int hashCode() {
        return 0;
    }
}
