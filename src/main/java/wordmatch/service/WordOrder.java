package wordmatch.service;

import java.util.List;
import java.util.Random;
import wordmatch.domain.Language;
import wordmatch.domain.Word;

/** Strategy contract: arrange a column without modifying the source list. */
public interface WordOrder {
    List<Word> arrange(List<Word> words, Language language, Random random);
}
