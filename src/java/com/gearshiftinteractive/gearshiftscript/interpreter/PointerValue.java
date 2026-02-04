package com.gearshiftinteractive.gearshiftscript.interpreter;

public interface PointerValue {
    void set(GearshiftValue value);
    GearshiftValue get();
}
