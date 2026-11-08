package wordmatch.service;

import java.util.List;
import java.util.Random;
import wordmatch.domain.Language;
import wordmatch.domain.Word;

/** Strategy sözleşmesi: Kaynağı değiştirmeden bir sütunun kelime sırasını üretir. */
public interface WordOrder {
    List<Word> arrange(List<Word> words, Language language, Random random);
}
