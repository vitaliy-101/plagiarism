package org.example.token;

import java8.grammar.JavaLexer;
import java8.grammar.JavaParser;
import java8.grammar.JavaParserBaseListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.token.strategy.TokenCollector;

import java.util.ArrayList;
import java.util.List;

public class JavaTokenCollector extends JavaParserBaseListener implements TokenCollector {

    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        if (token.getType() == JavaLexer.EOF)
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
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(path));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        JavaParser parser = new JavaParser(tokenStream);
        ParseTree tree = parser.compilationUnit();

        JavaTokenCollector collector = new JavaTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}