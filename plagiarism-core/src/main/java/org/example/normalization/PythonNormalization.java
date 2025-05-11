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

            if (token.getType() == PythonLexer.COMMENT ||
                    token.getType() == PythonLexer.STRING && i > 0 && tokens.get(i - 1).getType() == PythonLexer.INDENT) {
                Instant instant = Instant.now();
                long timeStampMillis = instant.toEpochMilli();
                newNorm = "comment_" + Math.abs(timeStampMillis);
            }
            else if (token.getType() == PythonLexer.NAME) {
                newNorm = "id";

                // 1. Импорты: import os
                if (i > 0 && (tokens.get(i - 1).getType() == PythonLexer.IMPORT || tokens.get(i - 1).getType() == PythonLexer.FROM)) {
                    int currLine = tokens.get(i - 1).getLine();
                    while (tokens.get(i).getLine() == currLine) {
                        if (tokens.get(i).getType() != PythonLexer.IMPORT){
                            Instant instant = Instant.now();
                            long timeStampMillis = instant.toEpochMilli();
                            newNorm = "lib_" + token.getText() + Math.abs(timeStampMillis);
                        }
                        tokens.set(i, new TokenInfo(token.getText(), token.getType(), token.getLine(),
                                token.getColumn(), token.getLength(), newNorm));
                        i++;
                    }
                    i--;
                }

                // 2. До скобок без точки — вызов функции
                else if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).getType() == PythonLexer.LPAR &&
                        (i == 0 || !Objects.equals(tokens.get(i - 1).getText(), "."))) {
                    newNorm = "func" + token.getText();
                }

                // 3. После точки — член объекта
                else if (i > 0 && Objects.equals(tokens.get(i - 1).getText(), ".")) {
                    newNorm = "member" + token.getText();
                }

                // 4. По умолчанию — переменная
                else {
                    newNorm = "var";
                }

            } else if (token.getType() == PythonLexer.STRING) {
                newNorm = "string_lit";
            } else if (token.getType() == PythonLexer.NUMBER) {
                newNorm = "number_lit";
            } else if (token.getType() == PythonLexer.NONE) {
                newNorm = "null_lit";
            } else if (token.getType() == PythonLexer.TRUE || token.getType() == PythonLexer.FALSE) {
                newNorm = "boolean_lit";
            } else {
                continue;
            }

            tokens.set(i, new TokenInfo(token.getText(), token.getType(), token.getLine(),
                    token.getColumn(), token.getLength(), newNorm));
        }

        return tokens;
    }
}
