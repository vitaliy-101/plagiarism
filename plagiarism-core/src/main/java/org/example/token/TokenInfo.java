package org.example.token;

import org.example.java.Java8Lexer;

public class TokenInfo {
    public final String text;
    public final int type;
    public final int line;
    public final int column;
    public final String normalizedText;

    public TokenInfo(String text, int type, int line, int column) {
        this.text = text;
        this.type = type;
        this.line = line;
        this.column = column;
        this.normalizedText = normalize(type, text);
    }

    public String normalize(int type, String text) {
        if (isIdentifier(type)) return "id";
        if (isLiteral(type)) return "lit";
        return text;
    }

    private boolean isIdentifier(int type) {
        return type == Java8Lexer.Identifier;
    }

    private boolean isLiteral(int type) {
        return type == Java8Lexer.IntegerLiteral
                || type == Java8Lexer.FloatingPointLiteral
                || type == Java8Lexer.BooleanLiteral
                || type == Java8Lexer.CharacterLiteral
                || type == Java8Lexer.StringLiteral
                || type == Java8Lexer.NullLiteral;
    }

    @Override
    public String toString() {
        return String.format("%s {%s}(type %d line %d, column %d)", text, normalizedText, type, line, column);
    }
}