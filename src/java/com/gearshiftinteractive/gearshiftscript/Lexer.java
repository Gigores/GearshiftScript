package com.gearshiftinteractive.gearshiftscript;

import com.gearshiftinteractive.gearshiftscript.interpreter.SyntaxError;

import java.util.*;

public class Lexer {

    private final static Map<String, TokenType> OPERATORS = Map.ofEntries(
            Map.entry("=", TokenType.ASSIGN),
            Map.entry("+=", TokenType.ADD_ASSIGN),
            Map.entry("-=", TokenType.SUB_ASSIGN),
            Map.entry("/=", TokenType.DIV_ASSIGN),
            Map.entry("*=", TokenType.MUL_ASSIGN),
            Map.entry("^=", TokenType.POWER_ASSIGN),
            Map.entry("+", TokenType.ADD),
            Map.entry("-", TokenType.SUBTRACT),
            Map.entry("*", TokenType.MULTIPLY),
            Map.entry("/", TokenType.DIVIDE),
            Map.entry("^", TokenType.POWER),
            Map.entry(".", TokenType.DOT),
            Map.entry("..", TokenType.RANGE),
            Map.entry("++", TokenType.INCREMENT),
            Map.entry("--", TokenType.DECREMENT),
            Map.entry("==", TokenType.EQUALS),
            Map.entry("!=", TokenType.NOT_EQUALS),
            Map.entry("<", TokenType.LESS),
            Map.entry(">", TokenType.GREATER),
            Map.entry(">=", TokenType.GREATER_OR_EQUAL),
            Map.entry("<=", TokenType.LESS_OR_EQUAL)
    );
    private final static Map<String, TokenType> PUNCTUATION = Map.ofEntries(
            Map.entry("\n", TokenType.EOL),
            Map.entry(";", TokenType.SEMICOLON),
            Map.entry("(", TokenType.LPAREN),
            Map.entry(")", TokenType.RPAREN),
            Map.entry(",", TokenType.COMMA),
            Map.entry("[", TokenType.LBRACKET),
            Map.entry("]", TokenType.RBRACKET),
            Map.entry("{", TokenType.LBRACE),
            Map.entry("}", TokenType.RBRACE),
            Map.entry(":", TokenType.COLON)
    );
    private final static Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("let", TokenType.LET),
            Map.entry("if", TokenType.IF),
            Map.entry("then", TokenType.THEN),
            Map.entry("end", TokenType.END),
            Map.entry("else", TokenType.ELSE),
            Map.entry("while", TokenType.WHILE),
            Map.entry("do", TokenType.DO),
            Map.entry("continue", TokenType.CONTINUE),
            Map.entry("break", TokenType.BREAK),
            Map.entry("function", TokenType.FUNCTION),
            Map.entry("is", TokenType.IS),
            Map.entry("return", TokenType.RETURN),
            Map.entry("in", TokenType.IN),
            Map.entry("for", TokenType.FOR),
            Map.entry("struct", TokenType.STRUCT),
            Map.entry("new", TokenType.NEW),
            Map.entry("static", TokenType.STATIC),

            Map.entry("and", TokenType.AND),
            Map.entry("or", TokenType.OR),
            Map.entry("not", TokenType.NOT),
            Map.entry("step", TokenType.STEP),

            Map.entry("true", TokenType.TRUE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("null", TokenType.NULL)
    );
    private final static String IDENTIFIER_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    private final static String NUMERICAL_IDENTIFIER_CHARACTERS = "0123456789";
    private final static String NUMBER_LITERAL_CHARACTERS = "0123456789.";
    private final static String INVISIBLE_CHARACTERS = " \t";

    private String file;
    private int symbolIndex;
    private int line;
    private String text;

    private void init(String file, String text) {
        this.file = file;
        this.line = 1;
        this.symbolIndex = 0;
        this.text = text;
    }
    private char consume() {
//        System.out.println("consume - " + (symbolIndex + 1));
        var character = text.charAt(symbolIndex++);
        if (character == '\n') line++;
        return character;
    }
    private void consume(int amount) {
//        System.out.println("consume " + amount + " - " + (symbolIndex + amount));
        for (int i = 0; i < amount; i++)
            consume();
    }
    private boolean canConsume() {
        return symbolIndex < text.length();
    }
    private char peek(int ahead) {
        return text.charAt(symbolIndex + ahead);
    }
    private char peek() {
        return text.charAt(symbolIndex);
    }
    private String lookup(int ahead, int length) {
        return text.substring(symbolIndex + ahead, symbolIndex + ahead + length);
    }
    private String lookup(int length) {
        return text.substring(symbolIndex, symbolIndex + length);
    }

    private Token handleIdentifier() {
        var identifierText = new StringBuilder();
        while (canConsume() && (IDENTIFIER_CHARACTERS + NUMERICAL_IDENTIFIER_CHARACTERS).contains(String.valueOf(peek()))) {
            identifierText.append(consume());
        }
        var string = identifierText.toString();
        return keywords.containsKey(string) ? new Token(keywords.get(string), string, line, file) : new Token(TokenType.IDENTIFIER, string, line, file);
    }
    private Token handleNumberLiteral() {
        var numberLiteralText = new StringBuilder();
        while (canConsume() && NUMBER_LITERAL_CHARACTERS.contains(String.valueOf(peek()))) {
            if (peek() == '.' && !NUMBER_LITERAL_CHARACTERS.contains(String.valueOf(peek(1))) && !numberLiteralText.isEmpty())
                break;
            else if (peek() == '.' && peek(1) == '.' && !numberLiteralText.isEmpty())
                break;
            else if (peek() == '.' && peek(1) == '.' && numberLiteralText.isEmpty()) {
                consume(2);
                return new Token(TokenType.RANGE, "..", line, file);
            }
            numberLiteralText.append(consume());
            if (numberLiteralText.toString().equals(".."))
                return new Token(TokenType.RANGE, "..", line, file);
        }
        return numberLiteralText.toString().equals(".") ? new Token(TokenType.DOT, ".", line, file) : new Token(TokenType.NUMBER_LITERAL, numberLiteralText.toString(), line, file);
    }
    private Token handleStringLiteral() {
        var startPar = consume();
        var stringLiteralText = new StringBuilder();
        while (canConsume() && peek() != startPar) {
            var symbol = consume();
            if (symbol == '\\') {
                var followUp = consume();
                stringLiteralText.append(switch (followUp) {
                    case 'n' -> "\n";
                    case 'r' -> "\r";
                    case 't' -> "\t";
                    case 'b' -> "\b";
                    case 'f' -> "\f";
                    case '0' -> "\0";
                    case '\\' -> "\\";
                    case '\'' -> {
                        if (startPar == '\'')
                            yield "'";
                        else
                            throw new SyntaxError("Invalid escape sequence \\' in double-quoted string", file, line);
                    }
                    case '"' -> {
                        if (startPar == '"')
                            yield "\"";
                        else
                            throw new SyntaxError("Invalid escape sequence \\' in single-quoted string", file, line);
                    }
                    default -> throw new SyntaxError("Unknown escape sequence: \\" + followUp, file, line);
                });
            } else
                stringLiteralText.append(symbol);
        }
        consume();
        return new Token(TokenType.STRING_LITERAL, stringLiteralText.toString(), line, file);
    }
    private Optional<Token> handlePunctuationSymbol () {
        for (var punctuation : PUNCTUATION.keySet().stream().sorted(Comparator.comparingInt(String::length).reversed()).toList()) {
            if (lookup(punctuation.length()).equals(punctuation)) {
                consume(punctuation.length());
                return Optional.of(new Token(PUNCTUATION.get(punctuation), punctuation, line, file));
            }
        }
        for (var punctuation : OPERATORS.keySet().stream().sorted(Comparator.comparingInt(String::length).reversed()).toList()) {
            if (lookup(punctuation.length()).equals(punctuation)) {
                consume(punctuation.length());
                return Optional.of(new Token(OPERATORS.get(punctuation), punctuation, line, file));
            }
        }
        return Optional.empty();
    }

    public List<Token> perform(String file, String code) {
        var ret = new ArrayList<Token>();
        init(file, code);

        while (canConsume()) {
            var currentSymbol = peek();

            if (IDENTIFIER_CHARACTERS.contains(String.valueOf(currentSymbol))) {
                ret.add(handleIdentifier());
            } else if (NUMBER_LITERAL_CHARACTERS.contains(String.valueOf(currentSymbol))) {
                ret.add(handleNumberLiteral());
            } else if ("\"'".indexOf(currentSymbol) >= 0) {
                ret.add(handleStringLiteral());
            } else if (INVISIBLE_CHARACTERS.indexOf(currentSymbol) >= 0) {
                consume();
            } else if (currentSymbol == '#') {
                while (canConsume() && peek() != '\n') consume();
            } else {
                var punct = handlePunctuationSymbol();
                punct.ifPresent(ret::add);
            }
        }
        ret.add(new Token(TokenType.EOF, "", line, file));

        return ret;
    }
}
