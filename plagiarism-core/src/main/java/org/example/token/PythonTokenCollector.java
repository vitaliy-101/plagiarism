package org.example.token;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import python3.grammar.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PythonTokenCollector extends PythonParserBaseListener {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, TokenInfo.Language.PYTHON));
    }

    public static List<TokenInfo> collectTokensFromFile(String filename) throws IOException {
        CharStream input = CharStreams.fromReader(new FileReader(filename));

        PythonLexer lexer = new PythonLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        PythonParser parser = new PythonParser(tokenStream);
        ParseTree tree = parser.file_input();

        PythonTokenCollector collector = new PythonTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}
