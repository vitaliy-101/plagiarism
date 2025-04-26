package org.example.token;

import com.example.content.Language;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import cpp14.grammar.*;
import org.example.token.strategy.TokenCollector;

import java.util.ArrayList;
import java.util.List;

public class CppTokenCollector extends CPP14ParserBaseListener implements TokenCollector {
    public List<TokenInfo> tokens = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String text = token.getText();
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        int type = token.getType();


        tokens.add(new TokenInfo(text, type, line, column, Language.CPP));
    }

    @Override
    public List<TokenInfo> collectTokensFromFile(String fileContent) {
        CPP14Lexer lexer = new CPP14Lexer(CharStreams.fromString(fileContent));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        CPP14Parser parser = new CPP14Parser(tokenStream);
        ParseTree tree = parser.translationUnit();

        CppTokenCollector collector = new CppTokenCollector();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(collector, tree);

        return collector.tokens;
    }
}

