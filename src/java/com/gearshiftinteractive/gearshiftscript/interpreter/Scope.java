package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, GearshiftValue> values = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }
    public Scope() {
        this.parent = null;
    }
    public GearshiftValue get(String name, String file, int line) {
        if (values.containsKey(name)) return values.get(name);
        if (parent != null) return parent.get(name, file, line);
        throw new NameError("Undefined variable: " + name, file, line);
    }
    public void declare(String name, GearshiftValue value) {
        values.put(name, value);
    }
    public void assign(String name, GearshiftValue value, String file, int line) {
        if (values.containsKey(name)) {
            values.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value, file, line);
        } else {
            throw new NameError("Undefined variable: " + name, file, line);
        }
    }
    public void printOut(String file, int line) {
        for (var key : values.keySet()) {
            System.out.println(key + ": " + values.get(key).tojstring(file, line));
        }
        if (parent != null) { parent.printOut(file, line); }
    }
    public boolean contains(String name) {
        return values.containsKey(name) || (parent != null && parent.contains(name));
    }
}
