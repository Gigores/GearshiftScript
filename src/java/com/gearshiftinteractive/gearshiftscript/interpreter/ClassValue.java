package com.gearshiftinteractive.gearshiftscript.interpreter;

import com.gearshiftinteractive.gearshiftscript.Interpreter;
import com.gearshiftinteractive.gearshiftscript.Node;
import com.gearshiftinteractive.gearshiftscript.TokenType;
import com.gearshiftinteractive.gearshiftscript.nodes.*;

import java.util.HashMap;
import java.util.List;

public class ClassValue extends GearshiftValue {

    public String name;
    public Scope declarationScope;
    public CodeBlock body;
    private Interpreter interpreter;

    public ClassValue(String name, Scope declarationScope, CodeBlock body, Interpreter interpreter) {
        this.name = name;
        this.declarationScope = declarationScope;
        this.body = body;
        this.interpreter = interpreter;
        declareFields();
    }
    private void declareFields() {
        declareField("getName", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                return new StringValue(name);
            }
        });
        declareField("__eq", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Class.__eq", args, 1, file, line);
                return new BooleanValue(name.equals(((ClassValue) args.getFirst()).name));
            }
        });
        for (var statement : body.statements()) {
            if (statement instanceof VariableDeclaration) {
                // ok
            } else if (statement instanceof FunctionDeclaration) {
                // ok
            } else if (statement instanceof PrefixOperatorExpression && ((PrefixOperatorExpression) statement).operator().type() == TokenType.STATIC) {
                declareField(((ScopeReference) interpreter.evalReference(((FunctionDeclaration) ((PrefixOperatorExpression) statement).operand()).name(), declarationScope)).fieldName(), new FreeFunctionValue((FunctionDeclaration) ((PrefixOperatorExpression) statement).operand(), declarationScope, interpreter));
            } else {
                throw new SyntaxError("You can't use " + statement.getType() + " inside struct declaration body", statement.file(), statement.line());
            }
        }
    }

    @Override
    public GearshiftValue instantiate(Scope scope, List<GearshiftValue> args, String file, int line) {
//        var targetFields = new HashMap<String, GearshiftValue>();
//        var innerScope = new Scope(scope);
//        for (var statement : body.statements()) {
//            if (statement instanceof VariableDeclaration) {
//                var expr = ((VariableDeclaration) statement).expr();
//                var res = interpreter.evalAssignment(expr, innerScope);
//                targetFields.put(((ScopeReference) res.getKey()).fieldName(), res.getValue());
//            } else {
//                throw new SyntaxError("You can't use " + statement.getType() + " inside struct declaration body", statement.file(), statement.line());
//            }
//        }
//        var parent = this;
//        var result = new GearshiftValue() {
//            public void declareFields() {
//                declareField("getClass", new FunctionValue() {
//                    @Override
//                    public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
//                        return parent;
//                    }
//                });
//                for (var fieldName : targetFields.keySet()) {
//                    declareField(fieldName, targetFields.get(fieldName));
//                }
//            }
//            @Override
//            public String getTypeName() {
//                return name;
//            }
//            @Override
//            public int hashCode() {
//                return 0;
//            }
//        };
//        result.declareFields();
//        return result;
        var objectScope = interpreter.getDefaultScope();
        objectScope.declare(name, this);
        return new ObjectValue(name, this, declarationScope, interpreter);
    }

    @Override
    public String getTypeName() {
        return "Struct";
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
