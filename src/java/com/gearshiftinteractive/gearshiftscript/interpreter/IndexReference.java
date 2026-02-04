package com.gearshiftinteractive.gearshiftscript.interpreter;

import java.util.List;

public record IndexReference (
        GearshiftValue indexFrom,
        GearshiftValue indexWhat
) implements Reference {

    @Override
    public GearshiftValue setValue(GearshiftValue value, String file, int line) {
        indexFrom.accessField("__assign_index", file, line).call(List.of(indexWhat, value), file, line);
        return value;
    }

    @Override
    public GearshiftValue getValue(String file, int line) {
        return indexFrom.accessField("__get_index", file, line).call(List.of(indexWhat), file, line);
    }
}
