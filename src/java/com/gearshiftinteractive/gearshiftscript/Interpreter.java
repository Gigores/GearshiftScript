package com.gearshiftinteractive.gearshiftscript;

import com.gearshiftinteractive.gearshiftscript.interpreter.*;
import com.gearshiftinteractive.gearshiftscript.interpreter.ClassValue;
import com.gearshiftinteractive.gearshiftscript.nodes.*;
import org.w3c.dom.ranges.Range;

import java.lang.reflect.Array;
import java.util.*;

sealed interface ExecResult permits Normal, Break, Continue, Return { }

record Normal(GearshiftValue value) implements ExecResult {}
record Break() implements ExecResult {}
record Continue() implements ExecResult {}
record Return(GearshiftValue value) implements ExecResult {}

public class Interpreter {
    private Normal evalExpressionAtom(ExpressionAtom node, Scope scope) {
        return new Normal(switch (node.identifier().type()) {
            case IDENTIFIER -> scope.get(node.identifier().content(), node.file(), node.line());
            case NUMBER_LITERAL -> new NumberValue(Double.parseDouble(node.identifier().content()));
            case STRING_LITERAL -> new StringValue(node.identifier().content());
            case TRUE -> new BooleanValue(true);
            case FALSE -> new BooleanValue(false);
            case NULL -> new NullValue();
            default -> throw new SyntaxError("Invalid token for an expression atom", node.file(), node.line());
        });
    }
    private String evalIdentifier(Node node) {
        if (!(node instanceof ExpressionAtom)) throw new SyntaxError("Expected identifier", node.file(), node.line());
        return ((ExpressionAtom) node).identifier().content();
    }
    private Normal evalInfixOperatorExpression(InfixOperatorExpression node, Scope scope) {
        return new Normal(switch (node.operator().type()) {

            case ASSIGN -> set(node, scope, evalValue(node.rightOperand(), scope));

            case ADD_ASSIGN -> compoundAssign(node, scope, "__add");
            case SUB_ASSIGN -> compoundAssign(node, scope, "__sub");
            case MUL_ASSIGN -> compoundAssign(node, scope, "__mul");
            case DIV_ASSIGN -> compoundAssign(node, scope, "__div");
            case POWER_ASSIGN -> compoundAssign(node, scope, "__pow");

            case DOT -> evalValue(node.leftOperand(), scope)
                    .accessField(evalIdentifier(node.rightOperand()), node.file(), node.line());

            case ADD -> binary(node, scope, "__add");
            case SUBTRACT -> binary(node, scope, "__sub");
            case MULTIPLY -> binary(node, scope, "__mul");
            case DIVIDE -> binary(node, scope, "__div");
            case POWER -> binary(node, scope, "__pow");

            case GREATER -> binary(node, scope, "__gt");
            case LESS -> binary(node, scope, "__ls");
            case EQUALS -> binary(node, scope, "__eq");

            case GREATER_OR_EQUAL -> or(
                    binaryBool(node, scope, "__gt"),
                    binaryBool(node, scope, "__eq")
            );

            case LESS_OR_EQUAL -> or(
                    binaryBool(node, scope, "__ls"),
                    binaryBool(node, scope, "__eq")
            );

            case NOT_EQUALS -> new BooleanValue(
                    !binaryBool(node, scope, "__eq").getValue()
            );

            case AND -> new BooleanValue(
                    bool(node.leftOperand(), scope) && bool(node.rightOperand(), scope)
            );

            case OR -> new BooleanValue(
                    bool(node.leftOperand(), scope) || bool(node.rightOperand(), scope)
            );

            case RANGE -> new RangeValue(
                    (int)((NumberValue) evalValue(node.leftOperand(), scope)).getValue(),
                    (int)((NumberValue) evalValue(node.rightOperand(), scope)).getValue()
            );
            case STEP -> ((RangeValue) evalValue(node.leftOperand(), scope)).step((int) ((NumberValue) evalValue(node.rightOperand(), scope)).getValue());

            default -> throw new SyntaxError("Invalid token for infix operator", node.file(), node.line());
        });
    }

    public GearshiftValue evalValue(Node expr, Scope scope) {
        return ((Normal) eval(expr, scope)).value();
    }

    private boolean bool(Node expr, Scope scope) {
        return ((BooleanValue) evalValue(expr, scope)).getValue();
    }

    private GearshiftValue binary(InfixOperatorExpression node, Scope scope, String method) {
        return evalValue(node.leftOperand(), scope)
                .accessField(method, node.file(), node.line())
                .call(List.of(evalValue(node.rightOperand(), scope)), node.file(), node.line());
    }

    private BooleanValue binaryBool(InfixOperatorExpression node, Scope scope, String method) {
        return (BooleanValue) binary(node, scope, method);
    }

    private GearshiftValue set(InfixOperatorExpression node, Scope scope, GearshiftValue value) {
        return evalReference(node.leftOperand(), scope).setValue(value, node.file(), node.line());
    }

    private GearshiftValue compoundAssign(InfixOperatorExpression node, Scope scope, String method) {
        var left = evalValue(node.leftOperand(), scope);
        var right = evalValue(node.rightOperand(), scope);
        return evalReference(node.leftOperand(), scope)
                .setValue(left.accessField(method, node.file(), node.line()).call(List.of(right), node.file(), node.line()), node.file(), node.line());
    }

    private BooleanValue or(BooleanValue a, BooleanValue b) {
        return new BooleanValue(a.getValue() || b.getValue());
    }
    private Normal evalPrefixOperator(PrefixOperatorExpression node, Scope scope) {
        return new Normal(switch (node.operator().type()) {

            case NOT -> new BooleanValue(
                    !((BooleanValue) evalValue(node.operand(), scope)).getValue()
            );

            case SUBTRACT -> evalValue(node.operand(), scope)
                    .accessField("__neg", node.file(), node.line())
                    .call(List.of(), node.file(), node.line());

            case NEW -> evalInstantiation(node, scope);

            default -> throw new SyntaxError("Invalid token for prefix operator", node.file(), node.line());
        });
    }
    private GearshiftValue evalInstantiation(PrefixOperatorExpression node, Scope scope) {
        if (!(node.operand() instanceof ExpressionAtom) && !(node.operand() instanceof FunctionCall))
            throw new SyntaxError("Expected an identifier after \"new\" keyword", node.file(), node.line());
        Node nodeOperand;
        if (node.operand() instanceof ExpressionAtom)
            nodeOperand = node.operand();
        else if (node.operand() instanceof FunctionCall)
            nodeOperand = ((FunctionCall) node.operand()).callWhat();
        else
            throw new SyntaxError("Expected an identifier after \"new\" keyword", node.file(), node.line());
        var result = evalValue(nodeOperand, scope).instantiate(scope, List.of(), node.file(), node.line());
        if (result.hasField("__init")) {
            var method = result.accessField("__init", node.file(), node.line());
            var args = new LinkedList<GearshiftValue>();
            if (node.operand() instanceof FunctionCall) {
                for (var arg : ((FunctionCall) node.operand()).args()) {
                    args.add(((Normal) eval(arg, scope)).value());
                }
            }
            method.call(args, node.file(), node.line());
        }
        return result;
    }
    public Reference evalReference(Node node, Scope scope) {
        return switch (node) {
            case ExpressionAtom expressionAtom -> new ScopeReference(scope, expressionAtom.identifier().content());
            case InfixOperatorExpression infixOperatorExpression -> switch (infixOperatorExpression.operator().type()) {
                case DOT -> new FieldReference(((Normal) eval(infixOperatorExpression.leftOperand(), scope)).value(), evalIdentifier(infixOperatorExpression.rightOperand()));
                default -> throw new SyntaxError("Invalid infix operator \"" + infixOperatorExpression.operator().content() + "\" for an assignment", node.file(), node.line());
            };
            case IndexAccessExpression indexAccessExpression -> new IndexReference(
                    ((Normal) eval(indexAccessExpression.accessFrom(), scope)).value(),
                    ((Normal) eval(indexAccessExpression.accessWhat(), scope)).value()
            );
            default -> throw new SyntaxError(
                    "Node cannot be assigned to: " + node.getClass().getSimpleName(),
                    node.file(),
                    node.line()
            );
        };
    }
    public Map.Entry<Reference, GearshiftValue> evalAssignment(Node expr, Scope scope) {
        if (expr instanceof InfixOperatorExpression) {
            if (((InfixOperatorExpression) expr).operator().type() != TokenType.ASSIGN)
                throw new SyntaxError("Expected assignment or identifier after \"let\"", expr.file(), expr.line());
            var reference = (ScopeReference) evalReference(((InfixOperatorExpression) expr).leftOperand(), scope);
            var value = evalValue(((InfixOperatorExpression) expr).rightOperand(), scope);
            return Map.entry(reference, value);
        } else if (expr instanceof ExpressionAtom) {
            if (((ExpressionAtom) expr).identifier().type() != TokenType.IDENTIFIER) {
                throw new SyntaxError("Expected assignment or identifier after \"let\"", expr.file(), expr.line());
            }
            var reference = (ScopeReference) evalReference(expr, scope);
            var value = new NullValue();
            return Map.entry(reference, value);
        } else {
            throw new SyntaxError("Expected assignment or identifier after \"let\"", expr.file(), expr.line());
        }
    }
    private Normal evalVariableDeclaration(VariableDeclaration variableDeclaration, Scope scope) {
        var assign = evalAssignment(variableDeclaration.expr(), scope);
        var reference = (ScopeReference) assign.getKey();
        var fieldName = reference.fieldName();
        if (scope.contains(fieldName))
            throw new NameError("Variable \"" + fieldName + "\" already exists", variableDeclaration.file(), variableDeclaration.line());
        scope.declare(fieldName, assign.getValue());
        return new Normal(new NullValue());
    }
    private Normal evalFunctionCall(FunctionCall functionCall, Scope scope) {
        var reference = ((Normal) eval(functionCall.callWhat(), scope)).value();
        var args = new LinkedList<GearshiftValue>();
        for (var arg : functionCall.args()) {
            args.add(((Normal) eval(arg, scope)).value());
        }
        return new Normal(reference.call(args, functionCall.file(), functionCall.line()));
    }
    private ExecResult evalIfStatement(IfStatement ifStatement, Scope scope) {
        var ext = false;
        ExecResult returnValue = new Normal(new NullValue());
        for (var i = 0; i < ifStatement.codeBlocks().length; i++) {
            var exprResult = ((BooleanValue) ((Normal) eval(ifStatement.conditions()[i], scope)).value());
            if (exprResult.getValue()) {
                returnValue = evalCodeBlock(ifStatement.codeBlocks()[i], scope);
                ext = true;
                break;
            }
        }
        if (!ext) {
            returnValue = evalCodeBlock(ifStatement.elseCodeBlock(), scope);
        }
        return returnValue;
    }
    public ExecResult evalCodeBlock(CodeBlock codeBlock, Scope scope) {
        GearshiftValue lastValue = new NullValue();
        for (var statement : codeBlock.statements()) {
            var res = eval(statement, scope);
            if (!(res instanceof Normal))
                return res;
            else
                lastValue = ((Normal) res).value();
        }
        return new Normal(lastValue);
    }
    public GearshiftValue runFunction(Scope scope, Node body, String file, int line) {
        var result = evalCodeBlock((CodeBlock) body, scope);
        if (result instanceof Return)
            return ((Return) result).value();
        else if (result instanceof Normal)
            return ((Normal) result).value();
        else
            throw new ControlFlowError("You can't use \"break\" and \"continue\" in function body", file, line);
    }
    private ExecResult evalWhileLoop(WhileLoop whileLoop, Scope scope) {
        while(((BooleanValue) ((Normal) eval(whileLoop.condition(), scope)).value()).getValue()) {
            var res = evalCodeBlock(whileLoop.body(), scope);
            if (res instanceof Break) break;
            else if (res instanceof Return) return res;
        }
        return new Normal(new NullValue());
    }
    public int requiredArgCount(FunctionDeclaration fn) {
        int count = 0;
        for (Node defaultValue : fn.parameters().sequencedValues()) {
            if (defaultValue == null)
                count++;
        }
        return count;
    }
    public ExecResult evalFunctionDeclaration(FunctionDeclaration functionDeclaration, Scope scope) {
//        String fieldName;
//        if (functionDeclaration.name() != null) {
//            var reference = (ScopeReference) evalReference(functionDeclaration.name(), scope);
//            fieldName = reference.fieldName();
//        } else {
//            fieldName = functionDeclaration.file() + "@" + functionDeclaration.line();
//        }
//        var theFunction = new FunctionValue() {
//            @Override
//            protected Scope buildScope(List<GearshiftValue> args) {
//                var innerScope = new Scope(scope);
//                for (var i = 0; i < functionDeclaration.parameters().size(); i++) {
//                    var fieldName = ((ScopeReference) evalReference(functionDeclaration.parameters().sequencedKeySet().toArray(Node[]::new)[i], innerScope)).fieldName();
//                    if (args.size() > i)
//                        innerScope.declare(fieldName, args.get(i));
//                    else
//                        innerScope.declare(fieldName, ((Normal) eval(functionDeclaration.parameters().sequencedValues().toArray(Node[]::new)[i], innerScope)).value());
//                }
//                return innerScope;
//            }
//            @Override
//            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
//                return call(buildScope(args), args, file, line);
//            }
//            public GearshiftValue call(Scope scope, List<GearshiftValue> args, String file, int line) {
//                checkArgs(fieldName, args, requiredArgCount(functionDeclaration), functionDeclaration.parameters().size(), file, line);
//                var result = evalCodeBlock((CodeBlock) functionDeclaration.body(), scope);
//                if (result instanceof Return)
//                    return ((Return) result).value();
//                else if (result instanceof Normal)
//                    return ((Normal) result).value();
//                else
//                    throw new ControlFlowError("You can't use \"break\" and \"continue\" in function body", file, line);
//            }
//        };
        var theFunction = new FreeFunctionValue(functionDeclaration, scope, this);
        if (functionDeclaration.name() != null) {
            scope.declare(theFunction.fieldName, theFunction);
        }
        return new Normal(theFunction);
    }
    private ExecResult evalListLiteral(ListLiteral listLiteral, Scope scope) {
        var values = new LinkedList<GearshiftValue>();
        for (var expr : listLiteral.elements())
            values.add(((Normal) eval(expr, scope)).value());
        return new Normal(new ListValue(values));
    }
    private Normal evalIndexAccess(IndexAccessExpression indexAccessExpression, Scope scope) {
        return new Normal(((Normal) eval(indexAccessExpression.accessFrom(), scope)).value().accessField("__get_index", indexAccessExpression.file(), indexAccessExpression.line()).call(List.of(((Normal) eval(indexAccessExpression.accessWhat(), scope)).value()), indexAccessExpression.file(), indexAccessExpression.line()));
    }
    private ExecResult evalForLoop(ForLoop forLoop, Scope scope) {
        GearshiftValue iterator;
        if (!(((Normal) eval(forLoop.iterable(), scope)).value() instanceof FunctionValue))
            iterator = ((Normal) eval(forLoop.iterable(), scope)).value().accessField("__iterator", forLoop.file(), forLoop.line()).call(List.of(), forLoop.file(), forLoop.line());
        else
            iterator = ((Normal) eval(forLoop.iterable(), scope)).value();
        while (true) {
            var iteratorResult = iterator.call(List.of(), forLoop.file(), forLoop.line());
            if (iteratorResult instanceof NullValue) break;
            var currentScope = new Scope(scope);
            currentScope.declare(((ExpressionAtom) forLoop.variable()).identifier().content(), iteratorResult);
            var res = evalCodeBlock((CodeBlock) forLoop.body(), currentScope);
            if (res instanceof Break) break;
            else if (res instanceof Return) return res;
        }
        return new Normal(new NullValue());
    }
    private Normal evalPostfixExpression(PostfixOperatorExpression node, Scope scope) {
        return new Normal(switch (node.operator().type()) {

            case INCREMENT -> incDec(node, scope, 1);
            case DECREMENT -> incDec(node, scope, -1);

            default -> throw new SyntaxError(
                    "Invalid operator for postfix expression: " + node.operator().type(),
                    node.file(), node.line()
            );
        });
    }

    private GearshiftValue incDec(PostfixOperatorExpression node, Scope scope, int delta) {
        var ref = evalReference(node.operand(), scope);
        var value = ref.getValue(node.file(), node.line());

        if (value instanceof NumberValue n) {
            var result = new NumberValue(n.getValue() + delta);
            ref.setValue(result, node.file(), node.line());
            return result;
        }

        throw new SyntaxError(
                "Postfix operator is not supported for " + value.getClass().getSimpleName(),
                node.file(), node.line()
        );
    }
    private Normal evalMapLiteral(MapLiteral mapLiteral, Scope scope) {
        var map = new MapValue();
        for (var pair : mapLiteral.elements())
            map.accessField("__assign_index", mapLiteral.file(), mapLiteral.line()).call(List.of(evalValue(pair.getKey(), scope), evalValue(pair.getValue(), scope)), mapLiteral.file(), mapLiteral.line());
        return new Normal(map);
    }
    private Normal evalStructDeclaration(StructDeclaration structDeclaration, Scope scope) {
        var ret = new ClassValue(
                evalIdentifier(structDeclaration.name()),
                scope,
                (CodeBlock) structDeclaration.body(),
                this
        );
        scope.declare(evalIdentifier(structDeclaration.name()), ret);
        return new Normal(new NullValue());
    }
    private ExecResult eval(Node node, Scope scope) {
        return switch (node) {
            case ExpressionAtom expressionAtom -> evalExpressionAtom(expressionAtom, scope);
            case InfixOperatorExpression infixOperatorExpression -> evalInfixOperatorExpression(infixOperatorExpression, scope);
            case PrefixOperatorExpression prefixOperatorExpression -> evalPrefixOperator(prefixOperatorExpression, scope);
            case VariableDeclaration variableDeclaration -> evalVariableDeclaration(variableDeclaration, scope);
            case FunctionCall functionCall -> evalFunctionCall(functionCall, scope);
            case IfStatement ifStatement -> evalIfStatement(ifStatement, scope);
            case WhileLoop whileLoop -> evalWhileLoop(whileLoop, scope);
            case CodeBlock codeBlock -> evalCodeBlock(codeBlock, scope);
            case com.gearshiftinteractive.gearshiftscript.nodes.Continue continue_ -> new Continue();
            case com.gearshiftinteractive.gearshiftscript.nodes.Break break_ -> new Break();
            case FunctionDeclaration functionDeclaration -> evalFunctionDeclaration(functionDeclaration, scope);
            case com.gearshiftinteractive.gearshiftscript.nodes.Return return_ -> new Return(((Normal) eval(return_.expr(), scope)).value());
            case ListLiteral listLiteral -> evalListLiteral(listLiteral, scope);
            case IndexAccessExpression indexAccessExpression -> evalIndexAccess(indexAccessExpression, scope);
            case ForLoop forLoop -> evalForLoop(forLoop, scope);
            case PostfixOperatorExpression postfixOperatorExpression -> evalPostfixExpression(postfixOperatorExpression, scope);
            case MapLiteral mapLiteral -> evalMapLiteral(mapLiteral, scope);
            case StructDeclaration structDeclaration -> evalStructDeclaration(structDeclaration, scope);
            default -> throw new RuntimeException("Invalid node");
        };
    }
    public void perform(CodeBlock codeBlock, Scope scope) {
        evalCodeBlock(codeBlock, scope);
//        context.printOut();
    }
    public void perform(CodeBlock codeBlock) {
        perform(codeBlock, getDefaultScope());
    }
    public Scope getDefaultScope() {
        var scope = new Scope();
        var scanner = new Scanner(System.in);
        scope.declare("print", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                var sb = new StringBuilder();
                for (var arg : args) {
                    sb.append(arg.tojstring(file, line));
                    sb.append(' ');
                }
                if (!sb.isEmpty()) {
                    sb.deleteCharAt(sb.length() - 1);
                    System.out.print(sb);
                }
                return new NullValue();
            }
        });
        scope.declare("println", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                var sb = new StringBuilder();
                for (var arg : args) {
                    sb.append(arg.tojstring(file, line));
                    sb.append(' ');
                }
                if (!sb.isEmpty()) {
                    sb.deleteCharAt(sb.length() - 1);
                    System.out.println(sb);
                }
                return new NullValue();
            }
        });
        scope.declare("Number", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Number", args, 1, file, line);
                return new NumberValue(switch (args.getFirst()) {
                    case NumberValue numberValue -> numberValue.getValue();
                    case StringValue stringValue -> Double.parseDouble(stringValue.tojstring(file, line));
                    case BooleanValue booleanValue -> booleanValue.getValue() ? 1 : 0;
                    case NullValue nullValue -> 0;
                    default -> throw new TypeError("Can't cast " + args.getFirst().getTypeName() + " to Number", file, line);
                });
            }
        });
        scope.declare("String", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("String", args, 1, file, line);
                return args.getFirst().toStringValue(file, line);
            }
        });
        scope.declare("Boolean", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("Boolean", args, 1, file, line);
                return new BooleanValue(switch (args.getFirst()) {
                    case BooleanValue booleanValue -> booleanValue.getValue();
                    case NumberValue numberValue -> numberValue.getValue() != 0;
                    case NullValue nullValue -> false;
                    case ListValue listValue -> ((NumberValue) listValue.accessField("size", file, line).call(List.of(), file, line)).getValue() != 0;
                    default -> throw new TypeError("Can't cast " + args.getFirst().getTypeName() + " to Boolean", file, line);
                });
            }
        });
        scope.declare("ensureType", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("ensureType", args, 2, 3, file, line);
                String expected = ((StringValue) args.get(1)).tojstring(file, line);
                String got = args.getFirst().getTypeName();
                String message;
                if (args.size() == 3) message = ((StringValue) args.get(2)).tojstring(file, line);
                else message = "Expected type " + expected + ", got " + got;
                if (!got.equals(expected))
                    throw new TypeError(message, file, line);
                return null;
            }
        });
        scope.declare("typeOf", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("typeOf", args, 1, file, line);
                return new StringValue(args.getFirst().getTypeName());
            }
        });
        scope.declare("input", new FunctionValue() {
            @Override
            public GearshiftValue call(List<GearshiftValue> args, String file, int line) {
                checkArgs("input", args, 0, file, line);
                return new StringValue(scanner.nextLine());
            }
        });
        return scope;
    }
}
