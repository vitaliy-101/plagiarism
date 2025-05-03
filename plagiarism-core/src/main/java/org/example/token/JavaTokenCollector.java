package org.example.token;

import com.example.content.Language;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java8.grammar.*;
import org.example.token.strategy.TokenCollector;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaTokenCollector extends Java8ParserBaseListener implements TokenCollector {

    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        tokens.add(new TokenInfo(
                token.getText(),
                token.getType(),
                token.getLine(),
                token.getCharPositionInLine(),
                Language.JAVA
        ));
    }

    @Override
    public List<TokenInfo> collectTokensFromFile(String path) {
        // Чтение и лексер
        try {
            CharStream input = CharStreams.fromString(path);
            Java8Lexer lexer = new Java8Lexer(input);

            // Подавляем ошибки лексера
            lexer.removeErrorListeners();
            lexer.addErrorListener(new SilentErrorListener());

            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            // Сохраняем все лексемы независимо от успешности парсинга
            tokenStream.fill();
            List<Token> allTokens = tokenStream.getTokens();

            for (Token t : allTokens) {
                if (t.getType() != Token.EOF) {
                    // Если тип токена равен null, присваиваем дефолтный тип, например, -1
                    int tokenType = t.getType() == Token.INVALID_TYPE ? -1 : t.getType();
                    tokens.add(new TokenInfo(
                            t.getText(),
                            tokenType, // Используем проверенный тип
                            t.getLine(),
                            t.getCharPositionInLine(),
                            Language.JAVA
                    ));
                }
            }


            // Пытаемся построить AST, но даже если не получится — токены уже собраны
            Java8Parser parser = new Java8Parser(tokenStream);
            parser.removeErrorListeners();
            parser.addErrorListener(new SilentErrorListener());

            ParseTree tree = parser.compilationUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(this, tree);

            return tokens;
        } catch (Exception e) {
            System.out.println("LEXER EXP = " + e.getMessage());
            return new ArrayList<>();
        }

    }
}