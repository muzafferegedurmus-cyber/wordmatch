package wordmatch.service;

import java.text.Collator;
import java.util.*;
import wordmatch.domain.*;

/** Konsoldaki isteğe bağlı çalışma sırası: her sütun kendi diline göre sıralanır. */
public class AlphabeticalWordOrder implements WordOrder {
    @Override public List<Word> arrange(List<Word> words, Language language, Random random) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag(language.name().toLowerCase(Locale.ROOT)));
        List<Word> result = new ArrayList<>(words);
        result.sort(Comparator.comparing(word -> word.text(language), collator));
        return List.copyOf(result);
    }
}
