package org.example.normalization;

import go.grammar.GoLexer;
import org.example.token.TokenInfo;
import org.example.normalization.strategy.Normalization;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class GoNormalization implements Normalization {

    @Override
    public List<TokenInfo> normalize(List<TokenInfo> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            String newNorm = "default";

            // 1. Импорты: import "fmt"
            if (i > 0 && tokens.get(i - 1).getType() == GoLexer.IMPORT) {
                boolean flag = false;
                int currLine = tokens.get(i - 1).getLine();
                while (flag || tokens.get(i).getLine() == currLine) {
                    if (tokens.get(i).getType() == GoLexer.L_PAREN)
                        flag = true;
                    else if (tokens.get(i).getType() == GoLexer.R_PAREN)
                        flag = false;
                    else
                    {
                        Instant instant = Instant.now();
                        long timeStampMillis = instant.toEpochMilli();
                        newNorm = "lib_" + token.getText() + Math.abs(timeStampMillis);
                    }

                    tokens.set(i, new TokenInfo(token.getText(), token.getType(),
                            token.getLine(), token.getColumn(), token.getLength(), newNorm));
                    i++;
                }
                i--;
            }

            else if (token.getType() == GoLexer.IDENTIFIER) {
                newNorm = "id";

                // 2. До скобок без точки — вызов функции
                if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).getType() == GoLexer.L_PAREN &&
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

            } else if (token.getType() == GoLexer.DECIMAL_LIT) {
                newNorm = "decimal_lit";
            } else if (token.getType() == GoLexer.FLOAT_LIT) {
                newNorm = "float_lit";
            } else if (token.getType() == GoLexer.RUNE_LIT) {
                newNorm = "rune_lit";
            } else if (token.getType() == GoLexer.RAW_STRING_LIT || token.getType() == GoLexer.INTERPRETED_STRING_LIT) {
                newNorm = "string_lit";
            } else if (token.getType() == GoLexer.NIL_LIT) {
                newNorm = "null_lit";
            } else if (token.getType() == GoLexer.BINARY_LIT) {
                newNorm = "boolean_lit";
            } else {
                continue;
            }

            tokens.set(i, new TokenInfo(token.getText(), token.getType(),
                    token.getLine(), token.getColumn(), token.getLength(), newNorm));
        }

        return tokens;
    }
}

