package com.gearshiftinteractive.gearshiftscript.interpreter;

import com.gearshiftinteractive.gearshiftscript.Interpreter;
import com.gearshiftinteractive.gearshiftscript.TokenType;
import com.gearshiftinteractive.gearshiftscript.nodes.FunctionDeclaration;
import com.gearshiftinteractive.gearshiftscript.nodes.PrefixOperatorExpression;
import com.gearshiftinteractive.gearshiftscript.nodes.VariableDeclaration;

import java.util.HashMap;
import java.util.List;

public class ObjectValue extends GearshiftValue {

    private final String className;
    private final ClassValue classValue;

    public ObjectValue(String className, ClassValue classValue, Scope scope, Interpreter interpreter) {
        this.className = className;
        this.classValue = classValue;
        declareFields(scope, interpreter);
    }
    public void declareFields(Scope scope, Interpreter interpreter) {
        var targetFields = new HashMap<String, GearshiftValue>();
        var innerScope = new Scope(scope);
        for (var statement : classValue.body.statements()) {
            if (statement instanceof VariableDeclaration) {
                var expr = ((VariableDeclaration) statement).expr();
                var res = interpreter.evalAssignment(expr, innerScope);
                targetFields.put(((ScopeReference) res.getKey()).fieldName(), res.getValue());
            } else if (statement instanceof FunctionDeclaration) {
                targetFields.put(((ScopeReference) interpreter.evalReference(((FunctionDeclaration) statement).name(), scope)).fieldName(), new MethodValue(this, (FunctionDeclaration) statement, innerScope, interpreter));
            } else if (statement instanceof PrefixOperatorExpression && ((PrefixOperatorExpression) statement).operator().type() == TokenType.STATIC) {
                // thats fine
            } else {
                throw new SyntaxError("You can't use " + statement.getType() + " inside struct declaration body", statement.file(), statement.line());
            }
        }
        declareField("getClass", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                return classValue;
            }
        });
        for (var fieldName : targetFields.keySet()) {
            declareField(fieldName, targetFields.get(fieldName));
        }
    }
    @Override
    public String getTypeName() {
        return className;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
