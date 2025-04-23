package org.example.token;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import go.grammar.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoTokenCollector extends GoParserBaseListener {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, TokenInfo.Language.GO));
    }

    public static List<TokenInfo> collectTokensFromFile(String filename) throws IOException {
        CharStream input = CharStreams.fromReader(new FileReader(filename));

        GoLexer lexer = new GoLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        GoParser parser = new GoParser(tokenStream);
        ParseTree tree = parser.sourceFile();

        GoTokenCollector collector = new GoTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}

