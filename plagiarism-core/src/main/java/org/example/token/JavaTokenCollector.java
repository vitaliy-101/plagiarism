package org.example.token;

import com.example.content.Language;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java8.grammar.*;
import org.example.token.strategy.TokenCollector;

import java.util.ArrayList;
import java.util.List;

public class JavaTokenCollector extends Java8ParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, Language.JAVA));
    }

    @Override
    public List<TokenInfo> collectTokensFromFile(String fileContent) {
        Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(fileContent));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        Java8Parser parser = new Java8Parser(tokenStream);
        ParseTree tree = parser.compilationUnit();

        JavaTokenCollector collector = new JavaTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}
