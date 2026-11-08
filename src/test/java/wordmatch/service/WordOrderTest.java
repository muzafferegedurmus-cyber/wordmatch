package wordmatch.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.text.Collator;
import wordmatch.domain.*;
import wordmatch.persistence.WordRepository;

class WordOrderTest {
    @Test void everyLanguagePairHasUnambiguousRoundsAtEveryLevel() throws Exception {
        var repository = new WordRepository();
        for (var difficulty : GameRules.Difficulty.values()) {
            assertTrue(repository.count(difficulty) >= 25);
            for (var a : Language.values()) for (var b : Language.values()) if (a != b) {
                for (int seed = 0; seed < 20; seed++) {
                    var words = repository.choose(difficulty, a, b, new Random(seed));
                    assertEquals(difficulty.pairs, words.size());
                    assertEquals(words.size(), words.stream().map(w -> w.text(a)).distinct().count());
                    assertEquals(words.size(), words.stream().map(w -> w.text(b)).distinct().count());
                }
            }
        }
    }
    @Test void changingStrategyChangesBehaviorThroughTheSameInterface() throws Exception {
        var repository = new WordRepository(); var controller = new GameController(repository, new Random(5), new ShuffledWordOrder());
        var round = controller.startRound(GameRules.Difficulty.HARD, Language.TR, Language.EN);
        var shuffled = controller.column(round, Language.TR);
        controller.setWordOrder(new AlphabeticalWordOrder());
        var sorted = controller.column(round, Language.TR);
        var collator = Collator.getInstance(Locale.forLanguageTag("tr"));
        for (int i = 1; i < sorted.size(); i++) assertTrue(collator.compare(sorted.get(i - 1).text(Language.TR), sorted.get(i).text(Language.TR)) <= 0);
        assertEquals(new HashSet<>(shuffled), new HashSet<>(sorted));
        assertNotEquals(shuffled, sorted);
        assertThrows(UnsupportedOperationException.class, () -> sorted.clear());
    }
    @Test void domainRuleRejectsIdenticalLanguages() throws Exception {
        var controller = new GameController(new WordRepository());
        assertThrows(InvalidGameException.class, () -> controller.startRound(GameRules.Difficulty.EASY, Language.TR, Language.TR));
    }
}
