package org.example.token;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.token.strategy.TokenCollector;
import python3.grammar.PythonLexer;
import python3.grammar.PythonParser;
import python3.grammar.PythonParserBaseListener;

import java.util.ArrayList;
import java.util.List;

public class PythonTokenCollector extends PythonParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        if (token.getType() == PythonLexer.EOF)
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
        PythonLexer lexer = new PythonLexer(CharStreams.fromString(path));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        PythonParser parser = new PythonParser(tokenStream);
        ParseTree tree = parser.file_input();

        PythonTokenCollector collector = new PythonTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}
