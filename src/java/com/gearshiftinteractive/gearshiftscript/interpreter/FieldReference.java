package com.gearshiftinteractive.gearshiftscript.interpreter;

public record FieldReference(GearshiftValue owner, String fieldName) implements Reference {
    @Override
    public GearshiftValue setValue(GearshiftValue value, String file, int line) {
        owner.assignField(fieldName, value, file, line);
        return value;
    }
    @Override
    public GearshiftValue getValue(String file, int line) {
        return owner.accessField(fieldName, file, line);
    }
}
