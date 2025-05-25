package org.example.token;

import cpp14.grammar.CPP14Lexer;
import cpp14.grammar.CPP14Parser;
import cpp14.grammar.CPP14ParserBaseListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.token.strategy.TokenCollector;

import java.util.ArrayList;
import java.util.List;

public class CppTokenCollector extends CPP14ParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        if (token.getType() == CPP14Lexer.EOF)
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
        try {
            CharStream input = CharStreams.fromString(path);
            CPP14Lexer lexer = new CPP14Lexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            CPP14Parser parser = new CPP14Parser(tokenStream);
            ParseTree tree = parser.translationUnit();

            CppTokenCollector collector = new CppTokenCollector();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(collector, tree);

            return collector.tokens;
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return List.of(new TokenInfo("Not supported", 0, 0,
                    0, 0, "Not supported"));
        }
    }
}

