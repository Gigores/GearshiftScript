package com.gearshiftinteractive.gearshiftscript.interpreter;

public interface Reference {
    GearshiftValue setValue(GearshiftValue value, String file, int line);
    GearshiftValue getValue(String file, int line);
}
