package org.example.token;

import com.example.content.Language;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import go.grammar.*;
import org.example.token.strategy.TokenCollector;

import java.util.ArrayList;
import java.util.List;

public class GoTokenCollector extends GoParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, Language.GO));
    }

    @Override
    public List<TokenInfo> collectTokensFromFile(String fileContent) {
        GoLexer lexer = new GoLexer(CharStreams.fromString(fileContent));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        GoParser parser = new GoParser(tokenStream);
        ParseTree tree = parser.sourceFile();

        GoTokenCollector collector = new GoTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}

