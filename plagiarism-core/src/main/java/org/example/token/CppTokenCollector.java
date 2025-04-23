package org.example.token;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import cpp14.grammar.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CppTokenCollector extends CPP14ParserBaseListener {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, TokenInfo.Language.CPP));
    }

    public static List<TokenInfo> collectTokensFromFile(String filename) throws IOException {
        CharStream input = CharStreams.fromReader(new FileReader(filename));

        CPP14Lexer lexer = new CPP14Lexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        CPP14Parser parser = new CPP14Parser(tokenStream);
        ParseTree tree = parser.translationUnit();

        CppTokenCollector collector = new CppTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}

