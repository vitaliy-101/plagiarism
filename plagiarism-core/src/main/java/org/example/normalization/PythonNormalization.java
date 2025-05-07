package org.example.normalization;

import python3.grammar.PythonLexer;
import org.example.token.TokenInfo;
import org.example.normalization.strategy.Normalization;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class PythonNormalization implements Normalization {

    @Override
    public List<TokenInfo> normalize(List<TokenInfo> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            String newNorm = "default";

            if (token.type == PythonLexer.COMMENT ||
                    token.type == PythonLexer.STRING && i > 0 && tokens.get(i - 1).type == PythonLexer.INDENT) {
                Instant instant = Instant.now();
                long timeStampMillis = instant.toEpochMilli();
                newNorm = "comment_" + Math.abs(timeStampMillis);
            }
            else if (token.type == PythonLexer.NAME) {
                newNorm = "id";

                // 1. Импорты: import os
                if (i > 0 && (tokens.get(i - 1).type == PythonLexer.IMPORT || tokens.get(i - 1).type == PythonLexer.FROM)) {
                    int currLine = tokens.get(i - 1).line;
                    while (tokens.get(i).line == currLine) {
                        if (tokens.get(i).type != PythonLexer.IMPORT){
                            Instant instant = Instant.now();
                            long timeStampMillis = instant.toEpochMilli();
                            newNorm = "lib_" + token.text + Math.abs(timeStampMillis);
                        }
                        tokens.set(i, new TokenInfo(token.text, token.type, token.line, token.column, newNorm));
                        i++;
                    }
                    i--;
                }

                // 2. До скобок без точки — вызов функции
                else if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).type == PythonLexer.LPAR &&
                        (i == 0 || !Objects.equals(tokens.get(i - 1).text, "."))) {
                    newNorm = "func" + token.text;
                }

                // 3. После точки — член объекта
                else if (i > 0 && Objects.equals(tokens.get(i - 1).text, ".")) {
                    newNorm = "member" + token.text;
                }

                // 4. По умолчанию — переменная
                else {
                    newNorm = "var";
                }

            } else if (token.type == PythonLexer.STRING) {
                newNorm = "string_lit";
            } else if (token.type == PythonLexer.NUMBER) {
                newNorm = "number_lit";
            } else if (token.type == PythonLexer.NONE) {
                newNorm = "null_lit";
            } else if (token.type == PythonLexer.TRUE || token.type == PythonLexer.FALSE) {
                newNorm = "boolean_lit";
            } else {
                continue;
            }

            tokens.set(i, new TokenInfo(token.text, token.type, token.line, token.column, newNorm));
        }

        return tokens;
    }
}
