package org.example.normalization.strategy;

import org.example.token.TokenInfo;

import java.util.List;

public interface Normalization {

    List<TokenInfo> normalize(List<TokenInfo> tokens);
}
