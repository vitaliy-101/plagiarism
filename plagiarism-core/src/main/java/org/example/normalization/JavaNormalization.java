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

            if (token.type == JavaLexer.IDENTIFIER) {
                newNorm = "id";

                // 1. Импорты: import java.util.Scanner;
                if (i > 0 && tokens.get(i - 1).type == JavaLexer.IMPORT) {
                    while (tokens.get(i).type != JavaLexer.SEMI)
                    {
                        Instant instant = Instant.now();
                        long timeStampMillis = instant.toEpochMilli();
                        newNorm = "lib_" + token.text + Math.abs(timeStampMillis);
                        tokens.set(i, new TokenInfo(
                                token.text, token.type, token.line, token.column,
                                newNorm));
                        i++;
                    }
                    i--;
                }

                // 2. После new => вероятно, имя класса
                else if (i > 0 && tokens.get(i - 1).type == JavaLexer.NEW) {
                    newNorm = "class" + token.text;
                }

                // 3. До скобок без точки => вызов функции
                else if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).type == JavaLexer.LPAREN &&  // (
                        (i == 0 || !Objects.equals(tokens.get(i - 1).text, "."))) {
                    newNorm = "func" + token.text;
                }

                // 4. После точки (obj.method или System.out)
                else if (i > 0 && Objects.equals(tokens.get(i - 1).text, ".")) {
                    newNorm = "member" + token.text;
                }

                // 5. По умолчанию — переменная
                else {
                    newNorm = "var";
                }
            }
            else if (token.type == JavaLexer.DECIMAL_LITERAL) {
                newNorm = "decimal_lit";
            }
            else if (token.type == JavaLexer.FLOAT_LITERAL) {
                newNorm = "float_lit";
            }
            else if (token.type == JavaLexer.CHAR_LITERAL) {
                newNorm = "char_lit";
            }
            else if (token.type == JavaLexer.STRING_LITERAL) {
                newNorm = "string_lit";
            }
            else if (token.type == JavaLexer.BOOL_LITERAL) {
                newNorm = "boolean_lit";
            }
            else if (token.type == JavaLexer.NULL_LITERAL) {
                newNorm = "null_lit";
            }
            else {
                continue;
            }

            tokens.set(i, new TokenInfo(
                    token.text, token.type, token.line, token.column,
                    newNorm));
        }
        return tokens;
    }
}

