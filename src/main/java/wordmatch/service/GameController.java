package wordmatch.service;

import java.util.*;
import wordmatch.domain.*;
import wordmatch.persistence.WordRepository;

/** Shared round-creation service for both interfaces; uses the selected ordering strategy. */
public class GameController {
    private final WordRepository repository;
    private final Random random;
    private WordOrder wordOrder;

    public GameController(WordRepository repository) {
        this(repository, new Random(), new ShuffledWordOrder());
    }
    public GameController(WordRepository repository, Random random, WordOrder wordOrder) {
        this.repository = Objects.requireNonNull(repository);
        this.random = Objects.requireNonNull(random);
        setWordOrder(wordOrder);
    }
    public void setWordOrder(WordOrder wordOrder) { this.wordOrder = Objects.requireNonNull(wordOrder); }
    public GameSession startRound(GameRules.Difficulty difficulty, Language known, Language practice) {
        if (difficulty == null || known == null || practice == null || known == practice) {
            throw new InvalidGameException("İki farklı dil ve geçerli bir zorluk seçmelisin.");
        }
        return new GameSession(repository.choose(difficulty, known, practice, random), difficulty);
    }
    public List<Word> column(GameSession session, Language language) {
        return wordOrder.arrange(session.words(), language, random);
    }
}
