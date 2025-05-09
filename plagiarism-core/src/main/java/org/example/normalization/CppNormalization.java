package org.example.normalization;

import cpp14.grammar.CPP14Lexer;
import org.example.token.TokenInfo;
import org.example.normalization.strategy.Normalization;

import java.util.List;
import java.util.Objects;

public class CppNormalization implements Normalization {

    @Override
    public List<TokenInfo> normalize(List<TokenInfo> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            String newNorm = "default";

            if (token.getType() == CPP14Lexer.Identifier) {
                newNorm = "id";

                // 2. После new — имя класса
                if (i > 0 && tokens.get(i - 1).getType() == CPP14Lexer.New) {
                    newNorm = "class" + token.getText();
                }

                // 3. До скобок без точки — вызов функции
                else if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).getType() == CPP14Lexer.LeftParen &&
                        (i == 0 || !Objects.equals(tokens.get(i - 1).getText(), "."))) {
                    newNorm = "func" + token.getText();
                }

                // 4. После точки или стрелки — член
                else if (i > 0 && (Objects.equals(tokens.get(i - 1).getText(), ".") ||
                        Objects.equals(tokens.get(i - 1).getText(), "->"))) {
                    newNorm = "member" + token.getText();
                }

                // 5. По умолчанию — переменная
                else {
                    newNorm = "var";
                }

            } else if (token.getType() == CPP14Lexer.IntegerLiteral) {
                newNorm = "decimal_lit";
            } else if (token.getType() == CPP14Lexer.FloatingLiteral) {
                newNorm = "float_lit";
            } else if (token.getType() == CPP14Lexer.CharacterLiteral) {
                newNorm = "char_lit";
            } else if (token.getType() == CPP14Lexer.StringLiteral) {
                newNorm = "string_lit";
            } else if (token.getText().equals("true") || token.getText().equals("false")) {
                newNorm = "boolean_lit";
            } else if (token.getText().equals("nullptr")) {
                newNorm = "null_lit";
            } else {
                continue;
            }

            tokens.set(i, new TokenInfo(token.getText(), token.getType(), token.getLine(),
                    token.getColumn(), token.getLength(), newNorm));
        }

        return tokens;
    }
}

