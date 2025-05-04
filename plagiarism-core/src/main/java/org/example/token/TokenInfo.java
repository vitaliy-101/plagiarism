package org.example.token;

import com.example.content.Language;
import cpp14.grammar.CPP14Parser;
import java8.grammar.JavaLexer;
import python3.grammar.PythonLexer;
import cpp14.grammar.CPP14Lexer;
import go.grammar.GoLexer;

public class TokenInfo {


    public final String text;
    public final int type;
    public final int line;
    public final int column;
    public final String normalizedText;

    public TokenInfo(String text, int type, int line, int column, Language lang) {
        // Если type равен -1 (неизвестный тип), устанавливаем тип по умолчанию, например, 0
        this.text = text;
        this.type = (type == -1) ? 0 : type; // Устанавливаем дефолтный тип, если тип токена недействителен
        this.line = line;
        this.column = column;
        this.normalizedText = normalize(lang, this.type, text);
    }

    public String normalize(Language lang, int type, String text) {
        switch (lang) {
            case JAVA:
                if (isIdentifierJava(type)) return "id";
                if (isLiteralJava(type)) return "lit";
                break;
            case PY:
                if (isIdentifierPython(type)) return "id";
                if (isLiteralPython(type)) return "lit";
                break;
            case CPP:
                if (isIdentifierCpp(type)) return "id";
                if (isLiteralCpp(type)) return "lit";
                break;
            case GO:
                if (isIdentifierGo(type)) return "id";
                if (isLiteralGo(type)) return "lit";
                break;
        }
        return text;
    }

    private boolean isIdentifierJava(int type) {
        return type == JavaLexer.IDENTIFIER;
    }

    private boolean isLiteralJava(int type) {
        return type == JavaLexer.DECIMAL_LITERAL
                || type == JavaLexer.FLOAT_LITERAL
                || type == JavaLexer.BOOL_LITERAL
                || type == JavaLexer.CHAR_LITERAL
                || type == JavaLexer.STRING_LITERAL
                || type == JavaLexer.NULL_LITERAL;
    }

    private boolean isIdentifierPython(int type) {
        return type == PythonLexer.NAME;
    }

    private boolean isLiteralPython(int type) {
        return type == PythonLexer.STRING
                || type == PythonLexer.NUMBER
                || type == PythonLexer.NONE
                || type == PythonLexer.TRUE
                || type == PythonLexer.FALSE;
    }

    private boolean isIdentifierCpp(int type) {
        return type == CPP14Lexer.Identifier;
    }

    private boolean isLiteralCpp(int type) {
        return type == CPP14Parser.IntegerLiteral
                || type == CPP14Lexer.FloatingLiteral
                || type == CPP14Lexer.CharacterLiteral
                || type == CPP14Lexer.StringLiteral
                || type == CPP14Lexer.BooleanLiteral;
    }

    private boolean isIdentifierGo(int type) {
        return type == GoLexer.IDENTIFIER;
    }

    private boolean isLiteralGo(int type) {
        return type == GoLexer.DECIMAL_LIT
                || type == GoLexer.FLOAT_LIT
                || type == GoLexer.RUNE_LIT
                || type == GoLexer.RAW_STRING_LIT
                || type == GoLexer.NIL_LIT
                || type == GoLexer.BINARY_LIT;
    }

    @Override
    public String toString() {
        return String.format("%s {%s}(type %d line %d, column %d)", text, normalizedText, type, line, column);
    }
}
