package com.gearshiftinteractive.gearshiftscript.interpreter;

import com.gearshiftinteractive.gearshiftscript.Interpreter;
import com.gearshiftinteractive.gearshiftscript.Node;
import com.gearshiftinteractive.gearshiftscript.nodes.FunctionDeclaration;

import java.util.List;

public class FreeFunctionValue extends FunctionValue {

    private final FunctionDeclaration functionDeclaration;
    private final Scope scope;
    private final Interpreter interpreter;
    public final String fieldName;

    public FreeFunctionValue(FunctionDeclaration declaration, Scope declarationScope, Interpreter interpreter) {

        this.functionDeclaration = declaration;
        this.scope = declarationScope;
        this.interpreter = interpreter;

        if (declaration.name() != null) {
            var reference = (ScopeReference) interpreter.evalReference(declaration.name(), declarationScope);
            fieldName = reference.fieldName();
        } else {
            fieldName = declaration.file() + "@" + declaration.line();
        }
    }
    @Override
    protected Scope buildScope(List<GearshiftValue> args) {
        var innerScope = new Scope(scope);
        for (var i = 0; i < functionDeclaration.parameters().size(); i++) {
            var fieldName = ((ScopeReference) interpreter.evalReference(functionDeclaration.parameters().sequencedKeySet().toArray(Node[]::new)[i], innerScope)).fieldName();
            if (args.size() > i)
                innerScope.declare(fieldName, args.get(i));
            else
                innerScope.declare(fieldName, interpreter.evalValue(functionDeclaration.parameters().sequencedValues().toArray(Node[]::new)[i], innerScope));
        }
        return innerScope;
    }
    @Override
    public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
        return call(buildScope(args), args, file, line);
    }
    public GearshiftValue call(Scope scope, List<GearshiftValue> args, String file, int line) {
        checkArgs(fieldName, args, interpreter.requiredArgCount(functionDeclaration), functionDeclaration.parameters().size(), file, line);
        return interpreter.runFunction(scope, functionDeclaration.body(), file, line);
    }
}
