package org.example.token;

import go.grammar.GoLexer;
import go.grammar.GoParser;
import go.grammar.GoParserBaseListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.token.strategy.TokenCollector;

import java.util.ArrayList;
import java.util.List;

public class GoTokenCollector extends GoParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        if (token.getType() == GoLexer.EOF)
            return;
        tokens.add(new TokenInfo(
                token.getText(),
                token.getType(),
                token.getLine() - 1,
                token.getCharPositionInLine(),
                token.getText().length(),
                token.getText()
        ));
    }

    @Override
    public List<TokenInfo> collectTokensFromFile(String path) {
        GoLexer lexer = new GoLexer(CharStreams.fromString(path));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        GoParser parser = new GoParser(tokenStream);
        ParseTree tree = parser.sourceFile();

        GoTokenCollector collector = new GoTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}

