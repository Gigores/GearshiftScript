package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.HashMap;
import java.util.Map;

record ScopeValue (GearshiftValue value, boolean isConstant) { }

public class Scope {
    private final Scope parent;
    private final Map<String, ScopeValue> values = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }
    public Scope() {
        this.parent = null;
    }
    public GearshiftValue get(String name, String file, int line) {
        if (values.containsKey(name)) return values.get(name).value();
        if (parent != null) return parent.get(name, file, line);
        throw new NameError("Undefined variable: " + name, file, line);
    }
    public void declare(String name, GearshiftValue value, boolean isConstant) {
        values.put(name, new ScopeValue(value, isConstant));
    }
    public void assign(String name, GearshiftValue value, String file, int line) {
        if (values.containsKey(name)) {
            if (values.get(name).isConstant())
                throw new ConstantFieldError(name, file, line);
            values.put(name, new ScopeValue(value, false));
        } else if (parent != null) {
            parent.assign(name, value, file, line);
        } else {
            throw new NameError("Undefined variable: " + name, file, line);
        }
    }
    public void forceAssign(String name, GearshiftValue value, boolean isConstant) {
        values.put(name, new ScopeValue(value, isConstant));
    }
    public void printOut(String file, int line) {
        for (var key : values.keySet()) {
            System.out.println(key + ": " + values.get(key).value().tojstring(file, line));
        }
        if (parent != null) { parent.printOut(file, line); }
    }
    public boolean contains(String name) {
        return values.containsKey(name) || (parent != null && parent.contains(name));
    }
}
