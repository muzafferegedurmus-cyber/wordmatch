package wordmatch.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import wordmatch.persistence.WordRepository;

class GameSessionTest {
    private List<Word> words(GameRules.Difficulty difficulty) throws Exception {
        return new WordRepository().choose(difficulty, Language.TR, Language.EN, new Random(42));
    }
    @Test void correctWrongAndRapidClicksAreScoredOnce() throws Exception {
        var words = words(GameRules.Difficulty.EASY);
        var game = new GameSession(words, GameRules.Difficulty.EASY);
        assertEquals(GameSession.Attempt.WRONG, game.attempt(words.get(0).id(), words.get(1).id()));
        assertEquals(-5, game.score()); assertEquals(1, game.mistakes());
        assertEquals(GameSession.Attempt.IGNORED, game.attempt(words.get(0).id(), words.get(1).id()));
        assertEquals(-5, game.score());
        game.finishFeedback();
        assertEquals(GameSession.Attempt.CORRECT, game.attempt(words.get(0).id(), words.get(0).id()));
        game.finishFeedback();
        assertEquals(GameSession.Attempt.IGNORED, game.attempt(words.get(0).id(), words.get(0).id()));
        assertEquals(5, game.score()); assertEquals(1, game.correct());
    }
    @Test void allLevelsCompleteAndRestartWithTheirOwnTime() throws Exception {
        for (var level : GameRules.Difficulty.values()) {
            var words = words(level); var game = new GameSession(words, level);
            for (Word word : words) { game.attempt(word.id(), word.id()); game.finishFeedback(); }
            assertEquals(GameSession.EndReason.COMPLETED, game.endReason());
            assertEquals(level.pairs * 10, game.score());
            game.tick(); assertEquals(level.seconds, game.secondsRemaining());
            var fresh = new GameSession(words, level);
            assertEquals(0, fresh.score()); assertEquals(level.seconds, fresh.secondsRemaining());
        }
    }
    @Test void timeUpAndAbandonStopFurtherChanges() throws Exception {
        var game = new GameSession(words(GameRules.Difficulty.HARD), GameRules.Difficulty.HARD);
        for (int i = 0; i < 40; i++) game.tick();
        assertEquals(0, game.secondsRemaining()); assertEquals(GameSession.EndReason.TIME_UP, game.endReason());
        var word = game.words().getFirst();
        assertEquals(GameSession.Attempt.IGNORED, game.attempt(word.id(), word.id()));
        var abandoned = new GameSession(words(GameRules.Difficulty.EASY), GameRules.Difficulty.EASY);
        abandoned.abandon(); abandoned.tick(); assertEquals(60, abandoned.secondsRemaining());
    }
    @Test void invalidObjectsCannotBeCreatedAndWordsAreImmutable() throws Exception {
        assertThrows(InvalidGameException.class, () -> new GameSession(List.of(), GameRules.Difficulty.EASY));
        var words = words(GameRules.Difficulty.EASY);
        assertThrows(InvalidGameException.class, () -> new GameSession(Collections.nCopies(4, words.getFirst()), GameRules.Difficulty.EASY));
        var game = new GameSession(words, GameRules.Difficulty.EASY);
        assertThrows(UnsupportedOperationException.class, () -> game.words().clear());
        assertThrows(UnsupportedOperationException.class, () -> words.getFirst().translations().clear());
        assertThrows(InvalidGameException.class, () -> new Word("x", GameRules.Difficulty.EASY, "food", List.of("elma")));
    }
}
