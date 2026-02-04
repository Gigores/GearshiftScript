package com.gearshiftinteractive.gearshiftscript.interpreter;

public record ScopeReference(Scope scope, String fieldName) implements Reference {
    @Override
    public GearshiftValue setValue(GearshiftValue value, String file, int line) {
        scope.assign(fieldName, value, file, line);
        return value;
    }
    @Override
    public GearshiftValue getValue(String file, int line) {
        return scope.get(fieldName, file, line);
    }
}
