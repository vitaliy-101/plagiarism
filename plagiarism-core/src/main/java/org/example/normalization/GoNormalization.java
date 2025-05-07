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
            if (i > 0 && tokens.get(i - 1).type == GoLexer.IMPORT) {
                boolean flag = false;
                int currLine = tokens.get(i - 1).line;
                while (flag || tokens.get(i).line == currLine) {
                    if (tokens.get(i).type == GoLexer.L_PAREN)
                        flag = true;
                    else if (tokens.get(i).type == GoLexer.R_PAREN)
                        flag = false;
                    else
                    {
                        Instant instant = Instant.now();
                        long timeStampMillis = instant.toEpochMilli();
                        newNorm = "lib_" + token.text + Math.abs(timeStampMillis);
                    }

                    tokens.set(i, new TokenInfo(token.text, token.type, token.line, token.column, newNorm));
                    i++;
                }
                i--;
            }

            else if (token.type == GoLexer.IDENTIFIER) {
                newNorm = "id";

                // 2. До скобок без точки — вызов функции
                if (i + 1 < tokens.size() &&
                        tokens.get(i + 1).type == GoLexer.L_PAREN &&
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

            } else if (token.type == GoLexer.DECIMAL_LIT) {
                newNorm = "decimal_lit";
            } else if (token.type == GoLexer.FLOAT_LIT) {
                newNorm = "float_lit";
            } else if (token.type == GoLexer.RUNE_LIT) {
                newNorm = "rune_lit";
            } else if (token.type == GoLexer.RAW_STRING_LIT || token.type == GoLexer.INTERPRETED_STRING_LIT) {
                newNorm = "string_lit";
            } else if (token.type == GoLexer.NIL_LIT) {
                newNorm = "null_lit";
            } else if (token.type == GoLexer.BINARY_LIT) {
                newNorm = "boolean_lit";
            } else {
                continue;
            }

            tokens.set(i, new TokenInfo(token.text, token.type, token.line, token.column, newNorm));
        }

        return tokens;
    }
}

