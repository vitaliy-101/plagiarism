package org.example.normalization;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import java8.grammar.JavaLexer;
import org.example.normalization.strategy.Normalization;
import org.example.token.TokenInfo;

public class JavaNormalization implements Normalization {

    @Override
    public List<TokenInfo> normalize(List<TokenInfo> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            String newNorm = "default";

            if (token.getType() == JavaLexer.IDENTIFIER) {
                newNorm = "id";

                // 1. Импорты: import java.util.Scanner;
                if (i > 0 && tokens.get(i - 1).getType() == JavaLexer.IMPORT) {
                    while (tokens.get(i).getType() != JavaLexer.SEMI)
                    {
                        Instant instant = Instant.now();
                        long timeStampMillis = instant.toEpochMilli();
                        newNorm = "lib_" + token.getText() + Math.abs(timeStampMillis);
                        tokens.set(i, new TokenInfo(
                                token.getText(), token.getType(), token.getLine(),
                                token.getColumn(), token.getLength(), newNorm));
                        i++;
                    }
                    i--;
                }

                // 2. После new => вероятно, имя класса
                else if (i > 0 && tokens.get(i - 1).getType() == JavaLexer.NEW) {
                    newNorm = "class" + token.getText();
                }

                // 3. До скобок без точки => вызов функции
                else if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).getType() == JavaLexer.LPAREN &&  // (
                        (i == 0 || !Objects.equals(tokens.get(i - 1).getText(), "."))) {
                    newNorm = "func" + token.getText();
                }

                // 4. После точки (obj.method или System.out)
                else if (i > 0 && Objects.equals(tokens.get(i - 1).getText(), ".")) {
                    newNorm = "member" + token.getText();
                }

                // 5. По умолчанию — переменная
                else {
                    newNorm = "var";
                }
            }
            else if (token.getType() == JavaLexer.DECIMAL_LITERAL) {
                newNorm = "decimal_lit";
            }
            else if (token.getType() == JavaLexer.FLOAT_LITERAL) {
                newNorm = "float_lit";
            }
            else if (token.getType() == JavaLexer.CHAR_LITERAL) {
                newNorm = "char_lit";
            }
            else if (token.getType() == JavaLexer.STRING_LITERAL) {
                newNorm = "string_lit";
            }
            else if (token.getType() == JavaLexer.BOOL_LITERAL) {
                newNorm = "boolean_lit";
            }
            else if (token.getType() == JavaLexer.NULL_LITERAL) {
                newNorm = "null_lit";
            }
            else {
                continue;
            }

            tokens.set(i, new TokenInfo(
                    token.getText(), token.getType(), token.getLine(),
                    token.getColumn(), token.getLength(), newNorm));
        }
        return tokens;
    }
}

