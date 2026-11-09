package wordmatch.service;

import java.util.*;
import wordmatch.domain.*;

/** Standard play: shuffle each column independently. */
public class ShuffledWordOrder implements WordOrder {
    @Override public List<Word> arrange(List<Word> words, Language language, Random random) {
        List<Word> result = new ArrayList<>(words);
        Collections.shuffle(result, random);
        return List.copyOf(result);
    }
}
