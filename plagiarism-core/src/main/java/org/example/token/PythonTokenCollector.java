package org.example.token;

import com.example.content.Language;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.example.token.strategy.TokenCollector;
import python3.grammar.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PythonTokenCollector extends PythonParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, Language.PY));
    }

    @Override
    public List<TokenInfo> collectTokensFromFile(String path) {
        try {
            PythonLexer lexer = new PythonLexer(CharStreams.fromPath(Paths.get(path)));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            PythonParser parser = new PythonParser(tokenStream);
            ParseTree tree = parser.file_input();

            PythonTokenCollector collector = new PythonTokenCollector();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(collector, tree);

            return collector.tokens;
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
