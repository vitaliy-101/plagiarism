package org.example.token.strategy;

import com.example.content.Language;
import org.example.token.*;

import java.util.List;
import java.util.Map;

public class TokenCollectorManager {
    private final static Map<Language, TokenCollector> TOKEN_COLLECTORS = Map.of(
            Language.JAVA, new JavaTokenCollector(),
            Language.CPP, new CppTokenCollector(),
            Language.PY, new PythonTokenCollector(),
            Language.GO, new GoTokenCollector()
    );

    private TokenCollector chooseCollector(Language language) {
        if (!TOKEN_COLLECTORS.containsKey(language)) {
            throw new RuntimeException("Token collector does not exists by language");
        }
        return TOKEN_COLLECTORS.get(language);
    }

    public List<TokenInfo> collectTokensFromFile(Language language, String fileContent) {
        return chooseCollector(language).collectTokensFromFile(fileContent);
    }
}
