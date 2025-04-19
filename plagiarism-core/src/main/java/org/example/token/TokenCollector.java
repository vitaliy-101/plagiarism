package org.example.token;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.example.java.Java8Lexer;
import org.example.java.Java8Parser;
import org.example.java.Java8ParserBaseListener;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenCollector extends Java8ParserBaseListener {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column));
    }

    public static List<TokenInfo> collectTokensFromFile(String filename) throws IOException {
        CharStream input = CharStreams.fromReader(new FileReader(filename));

        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        Java8Parser parser = new Java8Parser(tokenStream);
        ParseTree tree = parser.compilationUnit();

        TokenCollector collector = new TokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}
