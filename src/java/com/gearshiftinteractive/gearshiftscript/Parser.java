package com.gearshiftinteractive.gearshiftscript;

import com.gearshiftinteractive.gearshiftscript.interpreter.SyntaxError;
import com.gearshiftinteractive.gearshiftscript.nodes.*;
import com.gearshiftinteractive.gearshiftscript.nodes.Break;
import com.gearshiftinteractive.gearshiftscript.nodes.Continue;
import com.gearshiftinteractive.gearshiftscript.nodes.Return;
import com.sun.source.tree.LiteralTree;

import java.util.*;

public class Parser {

    private final static TokenType[] endOfStatementTokens = new TokenType[]{
            TokenType.EOL,
            TokenType.SEMICOLON,
            TokenType.EOF
    };
    private final static TokenType[] atomicTokenTypes = new TokenType[]{
            TokenType.IDENTIFIER,
            TokenType.NUMBER_LITERAL,
            TokenType.STRING_LITERAL,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL
    };
    private final static Map<TokenType, float[]> bindingPowers = Map.ofEntries(

            Map.entry(TokenType.ASSIGN,           new float[]{0.5f, 0.6f}),
            Map.entry(TokenType.ADD_ASSIGN,       new float[]{0.5f, 0.6f}),
            Map.entry(TokenType.SUB_ASSIGN,       new float[]{0.5f, 0.6f}),
            Map.entry(TokenType.MUL_ASSIGN,       new float[]{0.5f, 0.6f}),
            Map.entry(TokenType.DIV_ASSIGN,       new float[]{0.5f, 0.6f}),
            Map.entry(TokenType.POWER_ASSIGN,     new float[]{0.5f, 0.6f}),

//            Map.entry(TokenType.COMMA,            new float[]{1.0f, 1.0f}),
            Map.entry(TokenType.STEP,             new float[]{1.0f, 1.1f}),

            Map.entry(TokenType.OR,               new float[]{2.0f, 2.1f}),

            Map.entry(TokenType.AND,              new float[]{3.0f, 3.1f}),

            Map.entry(TokenType.EQUALS,           new float[]{4.0f, 4.0f}),
            Map.entry(TokenType.NOT_EQUALS,       new float[]{4.0f, 4.0f}),

            Map.entry(TokenType.GREATER,          new float[]{5.0f, 5.0f}),
            Map.entry(TokenType.LESS,             new float[]{5.0f, 5.0f}),
            Map.entry(TokenType.GREATER_OR_EQUAL, new float[]{5.0f, 5.0f}),
            Map.entry(TokenType.LESS_OR_EQUAL,    new float[]{5.0f, 5.0f}),

            Map.entry(TokenType.ADD,              new float[]{6.0f, 6.1f}),
            Map.entry(TokenType.SUBTRACT,         new float[]{6.0f, 6.1f}),

            Map.entry(TokenType.MULTIPLY,         new float[]{7.0f, 7.1f}),
            Map.entry(TokenType.DIVIDE,           new float[]{7.0f, 7.1f}),

            Map.entry(TokenType.POWER,            new float[]{10.1f, 10.0f}),

            Map.entry(TokenType.RANGE,            new float[]{10.5f, 10.5f}),

            Map.entry(TokenType.LBRACKET,         new float[]{11.0f, 11.1f}),
            Map.entry(TokenType.RBRACKET,         new float[]{11.0f, 11.1f}),
            Map.entry(TokenType.LPAREN,           new float[]{11.0f, 11.1f}),
            Map.entry(TokenType.RPAREN,           new float[]{11.0f, 11.1f}),
            Map.entry(TokenType.INCREMENT,        new float[]{11.0f, 11.1f}),
            Map.entry(TokenType.DECREMENT,        new float[]{11.0f, 11.1f}),

            Map.entry(TokenType.DOT,              new float[]{12.1f, 12.0f})
    );
    private final static Map<TokenType, Float> prefixBindingPowers = Map.ofEntries(
            Map.entry(TokenType.NEW,       6.5f),
            Map.entry(TokenType.STATIC,    6.5f),
            Map.entry(TokenType.NOT,       3.5f),
            Map.entry(TokenType.SUBTRACT,  8.0f),
            Map.entry(TokenType.INCREMENT, 9.0f),
            Map.entry(TokenType.DECREMENT, 9.0f)
    );
    private final static TokenType[] postfixTokenTypes = new TokenType[]{
            TokenType.INCREMENT,
            TokenType.DECREMENT
    };
    private final static TokenType[] tailTokens = new TokenType[]{
            TokenType.RPAREN,
            TokenType.EOL,
            TokenType.SEMICOLON,
            TokenType.RBRACKET,
            TokenType.COMMA,  //
            TokenType.EOF,
            TokenType.THEN,
            TokenType.DO,
            TokenType.ELSE,
            TokenType.END,
            TokenType.COLON,
            TokenType.RBRACE
    };
    private final static TokenType[] endOfBlockTokens = new TokenType[]{ TokenType.EOF, TokenType.END, TokenType.ELSE };

    private int tokenIndex;
    private Token[] tokens;

    private void init(Token[] tokens) {
        this.tokenIndex = 0;
        this.tokens = tokens;
    }
    private Token consume(TokenType expected) {
        var token = tokens[tokenIndex++];
        assert token.type().equals(expected) : expected + " expected, got " + token.type();
        return token;
    }
    private Token consume() {
        return tokens[tokenIndex++];
    }
    private Token consume(TokenType[] expected) {
        var token = tokens[tokenIndex++];
        assert Arrays.stream(expected).anyMatch((i) -> i.equals(token.type())) : Arrays.stream(expected).toList() + " expected, got " + token.type();
        return token;
    }
    private boolean canConsume() {
        return tokenIndex < tokens.length;
    }
    private Token peek(int ahead) {
        return tokens[tokenIndex + ahead];
    }
    private Token peek() {
        return tokens[tokenIndex];
    }
    /////////////////
    // EXPRESSIONS //
    /////////////////
    private Node handleExpression(float parentBindingPower) {
        var leftExpr = handleExpressionHead();
        return handleExpressionTail(parentBindingPower, leftExpr);
    }
    private Node handleExpressionHead() {
        var token = consume();
        if (token.type().equals(TokenType.LPAREN)) {
            var rightExpr = handleExpression(0);
            consume(TokenType.RPAREN);
            return rightExpr;
        } else if (token.type().equals(TokenType.LBRACKET)) {
            return handleListLiteral(0, token);
        } else if (token.type().equals(TokenType.LBRACE)) {
            return handleMapLiteral(0, token);
        }
        System.out.println(">>>" + token);
        if (Arrays.stream(atomicTokenTypes).anyMatch((i) -> i.equals(token.type()))) {  // if its just a literal
            return new ExpressionAtom(token, token.file(), token.lineNumber());
        } else if (token.type() == TokenType.FUNCTION) {
            return handleFunctionDeclarationExpression(token.file(), token.lineNumber());
        } else {  // try to make it into a prefix expression
            var op = token;
            var rightExpr = handleExpression(prefixBindingPowers.get(op.type()));
            return new PrefixOperatorExpression(op, rightExpr, op.file(), op.lineNumber());
        }
    }
    private Node handleExpressionTail(float parentBindingPower, Node leftExpr) {
        while (canConsume()) {
            var op = peek();
            if (Arrays.stream(tailTokens).anyMatch((i) -> i.equals(op.type())))  // if the token is a tail one
                break;
            else if (bindingPowers.get(op.type())[1] < parentBindingPower)
                break;
            else if (bindingPowers.get(op.type())[1] == parentBindingPower && bindingPowers.get(op.type())[0] > bindingPowers.get(op.type())[1])
                break;
            consume(bindingPowers.keySet().toArray(new TokenType[0]));
            if (op.type().equals(TokenType.LPAREN)) {
                skipEndOfStatementTokens();
                leftExpr = handleFunctionCall(0, leftExpr, op);
//            } else if (op.type() == TokenType.COMMA) {
//                var elements = new ArrayList<Node>();
//                elements.add(leftExpr);
//                var right = handleExpression(0);
//                if (right instanceof TupleLiteral) {
//                    elements.addAll(((TupleLiteral) right).elements());
//                } else
//                    elements.add(right);
//                leftExpr = new TupleLiteral(elements);
            } else if (op.type().equals(TokenType.LBRACKET)) {
                var middleExpr = handleExpression(0);
                consume(TokenType.RBRACKET);
                leftExpr = new IndexAccessExpression(leftExpr, middleExpr, middleExpr.file(), middleExpr.line());
            } else if (Arrays.stream(postfixTokenTypes).anyMatch((i) -> i.equals(op.type()))) {  // if its a postfix
                leftExpr = new PostfixOperatorExpression(op, leftExpr, op.file(), op.lineNumber());
            } else {
                var rightExpr = handleExpression(bindingPowers.get(op.type())[0]);
                leftExpr = new InfixOperatorExpression(op, leftExpr, rightExpr, op.file(), op.lineNumber());
            }
        }
        return leftExpr;
    }
    private Node handleFunctionCall(float parentBindingPower, Node leftExpr, Token lparen) {
        List<Node> args = new ArrayList<>();
        while (true) {
            if (peek().type().equals(TokenType.COMMA)) {
                consume(TokenType.COMMA);
                skipEndOfStatementTokens();
            } else if (peek().type().equals(TokenType.RPAREN)) {
                consume(TokenType.RPAREN);
                break;
            } else {
                args.add(handleExpression(parentBindingPower));
                skipEndOfStatementTokens();
            }
        }
        return new FunctionCall(leftExpr, args.toArray(new Node[0]), lparen.file(), lparen.lineNumber());
    }
    ////
    private Node handleMapLiteral(float parentBindingPower, Token token) {
        var elements = new LinkedList<Map.Entry<Node, Node>>();
        while (true) {
            if (peek().type().equals(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            } else if (peek().type().equals(TokenType.RBRACE)) {
                consume(TokenType.RBRACE);
                break;
            } else if (peek().type().equals(TokenType.EOL)) {
                consume(TokenType.EOL);
            } else {
                var key = handleExpression(parentBindingPower);
                consume(TokenType.COLON);
                var value = handleExpression(parentBindingPower);
                elements.add(Map.entry(key, value));
            }
        }
        return new MapLiteral(elements, token.file(), token.lineNumber());
    }
    private Node handleListLiteral(float parentBindingPower, Token lbracket) {
        var elements = new ArrayList<Node>();
        while (true) {
            if (peek().type().equals(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            } else if (peek().type().equals(TokenType.RBRACKET)) {
                consume(TokenType.RBRACKET);
                break;
            } else if (peek().type().equals(TokenType.EOL)) {
                consume(TokenType.EOL);
            } else {
                elements.add(handleExpression(parentBindingPower));
            }
        }
        return new ListLiteral(elements, lbracket.file(), lbracket.lineNumber());
    }
    private Node handleVariableDeclaration() {
        var let = consume(TokenType.LET);
        return new VariableDeclaration(handleExpression(0), let.file(), let.lineNumber());
    }
    private Node handleIfStatement() {
        var ifToken = consume(TokenType.IF);

        List<Node> conditions = new ArrayList<>();
        List<CodeBlock> codeBlocks = new ArrayList<>();

        // first if
        Node condition = handleExpression(0);
        consume(TokenType.THEN);
        skipEndOfStatementTokens();
        CodeBlock block = handleCodeBlock(ifToken.file(), ifToken.lineNumber());

        conditions.add(condition);
        codeBlocks.add(block);

        // else if
        while (peek().type() == TokenType.ELSE && peek(1).type() == TokenType.IF) {
            var elseToken = consume(TokenType.ELSE);
            consume(TokenType.IF);

            Node elseIfCondition = handleExpression(0);
            consume(TokenType.THEN);
            skipEndOfStatementTokens();
            CodeBlock elseIfBlock = handleCodeBlock(elseToken.file(), elseToken.lineNumber());

            conditions.add(elseIfCondition);
            codeBlocks.add(elseIfBlock);
        }

        // else
        CodeBlock elseCodeBlock = new CodeBlock(new Node[]{}, "null", 0);
        if (peek().type() == TokenType.ELSE) {
            var elseToken = consume(TokenType.ELSE);
            skipEndOfStatementTokens();
            elseCodeBlock = handleCodeBlock(elseToken.file(), elseToken.lineNumber());
        }

        // end
        consume(TokenType.END);

//        System.out.println(conditions);
//        System.out.println(codeBlocks);

        return new IfStatement(
                conditions.toArray(Node[]::new),
                codeBlocks.toArray(CodeBlock[]::new),
                elseCodeBlock,
                ifToken.file(),
                ifToken.lineNumber()
        );
    }
    private void skipEndOfStatementTokens() {
        while (canConsume() &&
                Arrays.stream(endOfStatementTokens)
                        .anyMatch(t -> t == peek().type())) {
            consume(endOfStatementTokens);
        }
    }
    private Node handleWhileLoop() {
        var whileToken = consume(TokenType.WHILE);
        var expr = handleExpression(0);
        consume(TokenType.DO);
        skipEndOfStatementTokens();
        var codeBlock = handleCodeBlock(whileToken.file(), whileToken.lineNumber());
        consume(TokenType.END);
        return new WhileLoop(expr, codeBlock, whileToken.file(), whileToken.lineNumber());
    }
    private Node handleFunctionName(String file, int line) {
        return new ExpressionAtom(consume(TokenType.IDENTIFIER), file, line);
    }
    private Node handleFunctionDeclarationExpression(String file, int line) {
        Node name = null;
        if (peek().type() != TokenType.LPAREN)
            name = handleFunctionName(file, line);
        consume(TokenType.LPAREN);
        var args = new LinkedHashMap<Node, Node>();
        if (peek().type() != TokenType.RPAREN)
            while (peek().type() != TokenType.RPAREN) {
                var key = new ExpressionAtom(consume(TokenType.IDENTIFIER), file, line);
                Node defValue = null;
                if (peek().type() == TokenType.ASSIGN) { consume(TokenType.ASSIGN); defValue = handleExpression(0); }
                if (peek().type() == TokenType.RPAREN) { args.put(key, defValue); break;  }
                else if (peek().type() == TokenType.COMMA) consume(TokenType.COMMA);
                else throw new SyntaxError("Unexpected token: " + peek().content(), file, line);
                args.put(key, defValue);
            }
        consume(TokenType.RPAREN);
        consume(TokenType.IS);
        skipEndOfStatementTokens();
        var body = handleCodeBlock(file, line);
        consume(TokenType.END);
        return new FunctionDeclaration(name, args, body, file, line);
    }
    private Node handleFunctionDeclaration() {
        var functionToken = consume(TokenType.FUNCTION);
        return handleFunctionDeclarationExpression(functionToken.file(), functionToken.lineNumber());
    }
    private Node handleReturn() {
        var returnToken = consume(TokenType.RETURN);
        if (Arrays.stream(endOfStatementTokens).anyMatch((i) -> i.equals(peek().type())))
            return new Return(new ExpressionAtom(new Token(TokenType.NULL, "null", returnToken.lineNumber(), returnToken.file()), returnToken.file(), returnToken.lineNumber()), returnToken.file(), returnToken.lineNumber());
        else
            return new Return(handleExpression(0), returnToken.file(), returnToken.lineNumber());
    }
    private Node handleForLoop() {
        var forToken = consume(TokenType.FOR);
        var variable = new ExpressionAtom(consume(TokenType.IDENTIFIER), forToken.file(), forToken.lineNumber());
        consume(TokenType.IN);
        var iterable = handleExpression(0);
        consume(TokenType.DO);
        skipEndOfStatementTokens();
        var body = handleCodeBlock(forToken.file(), forToken.lineNumber());
        consume(TokenType.END);
        return new ForLoop(variable, iterable, body, forToken.file(), forToken.lineNumber());
    }
    private Node handleStructDeclaration() {
        var struct = consume(TokenType.STRUCT);
        var name = handleFunctionName(struct.file(), struct.lineNumber());
        consume(TokenType.IS);
        skipEndOfStatementTokens();
        var body = handleCodeBlock(struct.file(), struct.lineNumber());
        consume(TokenType.END);
        return new StructDeclaration(name, body, struct.file(), struct.lineNumber());
    }
    private Node handleStatement() {
        return switch (peek().type()) {
            case LET -> handleVariableDeclaration();
            case IF -> handleIfStatement();
            case WHILE -> handleWhileLoop();
            case CONTINUE ->  {
                consume(TokenType.CONTINUE);
                yield new Continue(peek().file(), peek().lineNumber());
            }
            case BREAK ->  {
                consume(TokenType.BREAK);
                yield new Break(peek().file(), peek().lineNumber());
            }
            case FUNCTION -> handleFunctionDeclaration();
            case RETURN -> handleReturn();
            case FOR -> handleForLoop();
            case STRUCT -> handleStructDeclaration();
            default -> handleExpression(0);
        };
    }
    private CodeBlock handleCodeBlock(String file, int startLineNumber) {
        var statements = new ArrayList<Node>();
        skipEndOfStatementTokens();
        while (canConsume()) {
            if (peek().type() == TokenType.EOF) {
                break;
            }
            statements.add(handleStatement());
//            System.out.println(">>>" + peek().type());
            while (canConsume() && Arrays.stream(endOfStatementTokens).anyMatch((i) -> i.equals(peek().type()))) {
                consume(endOfStatementTokens);  // skipping "\n" or ";"
            }
            if (canConsume() && Arrays.stream(endOfBlockTokens).anyMatch((i) -> i.equals(peek().type()))) {
                break;
            }
        }
        return new CodeBlock(statements.toArray(Node[]::new), file, startLineNumber);
    }
    public CodeBlock perform(Token[] tokens, String file) {
        init(tokens);
        return handleCodeBlock(file, 0);
    }
}
