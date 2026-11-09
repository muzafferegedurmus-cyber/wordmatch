package wordmatch.service;

import java.text.Collator;
import java.util.*;
import wordmatch.domain.*;

/** Optional console study order: sort each column using its own language. */
public class AlphabeticalWordOrder implements WordOrder {
    @Override public List<Word> arrange(List<Word> words, Language language, Random random) {
        Collator collator = Collator.getInstance(Locale.forLanguageTag(language.name().toLowerCase(Locale.ROOT)));
        List<Word> result = new ArrayList<>(words);
        result.sort(Comparator.comparing(word -> word.text(language), collator));
        return List.copyOf(result);
    }
}
