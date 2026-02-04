package com.gearshiftinteractive.gearshiftscript.interpreter;

import com.gearshiftinteractive.gearshiftscript.Interpreter;
import com.gearshiftinteractive.gearshiftscript.nodes.FunctionDeclaration;

import java.util.List;

public class MethodValue extends FreeFunctionValue {
    private final GearshiftValue receiver;
    public MethodValue(GearshiftValue receiver, FunctionDeclaration declaration, Scope declarationScope, Interpreter interpreter) {
        super(declaration, declarationScope, interpreter);
        this.receiver = receiver;
    }

    @Override
    protected Scope buildScope(List<GearshiftValue> args) {
        var scope = super.buildScope(args);
        scope.declare("this", receiver);
        return scope;
    }
}
