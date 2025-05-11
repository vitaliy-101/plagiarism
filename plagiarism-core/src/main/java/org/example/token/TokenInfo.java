package org.example.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfo {
    private String text;
    private int type;
    private int line;
    private int column;
    private int length;
    private String normalizedText;

    public TokenInfo(String text, int type, int line, int column, int length, String normalizedText) {
        this.text = text;
        this.type = type;
        this.line = line;
        this.column = column;
        this.length = length;
        this.normalizedText = normalizedText;
    }

    @Override
    public String toString() {
        return String.format("%s {%s}(type %d line %d, column %d, length %d)",
                text, normalizedText, type, line, column, length);
    }
}
