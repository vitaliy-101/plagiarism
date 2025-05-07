package org.example.normalization.strategy;

import com.example.content.Language;
import org.example.normalization.CppNormalization;
import org.example.normalization.GoNormalization;
import org.example.normalization.JavaNormalization;
import org.example.normalization.PythonNormalization;
import org.example.token.*;

import java.util.List;
import java.util.Map;

public class NormalizationManager {
    private final static Map<Language, Normalization> NORMALIZATIONS = Map.of(
            Language.JAVA, new JavaNormalization(),
            Language.CPP, new CppNormalization(),
            Language.PY, new PythonNormalization(),
            Language.GO, new GoNormalization()
    );

    private Normalization chooseNormalization(Language language) {
        if (!NORMALIZATIONS.containsKey(language)) {
            throw new RuntimeException("Token normalization does not exists by language");
        }
        return NORMALIZATIONS.get(language);
    }

    public List<TokenInfo> normalizeTokens(Language language, List<TokenInfo> tokens) {
        return chooseNormalization(language).normalize(tokens);
    }
}
