package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GearshiftValue {
    protected Map<String, GearshiftValue> fields = new HashMap<>();
    public GearshiftValue accessField(String name, String file, int line) {
        if (fields.containsKey(name)) {
            return fields.get(name);
        } else {
            throw new FieldAccessError(name, getTypeName(), file, line);
        }
    }
    public void assignField(String name, GearshiftValue value, String file, int line) {
        if (!fields.containsKey(name)) throw new FieldAccessError(name, getTypeName(), file, line);
        fields.put(name, value);
    }
    public boolean hasField(String name) {
        return fields.containsKey(name);
    }
    protected void declareField(String name, GearshiftValue value) {
        fields.put(name, value);
    }
    public GearshiftValue getIndex(GearshiftValue index, String file, int line) {
        throw new NotIndexableError(getTypeName(), file, line);
    }

    public void setIndex(GearshiftValue index, GearshiftValue value, String file, int line) {
        throw new NotIndexableError(getTypeName(), file, line);
    }

    public abstract String getTypeName();

    public GearshiftValue call(List<GearshiftValue> args, String file, int line) { throw new NotCallableError(getTypeName(), file, line); }
    public final GearshiftValue toStringValue(String file, int line) {
        if (fields.containsKey("__tostring")) {
            return accessField("__tostring", file, line).call(List.of(), file, line);
        } else {
            return new StringValue(toString());
        }
    }
    public String tojstring(String file, int line) {
        if (fields.containsKey("__tostring")) {
            return ((StringValue) toStringValue(file, line)).tojstring(file, line);
        } else {
            return toString();
        }
    }
    public GearshiftValue instantiate(Scope scope, List<GearshiftValue> args, String file, int line) { throw new NotInstantiableError(getTypeName(), file, line); }

    @Override
    public abstract int hashCode();
}
