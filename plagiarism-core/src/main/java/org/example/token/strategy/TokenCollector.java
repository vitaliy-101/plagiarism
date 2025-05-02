package org.example.token.strategy;

import org.example.token.TokenInfo;

import java.util.List;

public interface TokenCollector {

    List<TokenInfo> collectTokensFromFile(String path);

}
